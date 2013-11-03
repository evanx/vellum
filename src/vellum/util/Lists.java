/*
 * Source https://code.google.com/p/vellum by @evanxsummers

       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements. See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.  
 */
package vellum.util;

import vellum.datatype.MapEntryComparator;
import vellum.datatype.MapValueComparator;
import vellum.format.ListFormats;
import java.util.*;

/**
 * Utility methods related to classes.
 * Utility methods for specific types eg {@code String}, are found in {@code Strings}.
 *
 * @author evan.summers
 */
public class Lists {

    public static <T> LinkedList<T> sortedLinkedList(Collection<T> collection, Comparator<T> comparator) {
        LinkedList list = new LinkedList(collection);
        Collections.sort(list, comparator);
        return list;
    }
    
    public static <T> LinkedList<T> sortedReverseLinkedList(Collection<T> collection, Comparator<T> comparator) {
        return sortedLinkedList(collection, Collections.reverseOrder(comparator));
    }

    public static Map sortByValue(Map map) {
        Map result = new TreeMap(new MapValueComparator(map));
        result.putAll(map);
        return result;
    }
    
    /**
     * Compare items in two lists for equality.
     * 
     */
    public static boolean equals(List list, List other) {
        if (list == other) return true;
        if (list == null || other == null) return false;
        if (list.size() != other.size()) return false;
        for (int i = 0; i < list.size(); i++) {
            if (!Types.equals(list.get(i), other.get(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Compare items in two arrays for equality.
     *
     */
    public static boolean equals(Object[] array, Object[] other) {
        if (array == other) return true;
        if (array == null || other == null) return false;
        if (array.length != other.length) return false;
        for (int i = 0; i < array.length; i++) {
            if (!Types.equals(array[i], other[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Convenience method used in {@code toString()} methods of objects
     * to format their properties.
     *
     */
    public static String format(Object[] args) {
        return ListFormats.formatter.formatArray(args);
    }

    /**
     * Convenience method used in {@code toString()} methods of objects
     * to format their properties.
     *
     */
    public static String format(Collection collection) {
        return ListFormats.formatter.formatArray(collection);
    }
    
    /**
     * Create and populate a new fixed length list.
     *
     */
    public static <T> List<T> newList(Class<T> type, int length) {
        List list = new ArrayList(length);
        for (int i = 0; i < length; i++) {
            list.add(Types.newInstance(type));
        }
        return list;
    }

    public static List<Map.Entry> sortedEntryList(Map map) {
        List list = new ArrayList(map.entrySet());
        Collections.sort(list, new MapEntryComparator());
        return list;
    }
    

    public static List asList(String[] array) {
        List list = new ArrayList();
        for (String string : array) {
            list.add(string);
        }
        return list;
    }

    public static <T> T get(List<T> list, int index) {
        if (list != null && index < list.size()) {
            return (T) list.get(index);
        }
        return null;
    }

    public static boolean contains(String[] array, String string) {
        for (String item : array) {
            if (item.equals(string)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean contains(Map map, Object... keys) {
        for (Object key : keys) {
            if (map.containsKey(key)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsAll(Map map, Object... keys) {
        for (Object key : keys) {
            if (!map.containsKey(key)) {
                return false;
            }
        }
        return true;
    }

    public static int compareTo(Object[] array, Object[] other) {
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
            int result = Types.compareTo(array[i], other[i]);
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }

    public static boolean isEmpty(String[] array) {
        return array != null && array.length > 0 && array[0] != null && array[0].length() > 0;
    }

    public static String getFirst(String[] array) {
        if (array != null && array.length > 0) {
            return array[0];
        }
        return null;
    }

    public static <T> T getFirst(List<T> list) {
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }
    
    public static String getLast(String[] array) {
        if (array != null && array.length > 0) {
            return array[array.length - 1];
        }
        return null;
    }

    public static Object[] toArray(String[] array) {
        return Arrays.asList(array).toArray();
    }

    public static List toList(byte[] array) {
        List list = new ArrayList();
        for (byte element : array) {
            list.add(element);
        }
        return list;
    }

    public static <T> List<String> toStringList(T[] array) {
        List<String> list = new ArrayList();
        for (T element : array) {
            list.add(element.toString());
        }
        return list;
    }
    
    public static boolean intersects(String[] array, String[] other) {
        return intersects(asHashSet(array), asHashSet(other));
    }

    public static <T> HashSet<T> coalesceHashSetArgs(T ... args) {
        HashSet<T> set = new HashSet();
        for (T arg : args) {
            if (arg != null) {
                set.add(arg);
            }
        }
        return set;
    }
    
    public static <T> HashSet<T> coalesceHashSet(Collection<T> collection) {
        HashSet<T> set = new HashSet();
        for (T item : collection) {
            if (item != null) {
                set.add(item);
            }
        }
        return set;
    }
    
    public static HashSet asHashSet(String[] array) {
        return new HashSet(Arrays.asList(array));
    }
    
    public static boolean intersects(HashSet set, HashSet other) {
        int size = set.size();
        set = new HashSet(set);
        set.removeAll(other);
        return set.size() < size;
    }   

    public static boolean containsArgs(Object value, Object ... args) {
        for (Object arg : args) {
            if (value == arg) {
                return true;
            }
            if (value != null && arg != null && value.equals(arg)) {
                return true;
            }
        }
        return false;
    }
    
    public static <T extends Enum> boolean contains(T[] args, Object value) {
        for (T arg : args) {
            if (value == arg) {
                return true;
            }
        }
        return false;
    }

    public static <T> List<T> asList(Enumeration<T> en) {
        List list = new ArrayList();
        while (en.hasMoreElements()) {
            list.add(en.nextElement());
        }
        return list;
    }

    public static List<String> subList(String[] array, int fromIndex) {
        List<String> list = new ArrayList();
        list.addAll(asList(array));
        return list.subList(fromIndex, array.length);
    }

    public static <E> SortedSet<E> asSortedSet(Enumeration<E> enumeration) {
        return new TreeSet(Collections.list(enumeration));
    }
    
}
