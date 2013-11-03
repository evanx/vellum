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

import vellum.enumtype.DelimiterType;
import vellum.exception.Exceptions;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

/**
 * This class contains several useful methods for working with Strings,
 * as language extensions.
 *
 * {@code Types} has utility methods related to types in general,
 * whereas this is more specific to {@code String} types.
 *
 * @author evan.summers
 */
public class Strings {

    public static final String UTF8 = "UTF-8";
    public static final String ENCODING = "UTF-8";

    /**
     * Return first argument that is not null.
     * 
     */
    public static String coalesce(String... args) {
        for (String arg : args) {
            if (arg != null) {
                return arg;
            }
        }
        return null;
    }

    /**
     * Format args using pattern.
     * 
     */
    public static String format(String string, Object[] args) {
        List list = new ArrayList();
        for (Object arg : args) {
            if (arg instanceof Date) {
                arg = DefaultDateFormats.timeMillisFormat.format((Date) arg);
            }
            list.add(arg);
        }
        return String.format(string, list.toArray());
    }

    /**
     * Join list of lines.
     * 
     * @return 
     */
    public static String joinLines(List<String> lineList) {
        StringBuilder builder = new StringBuilder();
        for (String line : lineList) {
            if (builder.length() > 0) {
                builder.append("\n");
            }
            builder.append(line);
        }
        return builder.toString();
    }

    /**
     * Join list of lines.
     * 
     * @return 
     */
    public static String joinArray(String delimiter, String[] args) {
        StringBuilder builder = new StringBuilder();
        for (String arg : args) {
            if (builder.length() > 0) {
                builder.append(delimiter);
            }
            builder.append(arg);
        }
        return builder.toString();
    }

    /**
     * check equality.
     * 
     */
    public static boolean equals(List<String> list, List<String> otherList) {
        if (list == null || otherList == null || list.size() != otherList.size()) return false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == null || otherList.get(i) == null) return false;
            if (!list.get(i).equals(otherList.get(i))) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * This class cannot be instantiated, since all of its methods are static.
     *
     */
    private Strings() {
    }

    /**
     * @returns {@literal true} if the given {@code String} is not empty and contains only uppercase letters
     * @see Character#isUpperCase(char)
     */
    public static boolean isUpperCase(String string) {
        if (isEmpty(string)) {
            return false;
        }
        for (char ch : string.toCharArray()) {
            if (!Character.isUpperCase(ch)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @returns {@literal true} if the given {@code String} is not empty and contains only lowercase letters
     * @see Character#isLowerCase(char)
     */
    public static boolean isLowerCase(String string) {
        if (isEmpty(string)) {
            return false;
        }
        for (char ch : string.toCharArray()) {
            if (!Character.isLowerCase(ch)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns {@literal true} if the given {@code String} is {@literal null}
     * or considered empty. This method takes the additional step of testing
     * to see if the {@link String#trim() trimmed} {@code String} is
     * empty.
     *
     * @param object the {@code String} to test
     * @return {@literal true} if the given {@code String} is empty
     */
    public static boolean isEmpty(final String string) {
        return string == null
                || string.isEmpty()
                || string.trim().isEmpty();
    }

    /**
     * @returns {@literal true} if the given {@code String} is not empty and contains only digits
     * @see Character#isDigit(char)
     */
    public static boolean isDigits(String string) {
        if (isEmpty(string)) {
            return false;
        }
        for (char ch : string.toCharArray()) {
            if (!Character.isDigit(ch)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @returns {@literal true} if the given {@code String} is not empty and contains only letters
     * @see Character#isLetter(char)
     */
    public static boolean isLetter(String string) {
        if (isEmpty(string)) {
            return false;
        }
        for (char ch : string.toCharArray()) {
            if (!Character.isLetter(ch)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Convert to uppercase, if not {@literal null}.
     *
     * @see String#toUpperCase()
     */
    public static String toUpperCase(String string) {
        if (string == null) {
            return null;
        }
        return string.toUpperCase();
    }

    /**
     * Determine if the string starts with any of the given arguments.
     */
    public static boolean startsWith(String string, String... args) {
        if (string == null || args.length == 0) {
            return false;
        }
        for (String arg : args) {
            if (arg != null && string.startsWith(arg)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determine if the string equals any of the given arguments.
     */
    public static boolean equals(String string, String... args) {
        if (string == null || args.length == 0) {
            return false;
        }
        for (String arg : args) {
            if (arg != null && string.equals(arg)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Null safe compare.
     *
     */
    public static int compareTo(String string, String other) {
        if (string == null || other == null) {
            return 1;
        }
        return string.compareTo(other);
    }

    /**
     * Determine if the string equals of the given arguments.
     */
    public static boolean equals(String string, Collection<String> list) {
        if (string == null || list == null || list.isEmpty()) {
            return false;
        }
        for (String arg : list) {
            if (arg != null && string.equals(arg)) {
                return true;
            }

        }
        return false;
    }

    /**
     * Determine if the string contains any of the given arguments.
     */
    public static boolean contains(String string, String... args) {
        return contains(string, Arrays.asList(args));
    }

    /**
     * Determine if the string contains any of the given arguments.
     */
    public static boolean contains(String string, List<String> list) {
        return contains(string, new HashSet(list));
    }

    /**
     * Determine if the string contains any of the strings in the given set.
     */
    public static boolean contains(String string, Set<String> set) {
        if (string == null || set == null || set.isEmpty()) {
            return false;
        }
        for (String arg : set) {
            if (arg != null && string.contains(arg)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determine if the string contains any of the strings in the given set.
     */
    public static boolean containsDigits(String string, Set<String> set) {
        if (string == null || set == null || set.isEmpty()) {
            return false;
        }
        for (String arg : set) {
            if (arg != null && isDigits(arg) && string.contains(arg)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Create a new set of strings with items that are digits.
     */
    public static Set<String> newSetDigits(Set<String> set) {
        Set<String> result = new HashSet();
        if (set != null) {
            for (String arg : set) {
                if (arg != null && isDigits(arg)) {
                    result.add(arg);
                }
            }
        }
        return result;
    }

    /**
     * Determine if the string contains any of the given arguments.
     */
    public static boolean containsAll(String string, String... args) {
        if (string == null) {
            return false;
        }
        for (String arg : args) {
            if (arg == null || !string.contains(arg)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determine if the string contains any of the given arguments.
     */
    public static boolean containsIgnoreCase(String string, String... args) {
        if (string == null) {
            return false;
        }
        string = string.toLowerCase();
        for (String arg : args) {
            if (arg != null && string.contains(arg)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determine if the string contains any of the given arguments.
     */
    public static boolean contains(StringBuilder string, List<String> list) {
        if (string == null || list == null || list.isEmpty()) {
            return false;
        }
        for (String arg : list) {
            if (arg != null && string.indexOf(arg) >= 0) {
                return true;
            }
        }
        return false;
    }

    public static String escapeHtml(String text) {
        StringBuilder builder = new StringBuilder();
        for (String line : text.split("\n")) {
            line = escapeXml(line);
            line = highlightXml(line);
            if (builder.length() > 0) {
                builder.append("\n");
            }
            builder.append(line);
            if (line.startsWith("&lt;/")) {
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    public static String highlightXml(String text) {
        if (text.startsWith("20") && text.length() > 32) {
            return "<b>" + text.substring(0, 23) + "</b>" + text.substring(23);
        }
        int index1 = text.indexOf("&gt;");
        if (index1 > 0) {
            int index2 = text.indexOf("&lt;", index1);
            if (index2 > index1) {
                index1 += 4;
                text = text.substring(0, index1) + " <b>" + text.substring(index1, index2) + "</b> " + text.substring(index2);
            }
        }
        return text;
    }

    public static String escapeXml(String text) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == '<') {
                builder.append("&lt;");
            } else if (ch == '>') {
                builder.append("&gt;");
            } else if (ch == '&') {
                builder.append("&amp;");
            } else {
                builder.append(ch);
            }
        }
        return builder.toString();
    }

    public static String[] split(String string, DelimiterType delimiterType) {
        if (string == null) {
            return new String[]{};
        }
        if (delimiterType == DelimiterType.SPACE) {
        }
        return string.split("\\s");
    }

    public static void appendln(StringBuilder builder, Object object) {
        builder.append(object);
        builder.append("\n");
    }

    public static void trim(String[] strings) {
        for (int i = 0; i < strings.length; i++) {
            if (strings[i] != null) {
                strings[i] = strings[i].trim();
            }
        }
    }

    public static String toCamelCase(String string) {
        boolean printable = true;
        StringBuilder builder = new StringBuilder();
        for (char ch : string.toCharArray()) {
            if (Character.isLetterOrDigit(ch)) {
                if (printable) {
                    builder.append(ch);
                } else {
                    builder.append(Character.toUpperCase(ch));
                }
                printable = true;
            } else {
                printable = false;
            }
        }
        return builder.toString();
    }

    public static String parseWord(String string, int fromIndex) {
        StringBuilder builder = new StringBuilder();
        while (fromIndex < string.length() && isWhitespace(string.charAt(fromIndex))) {
            fromIndex++;
        }
        while (fromIndex < string.length() && !isWhitespace(string.charAt(fromIndex))) {
            builder.append(string.charAt(fromIndex));
            fromIndex++;
        }
        return builder.toString();
    }

    public static boolean isWhitespace(char ch) {
        return ch == ' ' || ch == '\n' || ch == '\t';
    }

    public static String encodeUrl(String string) {
        try {
            return URLEncoder.encode(string, Strings.ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw Exceptions.newRuntimeException(e);
        }
    }

    public static String decodeUrl(String string) {
        try {
            return URLDecoder.decode(string, ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw Exceptions.newRuntimeException(e);
        }
    }
    
    public static void replace(StringBuilder text, String pattern, String string) {
        if (string != null) {
            int index = 0;
            while (true) {
                index = text.indexOf(pattern, index);
                if (index >= 0) {
                    text.replace(index, index + pattern.length(), string);
                    index += string.length();
                    if (index >= text.length()) {
                        break;
                    }
                } else {
                    break;
                }
            }
        }
    }
        
    
}
