package com.ethan.rpc.protocol;

import com.ethan.common.extension.Activate;
import com.ethan.model.ApplicationModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Huang Z.Y.
 */
public class FilterChain {

    /**
     * Cache of loaded filters by group.
     */
    private static final Map<String, List<Filter>> FILTER_CACHE = new ConcurrentHashMap<>();

    /**
     * Load filters by group and cache the result.
     *
     * @param group The group for which filters should be activated (e.g. "provider", "consumer").
     * @return A list of filters sorted by order.
     */
    public static List<Filter> loadFilters(String group) {
        // Return from cache if already loaded
        if (FILTER_CACHE.containsKey(group)) {
            return FILTER_CACHE.get(group);
        }

        List<Filter> filters = new ArrayList<>();

        // Simulate scanning and finding all filter classes dynamically
        for (Filter filter : findAllFilters()) {
            Activate activate = filter.getClass().getAnnotation(Activate.class);

            // Check if the filter is annotated with @Activate and matches the group
            if (activate != null && matchesGroup(activate, group)) {
                filters.add(filter);
            }
        }

        // Sort the filters based on the order in @Activate
        filters.sort((f1, f2) -> {
            Activate a1 = f1.getClass().getAnnotation(Activate.class);
            Activate a2 = f2.getClass().getAnnotation(Activate.class);
            return Integer.compare(a1.order(), a2.order());
        });

        // Cache the filters for this group
        FILTER_CACHE.put(group, filters);

        return filters;
    }

    /**
     * Check if the filter belongs to the specified group.
     *
     * @param activate The Activate annotation from the filter.
     * @param group    The group to match (e.g. "provider", "consumer").
     * @return true if the group matches, false otherwise.
     */
    private static boolean matchesGroup(Activate activate, String group) {
        for (String g : activate.group()) {
            if (g.equals(group)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Simulates finding all available filters (in a real implementation this would dynamically scan classes).
     *
     * @return A list of all available filters.
     */
    private static List<Filter> findAllFilters() {
        // Normally, this would scan the classpath for filters with @Activate
        return ApplicationModel.defaultModel()
                .getExtensionLoader(Filter.class)
                .getSupportedExtensionInstances()
                .stream()
                .toList();
    }

}
    