package Models.Utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Utility class for memoization.
 * Memoization is an optimization technique that stores the results of expensive function calls
 * and returns the cached result when the same inputs occur again.
 */
public class Memoizer {

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
        return input -> cache.computeIfAbsent(input, function);
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
        return input -> {
            K key = keyMapper.apply(input);
            return cache.computeIfAbsent(key, k -> function.apply(input));
        };
    }
}
