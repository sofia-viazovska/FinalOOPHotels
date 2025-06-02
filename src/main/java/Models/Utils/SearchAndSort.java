package Models.Utils;

import Models.Hotel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility class for searching and sorting operations.
 * Implements merge sort and binary search algorithms.
 */
public class SearchAndSort {

    /**
     * Cache for memoized merge sort results.
     * This map stores previously computed sort results to avoid redundant computation.
     */
    private static final Map<Integer, List<?>> MERGE_SORT_CACHE = new ConcurrentHashMap<>();

    /**
     * Input class for the memoized merge sort function.
     * This class encapsulates the input parameters and provides proper equals and hashCode methods.
     */
    private static class MergeSortInput<T> {
        private final List<T> list;
        private final Comparator<T> comparator;

        public MergeSortInput(List<T> list, Comparator<T> comparator) {
            this.list = list;
            this.comparator = comparator;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MergeSortInput<?> that = (MergeSortInput<?>) o;
            return Objects.equals(list, that.list) &&
                   Objects.equals(comparator, that.comparator);
        }

        @Override
        public int hashCode() {
            return Objects.hash(list, comparator);
        }
    }

    /**
     * Sorts a list using a merge sort algorithm with memoization.
     * This public method uses caching for better performance.
     *
     * @param <T> the type of elements in the list
     * @param list the list to be sorted
     * @param comparator the comparator to determine the order of the list
     * @return a new sorted list
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> mergeSort(List<T> list, Comparator<T> comparator) {
        // Create a key for the cache based on the list and comparator
        MergeSortInput<T> input = new MergeSortInput<>(list, comparator);
        int cacheKey = input.hashCode();

        // Check if the result is already in the cache
        if (MERGE_SORT_CACHE.containsKey(cacheKey)) {
            return (List<T>) MERGE_SORT_CACHE.get(cacheKey);
        }

        // If not in cache, compute the result and store it
        List<T> result = mergeSortImpl(list, comparator);
        MERGE_SORT_CACHE.put(cacheKey, result);

        return result;
    }

    /**
     * Implementation of the merge sort algorithm.
     * This is the actual implementation that gets memoized.
     *
     * @param <T> the type of elements in the list
     * @param list the list to be sorted
     * @param comparator the comparator to determine the order of the list
     * @return a new sorted list
     */
    private static <T> List<T> mergeSortImpl(List<T> list, Comparator<T> comparator) {
        // Base case: lists of size 0 or 1 are already sorted
        if (list.size() <= 1) {
            return new ArrayList<>(list);
        }

        // Divide the list into two halves
        int middle = list.size() / 2;
        // Create a copy of the left half
        List<T> left = new ArrayList<>(list.subList(0, middle));
        // Create a copy of the right half
        List<T> right = new ArrayList<>(list.subList(middle, list.size()));

        // Recursively sort both halves
        left = mergeSortImpl(left, comparator);
        right = mergeSortImpl(right, comparator);

        // Merge the sorted halves
        return merge(left, right, comparator);
    }

    /**
     * Merges two sorted lists into a single sorted list.
     *
     * @param <T> the type of elements in the lists
     * @param left the first sorted list
     * @param right the second sorted list
     * @param comparator the comparator to determine the order of the merged list
     * @return a new merged and sorted list
     */
    private static <T> List<T> merge(List<T> left, List<T> right, Comparator<T> comparator) {
        // Create a new list to hold the merged result
        List<T> result = new ArrayList<>();
        // Initialize indices for traversing both lists
        int leftIndex = 0;
        int rightIndex = 0;

        // Compare elements from both lists and add the smaller one to the result
        while (leftIndex < left.size() && rightIndex < right.size()) {
            // Compare current elements from both lists
            if (comparator.compare(left.get(leftIndex), right.get(rightIndex)) <= 0) {
                // If the left element is smaller or equal, add it to the result
                result.add(left.get(leftIndex));
                leftIndex++; // Move to the next element in the left list
            } else {
                // If the right element is smaller, add it to the result
                result.add(right.get(rightIndex));
                rightIndex++; // Move to the next element in the right list
            }
        }

        // Add any remaining elements from the left list (if any)
        result.addAll(left.subList(leftIndex, left.size()));
        // Add any remaining elements from the right list (if any)
        result.addAll(right.subList(rightIndex, right.size()));

        return result;
    }

    /**
     * Performs a binary search on a sorted list.
     *
     * @param <T> the type of elements in the list
     * @param sortedList the sorted list to search in
     * @param key the element to search for
     * @param comparator the comparator to determine the order of the list
     * @return the index of the key if found, otherwise -1
     */
    public static <T> int binarySearch(List<T> sortedList, T key, Comparator<T> comparator) {
        // Initialize search boundaries
        int low = 0; // Start of search range
        int high = sortedList.size() - 1; // End of search range

        // Continue searching while the range is valid
        while (low <= high) {
            // Calculate the middle index
            int mid = (low + high) / 2;
            // Get the middle element
            T midVal = sortedList.get(mid);
            // Compare the middle element with the key
            int cmp = comparator.compare(midVal, key);

            if (cmp < 0) {
                // If the middle element is less than the key, search in the right half
                low = mid + 1;
            } else if (cmp > 0) {
                // If the middle element is greater than the key, search in the left half
                high = mid - 1;
            } else {
                // Key found at middle index
                return mid; // key found
            }
        }

        // Key not found in the list
        return -1; // key not found
    }

    /**
     * Sorts a list of hotels by rating (descending).
     *
     * @param hotels the list of hotels to be sorted
     * @return a new sorted list of hotels
     */
    public static List<Hotel> sortHotelsByRating(List<Hotel> hotels) {
        // Use merge sort with a comparator that compares hotels by rating in descending order
        // Note: h2.getRating() comes before h1.getRating() to sort in descending order
        return mergeSort(hotels, (h1, h2) -> Integer.compare(h2.getRating(), h1.getRating()));
    }

    /**
     * Sorts a list of hotels by name (ascending).
     *
     * @param hotels the list of hotels to be sorted
     * @return a new sorted list of hotels
     */
    public static List<Hotel> sortHotelsByName(List<Hotel> hotels) {
        // Use merge sort with a comparator that compares hotels by name alphabetically
        return mergeSort(hotels, (h1, h2) -> h1.getName().compareTo(h2.getName()));
    }

    /**
     * Searches for a hotel by name using binary search.
     * The list must be sorted by name before calling this method.
     *
     * @param sortedHotels the list of hotels sorted by name
     * @param name the name to search for
     * @return the index of the hotel if found, otherwise -1
     */
    public static int searchHotelByName(List<Hotel> sortedHotels, String name) {
        // Create an example hotel with the search name to use for comparison
        // Other properties don't matter for name comparison
        Hotel searchHotel = new Hotel("", name, "", 0, "");

        // Use binary search with a comparator that compares hotels by name
        return binarySearch(sortedHotels, searchHotel, (h1, h2) -> h1.getName().compareTo(h2.getName()));
    }
}
