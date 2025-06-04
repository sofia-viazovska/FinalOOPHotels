package Models.Utils;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utility class for memoization.
 * Memoization is an optimization technique that stores the results of expensive function calls
 * and returns the cached result when the same inputs occur again.
 */
public class Memoizer {
    /**
     * Enum defining different cache clearing strategies.
     */
    public enum CacheStrategy {
        ALL,       // Clear all caches
        LRU,       // Least Recently Used
        LFU,       // Least Frequently Used
        TIME_BASED // Time-Based (clear oldest entries)
    }

    // Store cache metadata for tracking usage
    private static final Map<String, CacheMetadata> cacheRegistry = new ConcurrentHashMap<>();

    /**
     * Class to track cache usage information
     */
    private static class CacheMetadata {
        private final Map<?, ?> cache;
        private final Map<Object, CacheEntryMetadata> entryMetadata;
        private final Instant creationTime;

        public CacheMetadata(Map<?, ?> cache) {
            this.cache = cache;
            this.entryMetadata = new ConcurrentHashMap<>();
            this.creationTime = Instant.now();
        }

        public Map<?, ?> getCache() {
            return cache;
        }

        public Map<Object, CacheEntryMetadata> getEntryMetadata() {
            return entryMetadata;
        }

        public Instant getCreationTime() {
            return creationTime;
        }

        public void recordAccess(Object key) {
            entryMetadata.computeIfAbsent(key, k -> new CacheEntryMetadata())
                         .recordAccess();
        }
    }

    /**
     * Class to track individual cache entry usage
     */
    private static class CacheEntryMetadata {
        private Instant lastAccessTime;
        private int accessCount;
        private final Instant creationTime;

        public CacheEntryMetadata() {
            this.lastAccessTime = Instant.now();
            this.creationTime = Instant.now();
            this.accessCount = 1;
        }

        public void recordAccess() {
            this.lastAccessTime = Instant.now();
            this.accessCount++;
        }

        public Instant getLastAccessTime() {
            return lastAccessTime;
        }

        public int getAccessCount() {
            return accessCount;
        }

        public Instant getCreationTime() {
            return creationTime;
        }
    }

    /**
     * Creates a memoized version of the given function.
     *
     * @param <T> the type of the input to the function
     * @param <R> the type of the result of the function
     * @param function the function to memoize
     * @return a memoized version of the function
     */
    public static <T, R> Function<T, R> memoize(Function<T, R> function) {
        Map<T, R> cache = new ConcurrentHashMap<>();
        // Register this cache for potential cleaning
        String cacheId = "cache_" + System.identityHashCode(cache);
        cacheRegistry.put(cacheId, new CacheMetadata(cache));

        return input -> {
            R result = cache.get(input);
            if (result != null) {
                // Record cache hit
                cacheRegistry.get(cacheId).recordAccess(input);
                return result;
            }

            R value = function.apply(input);
            cache.put(input, value);
            // Record new entry
            cacheRegistry.get(cacheId).recordAccess(input);
            return value;
        };
    }

    /**
     * Creates a memoized version of the given function with a custom cache.
     * This is useful when the input type doesn't have proper equals/hashCode implementations.
     *
     * @param <T> the type of the input to the function
     * @param <K> the type of the key used for caching
     * @param <R> the type of the result of the function
     * @param function the function to memoize
     * @param keyMapper a function that maps the input to a cache key
     * @return a memoized version of the function
     */
    public static <T, K, R> Function<T, R> memoizeWithKey(Function<T, R> function, Function<T, K> keyMapper) {
        Map<K, R> cache = new ConcurrentHashMap<>();
        // Register this cache for potential cleaning
        String cacheId = "cache_" + System.identityHashCode(cache);
        cacheRegistry.put(cacheId, new CacheMetadata(cache));

        return input -> {
            K key = keyMapper.apply(input);
            R result = cache.get(key);
            if (result != null) {
                // Record cache hit
                cacheRegistry.get(cacheId).recordAccess(key);
                return result;
            }

            R value = function.apply(input);
            cache.put(key, value);
            // Record new entry
            cacheRegistry.get(cacheId).recordAccess(key);
            return value;
        };
    }

    /**
     * Clears all caches created by memoize methods.
     * This should be called when the system state changes significantly.
     */
    public static void clearAllCaches() {
        for (CacheMetadata metadata : cacheRegistry.values()) {
            metadata.getCache().clear();
            metadata.getEntryMetadata().clear();
        }
    }

    /**
     * Clears caches using the specified strategy.
     *
     * @param strategy the cache clearing strategy to use
     * @param percentage the percentage of entries to clear (0-100)
     */
    public static void clearCaches(CacheStrategy strategy, int percentage) {
        if (strategy == CacheStrategy.ALL) {
            clearAllCaches();
            return;
        }

        for (CacheMetadata metadata : cacheRegistry.values()) {
            Map<?, ?> cache = metadata.getCache();
            Map<Object, CacheEntryMetadata> entryMetadata = metadata.getEntryMetadata();

            if (cache.isEmpty()) {
                continue;
            }

            int entriesToRemove = Math.max(1, (int) Math.ceil(cache.size() * percentage / 100.0));

            List<Object> keysToRemove;

            switch (strategy) {
                case LRU:
                    // Sort by last access time (oldest first)
                    keysToRemove = entryMetadata.entrySet().stream()
                        .sorted(Comparator.comparing(e -> e.getValue().getLastAccessTime()))
                        .limit(entriesToRemove)
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());
                    break;

                case LFU:
                    // Sort by access count (least frequent first)
                    keysToRemove = entryMetadata.entrySet().stream()
                        .sorted(Comparator.comparing(e -> e.getValue().getAccessCount()))
                        .limit(entriesToRemove)
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());
                    break;

                case TIME_BASED:
                    // Sort by creation time (oldest first)
                    keysToRemove = entryMetadata.entrySet().stream()
                        .sorted(Comparator.comparing(e -> e.getValue().getCreationTime()))
                        .limit(entriesToRemove)
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());
                    break;

                default:
                    continue;
            }

            // Remove the selected entries from the cache
            for (Object key : keysToRemove) {
                cache.remove(key);
                entryMetadata.remove(key);
            }
        }
    }

    /**
     * Checks if the number of bookings exceeds the threshold and clears caches if needed.
     *
     * @param bookingsCount the current number of bookings in the system
     * @param threshold the threshold at which caches should be cleared
     */
    public static void checkAndClearCaches(int bookingsCount, int threshold) {
        if (bookingsCount > threshold) {
            // Use LRU strategy by default, clearing 30% of entries
            clearCaches(CacheStrategy.LRU, 30);
        }
    }

    /**
     * Checks if the number of bookings exceeds the threshold and clears caches using the specified strategy.
     *
     * @param bookingsCount the current number of bookings in the system
     * @param threshold the threshold at which caches should be cleared
     * @param strategy the cache clearing strategy to use
     * @param percentage the percentage of entries to clear (0-100)
     */
    public static void checkAndClearCaches(int bookingsCount, int threshold, CacheStrategy strategy, int percentage) {
        if (bookingsCount > threshold) {
            clearCaches(strategy, percentage);
        }
    }
}
