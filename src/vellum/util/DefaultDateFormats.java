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

import vellum.datatype.SafeDateFormat;
import java.util.Date;

/**
 *
 * @author evan.summers
 */
public class DefaultDateFormats {

    public static final SafeDateFormat dateFormat = new SafeDateFormat("yyyy-MM-dd");
    public static final SafeDateFormat timeSecondsFormat = new SafeDateFormat("HH:mm:ss");
    public static final SafeDateFormat timeMillisFormat = new SafeDateFormat("HH:mm:ss,SSS");
    public static final SafeDateFormat dateTimeSecondsFormat = new SafeDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SafeDateFormat dateTimeMillisFormat = new SafeDateFormat("yyyy-MM-dd HH:mm:ss,SSS");

    public static String formatDateTimeSeconds(long millis) {
        if (millis == 0) return "";
        return dateTimeSecondsFormat.format(new Date(millis));
    }
    
    public static Date parseTimestampMillis(String string) {
        SafeDateFormat format = DefaultDateFormats.timeMillisFormat;
        if (string.length() > format.getPattern().length()) {
            string = string.substring(0, format.getPattern().length());
        }
        return format.parse(string);
    }

    public static Date parseTimestamp(String string) {
        SafeDateFormat format = DefaultDateFormats.timeMillisFormat;
        if (string.length() > format.getPattern().length()) {
            string = string.substring(0, format.getPattern().length());
        }
        return format.parse(string);
    }

    public static Date parseDate(String string) {
        if (string.length() > DefaultDateFormats.timeMillisFormat.getPattern().length()) {
            string = string.substring(0, DefaultDateFormats.timeMillisFormat.getPattern().length());
        }
        if (string.length() == DefaultDateFormats.timeMillisFormat.getPattern().length()) {
            return timeMillisFormat.parse(string);
        }
        return dateFormat.parse(string);
    }
}
