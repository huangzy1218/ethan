package com.ethan.registry.nacos;

import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.ListView;
import com.alibaba.nacos.client.naming.NacosNamingService;
import com.ethan.common.RpcException;
import com.ethan.common.URL;
import com.ethan.registry.support.AbstractRegistry;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.ethan.common.constant.CommonConstants.*;
import static com.ethan.registry.nacos.NacosServiceName.NAME_SEPARATOR;

/**
 * @author Huang Z.Y.
 */
@Slf4j
public class NacosRegistry extends AbstractRegistry {

    private final NacosNamingService nacosNamingService;

    private URL url;
    private static final long LOOKUP_INTERVAL = Long.getLong("nacos.service.names.lookup.interval", 30);


    private static final List<String> ALL_SUPPORTED_CATEGORIES =
            Arrays.asList(PROVIDERS_CATEGORY, CONSUMERS_CATEGORY, ROUTERS_CATEGORY, CONFIGURATORS_CATEGORY);
    private volatile ScheduledExecutorService scheduledExecutorService;
    private static final int PAGINATION_SIZE = Integer.getInteger("nacos.service.names.pagination.size", 100);
    private static final String SERVICE_NAME_SEPARATOR = System.getProperty("nacos.service.name.separator", ":");

    public NacosRegistry(URL url, NacosNamingService nacosNamingService) {
        this.url = url;
        this.nacosNamingService = nacosNamingService;
    }


    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public void init() {
        // nop
    }

    @Override
    public void register(URL url) {
        String serviceName = getServiceName(url);
        Instance instance = createInstance(url);
        try {
            nacosNamingService.registerInstance(serviceName, getUrl().getGroup(Constants.DEFAULT_GROUP), instance);
        } catch (NacosException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void unregister(URL url) {
        String serviceName = getServiceName(url);
        Instance instance = createInstance(url);
        try {
            nacosNamingService.deregisterInstance(serviceName, getUrl().getGroup(Constants.DEFAULT_GROUP), instance.getIp(), instance.getPort());
        } catch (NacosException cause) {
            throw new RpcException(
                    "Failed to unregister " + url + " to nacos " + getUrl() + ", cause: " + cause.getMessage(), cause);
        }
    }

    @Override
    public List<URL> lookup(URL url) {
        if (url == null) {
            throw new IllegalArgumentException("lookup url == null");
        }
        try {
            List<URL> urls = new LinkedList<>();
            Set<String> serviceNames = getServiceNames(url);
            for (String serviceName : serviceNames) {
                List<Instance> instances =
                        nacosNamingService.getAllInstances(serviceName, getUrl().getGroup(Constants.DEFAULT_GROUP));
                urls.addAll(buildURLs(url, instances));
            }
            return urls;
        } catch (Exception cause) {
            throw new RpcException(
                    "Failed to lookup " + url + " from nacos " + getUrl() + ", cause: " + cause.getMessage(), cause);
        }
    }

    private List<URL> buildURLs(URL consumerURL, Collection<Instance> instances) {
        List<URL> urls = new LinkedList<>();
        if (instances != null && !instances.isEmpty()) {
            for (Instance instance : instances) {
                URL url = buildURL(consumerURL, instance);
                urls.add(url);
            }
        }
        return urls;
    }

    private URL buildURL(URL consumerURL, Instance instance) {
        Map<String, String> metadata = instance.getMetadata();
        String protocol = metadata.get(PROTOCOL_KEY);
        String path = metadata.get(PATH_KEY);
        return new URL(protocol, instance.getIp(), instance.getPort(), path);
    }

    private Set<String> getServiceNames(URL url) {
        NacosServiceName serviceName = createServiceName(url);

        final Set<String> serviceNames;

        if (serviceName.isConcrete()) { // is the concrete service name
            serviceNames = new LinkedHashSet<>();
            serviceNames.add(serviceName.toString());
        } else {
            serviceNames = filterServiceNames(serviceName);
        }

        return serviceNames;
    }

    private Set<String> filterServiceNames(NacosServiceName serviceName) {
        try {
            Set<String> serviceNames = new LinkedHashSet<>();
            serviceNames.addAll(
                    nacosNamingService
                            .getServicesOfServer(1, Integer.MAX_VALUE, getUrl().getGroup(Constants.DEFAULT_GROUP))
                            .getData()
                            .stream()
                            .filter(this::isConformRules)
                            .map(NacosServiceName::new)
                            .filter(serviceName::isCompatible)
                            .map(NacosServiceName::toString)
                            .collect(Collectors.toList()));
            return serviceNames;
        } catch (Throwable cause) {
            throw new RpcException(
                    "Failed to filter serviceName from nacos, url: " + getUrl() + ", serviceName: " + serviceName
                            + ", cause: " + cause.getMessage(),
                    cause);
        }
    }

    private boolean isConformRules(String serviceName) {
        return serviceName.split(NAME_SEPARATOR, -1).length == 4;
    }

    private NacosServiceName createServiceName(URL url) {
        return new NacosServiceName(url);
    }

    private void scheduleServiceNamesLookup(final URL url) {
        if (scheduledExecutorService == null) {
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            scheduledExecutorService.scheduleAtFixedRate(
                    () -> {
                        Set<String> serviceNames = getAllServiceNames();
                        filterData(serviceNames, serviceName -> {
                            boolean accepted = false;
                            for (String category : ALL_SUPPORTED_CATEGORIES) {
                                String prefix = category + SERVICE_NAME_SEPARATOR;
                                if (serviceName != null && serviceName.startsWith(prefix)) {
                                    accepted = true;
                                    break;
                                }
                            }
                            return accepted;
                        });
                    },
                    LOOKUP_INTERVAL,
                    LOOKUP_INTERVAL,
                    TimeUnit.SECONDS);
        }
    }

    private <T> void filterData(Collection<T> collection, NacosDataFilter<T> filter) {
        // remove if not accept
        collection.removeIf(data -> !filter.accept(data));
    }

    private interface NacosDataFilter<T> {

        /**
         * Tests whether or not the specified data should be accepted.
         *
         * @param data The data to be tested
         * @return <code>true</code> if and only if <code>data</code>
         * should be accepted
         */
        boolean accept(T data);
    }

    private Set<String> getAllServiceNames() {
        try {
            final Set<String> serviceNames = new LinkedHashSet<>();
            int pageIndex = 1;
            ListView<String> listView = nacosNamingService.getServicesOfServer(
                    pageIndex, PAGINATION_SIZE, getUrl().getGroup(Constants.DEFAULT_GROUP));
            // First page data
            List<String> firstPageData = listView.getData();
            // Append first page into list
            serviceNames.addAll(firstPageData);
            // the total count
            int count = listView.getCount();
            // the number of pages
            int pageNumbers = count / PAGINATION_SIZE;
            int remainder = count % PAGINATION_SIZE;
            // remain
            if (remainder > 0) {
                pageNumbers += 1;
            }
            // If more than 1 page
            while (pageIndex < pageNumbers) {
                listView = nacosNamingService.getServicesOfServer(
                        ++pageIndex, PAGINATION_SIZE, getUrl().getGroup(Constants.DEFAULT_GROUP));
                serviceNames.addAll(listView.getData());
            }
            return serviceNames;
        } catch (Throwable cause) {
            throw new RpcException(
                    "Failed to get all serviceName from nacos, url: " + getUrl() + ", cause: " + cause.getMessage(),
                    cause);
        }
    }

    private boolean isAdminProtocol(URL url) {
        return ADMIN_PROTOCOL.equals(url.getProtocol());
    }

    private String getServiceName(URL url) {
        return getServiceName(url, url.getCategory(DEFAULT_CATEGORY));
    }

    private String getServiceName(URL url, String category) {
        return category + SERVICE_NAME_SEPARATOR + url.getServiceInterface();
    }

    private Instance createInstance(URL url) {
        String category = url.getCategory(DEFAULT_CATEGORY);
        URL newURL = url.addParameter(CATEGORY_KEY, category);
        newURL = newURL.addParameter(PROTOCOL_KEY, url.getProtocol());
        newURL = newURL.addParameter(PATH_KEY, url.getPath());
        String ip = url.getHost();
        int port = url.getPort();
        Instance instance = new Instance();
        instance.setIp(ip);
        instance.setPort(port);
        instance.setMetadata(new HashMap<>(newURL.getParameters()));
        return instance;
    }

}
    