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
package vellum.datatype;

import java.util.regex.Pattern;

/**
 *
 * @author evan.summers
 */
public class Patterns {
    private static final String USERNAME_PATTERN_CONTENT = "[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*";
    private static final String URL_PATTERN_CONTENT = "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})";
    
    public static Pattern USERNAME_PATTERN = Pattern.compile("^" + USERNAME_PATTERN_CONTENT + "$");
    public static Pattern URL_PATTERN = Pattern.compile("^" + URL_PATTERN_CONTENT + "$");
    public static Pattern EMAIL_PATTERN = Pattern.compile("^" + USERNAME_PATTERN_CONTENT + "@" + URL_PATTERN_CONTENT + "$");

    public static boolean matchesUserName(String string) {
        return USERNAME_PATTERN.matcher(string).matches();
    }
    
    public static boolean matchesUrl(String string) {
        return URL_PATTERN.matcher(string).matches();
    }
    
    public static boolean matchesEmail(String string) {
        return EMAIL_PATTERN.matcher(string).matches();
    }
}
