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
package vellum.parameter;

import vellum.exception.Exceptions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author evan.summers
 */
public class Parameters {

    public static boolean isEmpty(List list) {
        return list == null || list.isEmpty() || list.get(0) == null;
    }

    public static StringEntry parseEntry(String string) {
        int index = string.indexOf("=");
        if (index > 0 && index < string.length()) {
            return new StringEntry(string.substring(0, index), string.substring(index + 1));
        }
        throw new IllegalArgumentException(string);
    }

    public static void put(Map map, String string) {
        StringEntry entry = parseEntry(string);
        map.put(entry.getKey(), entry.getValue());
    }
    
    public static <T> T get(Class<T> type) {
        T object;
        try {
            object = (T) type.newInstance();
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        }
        return object;
    }

    public static String getString(String[] args, String name) {
        for (String arg : args) {
            Entry entry = parseEntry(arg);
            if (entry != null && entry.getKey().equals(name)) {
                return (String) entry.getValue();
            }
        }
        return null;
    }

    public static String getString(String defaultValue, String name, String[] args, int index) {
        if (args.length > index) {
            return args[index];
        }
        return defaultValue;
    }
   
    public static String getString(String name, String[] args, int index) {
        if (args.length > index) {
            return args[index];
        }
        throw new IllegalArgumentException(name);
    }

    public static boolean getBoolean(String name, String[] args) {
        for (String arg : args) {
            if (arg.equals(name)) {
                return true;
            }
        }
        return false;
    }

    public static String getString(String defaultValue, String name, String[] args) {
        for (String arg : args) {
            Entry entry = parseEntry(arg);
            if (entry != null && entry.getKey().equals(name)) {
                return (String) entry.getValue();
            }
        }
        return defaultValue;
    }

    public static List<String> getList(String string) {
        List<String> list = new ArrayList();
        if (string == null || string.isEmpty()) return list;
        int index0 = string.indexOf('\"');
        if (index0 >= 0) {
            index0++;
            int index1 = string.indexOf('\"', index0);
            if (index1 > 0) {
                list.add(string.substring(index0, index1));
                index1++;
                if (index1 == string.length()) return list;
                string = string.substring(index1);
            }
        }
        for (String item : string.split("\\s")) {
            list.add(item);
        }
        return list;
    }

    public static Map<String, String> createMap(String[] args) {
        Map<String, String> map = new HashMap();
        for (String arg : args) {
            Entry<String, String> entry = parseEntry(arg);
            if (entry != null) {
                map.put(entry.getKey(), entry.getValue());
            } else {
                map.put(arg, null);
            }
        }
        return map;
    }

    public static void addString(List<String> list, String name, String[] args) {
        for (String arg : args) {
            if (arg.startsWith(name) && arg.length() >= name.length() + 1 && arg.charAt(name.length()) == '=') {
                String value = arg.substring(name.length() + 1);
                list.add(value);
            }
        }
    }

    public static boolean isProperty(String name, String[] args) {
        for (String arg : args) {
            if (arg.startsWith(name) && arg.length() >= name.length() + 1 && arg.charAt(name.length()) == '=') {
                return true;
            }
        }
        return false;
    }
}
