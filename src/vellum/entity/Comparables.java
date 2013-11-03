/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package vellum.entity;

/**
 *
 * @author evan.summers
 */
public class Comparables {

    public static int compareTo(Comparable[] array, Comparable[] other) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null && other[i] == null) {
                continue;
            }
            if (array[i] == null) {
                return -1;
            }
            if (other[i] == null) {
                return 1;
            }
            int result = array[i].compareTo(other[i]);
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }

    public static int hashCode(Comparable[] array) {
        int hash = 0;
        for (int i = 0; i < array.length; i++) {
            hash ^= array[i].hashCode();
        }
        return hash;
    }
        
    public static int compareTo(Comparable comparable, Comparable other) {
        if (comparable == other) return 0;
        if (other == null) return 1;
        if (comparable == null) return -1;
        return comparable.compareTo(other);
    }
    
    public static boolean equals(Comparable comparable, Comparable other) {
        return compareTo(comparable, other) == 0;
    }

    public static String toString(String comparable) {
        if (comparable == null) return "null";
        return comparable.toString();
    }
}
