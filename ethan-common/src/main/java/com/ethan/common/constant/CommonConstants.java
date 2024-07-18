package com.ethan.common.constant;

import java.util.regex.Pattern;

/**
 * Common constants.
 *
 * @author Huang Z.Y.
 */
public interface CommonConstants {

    String ETHAN = "ethan";
    String PATH_SEPARATOR = "/";
    String TIMEOUT_KEY = "timeout";
    String PROVIDERS_CATEGORY = "providers";
    String SESSION_KEY = "session";
    String DYNAMIC_KEY = "dynamic";
    String ANY_VALUE = "*";
    String GROUP_KEY = "group";
    String INTERFACE_KEY = "interface";
    String VERSION_KEY = "version";
    String PATH_KEY = "path";
    String SIDE_KEY = "side";
    String CATEGORY_KEY = "category";
    String CONSUMERS_CATEGORY = "consumers";
    String CONSUMER_SIDE = "consumer";
    String LOCALHOST_VALUE = "127.0.0.1";
    Pattern COMMA_SPLIT_PATTERN = Pattern.compile("\\s*[,]+\\s*");
    int MAX_PROXY_COUNT = 65535;
    String NATIVE_STUB = "nativestub";
    String ETHAN_PROTOCOL = "ethan";
    String BIND_IP_KEY = "bind.ip";
    String COMPRESS_KEY = "message.compress";
    String DECOMPRESS_KEY = "message.decompress";
    String SERIALIZATION_KEY = "message.serialize";
    String BIND_PORT_KEY = "bind.port";
    String NETTY_EPOLL_ENABLE_KEY = "netty.epoll.enable";
    String KEEP_ALIVE_KEY = "keep.alive";
    String OS_NAME_KEY = "os.name";
    String OS_LINUX_PREFIX = "linux";
    String CONNECT_TIMEOUT_KEY = "connect.timeout";
    String HEARTBEAT_KEY = "heartbeat";
    String INTERFACES = "interfaces";
    String INTERFACE = "interface";
    String PROVIDER_ASYNC_KEY = "PROVIDER_ASYNC";
    String ASYNC_KEY = "async";
    String THREAD_NAME_KEY = "threadname";
    String SERVER_THREAD_POOL_NAME = "EthanServerHandler";
    String LOCAL_PROTOCOL = "native";
    String REMOTE_PROTOCOL = "remote";
    String PROTOCOL_VERSION = "1.0.0";
    String HEARTBEAT_EVENT = null;

    /*=======================================================================================
     * Default configuration
     *======================================================================================*/
    String DEFAULT_SERIALIZATION = "fastjson2";
    String DEFAULT_COMPRESS = "gzip";
    int DEFAULT_HEARTBEAT = 60 * 1000;
    String HEARTBEAT_CONFIG_KEY = "ethan.protocol.default-heartbeat";
    String HEARTBEAT_TIMEOUT_KEY = "heartbeat.timeout";
    String CLOSE_TIMEOUT_KEY = "close.timeout";
    String CLOSE_TIMEOUT_CONFIG_KEY = "ethan.protocol.default-close-timeout";
    int DEFAULT_IO_THREADS = Math.min(Runtime.getRuntime().availableProcessors() + 1, 32);
    int DEFAULT_CONNECT_TIMEOUT = 3000;
    String DEFAULT_CATEGORY = PROVIDERS_CATEGORY;
    int DEFAULT_TIMEOUT = 1000;
    String DEFAULT_EXTENSION_PATH = "META-INF/ethan/";
    String EXTENSION_INTERNAL_PATH = "META-INF/ethan/internal/";
    String EXTENSION_SERVICE_PATH = "META-INF/ethan/internal/service/";
    int DEFAULT_CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors();
    int DEFAULT_KEEP_ALIVE_TIME = 60;
    String DEFAULT_PROXY = "javassist";


}
