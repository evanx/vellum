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

import java.util.Date;
import java.util.concurrent.TimeUnit;
import vellum.exception.ParseRuntimeException;

/**
 *
 * @author evan.summers
 */
public class Millis {
    
    public static long toSeconds(long millis) {
        return millis/1000;
    }

    public static long toMinutes(long millis) {
        return millis/1000/60;
    }

    public static long toHours(long millis) {
        return millis/1000/60/60;
    }

    public static long toDays(long millis) {
        return millis/1000/60/60/24;
    }
    
    public static long fromSeconds(long seconds) {
        return seconds*1000;
    }

    public static long fromMinutes(long minutes) {
        return minutes*60*1000;
    }

    public static long fromHours(long hours) {
        return hours*60*60*1000;
    }
    
    public static long fromDays(long days) {
        return TimeUnit.DAYS.toMillis(days);
    }

    public static boolean isElapsed(long startMillis, long millis) {
        return elapsed(startMillis) > millis;
    }

    public static boolean isElapsed(Date startDate, long millis) {
        if (startDate == null) return true;
        return isElapsed(startDate.getTime(), millis);
    }

    public static long elapsed(long startMillis) {
        return System.currentTimeMillis() - startMillis;
    }
        
    public static String formatAsSeconds(long millis) {
        if (millis == 0) return "00:00:00";
        long hour = millis/Millis.fromHours(1);
        long minute = (millis % Millis.fromHours(1))/Millis.fromMinutes(1);
        long second = (millis % Millis.fromMinutes(1))/Millis.fromSeconds(1);
        return String.format("%02d:%02d:%02d", hour, minute, second);
    }

    public static String format(long millis) {
        if (millis == 0) return "00:00:00,000";
        long hour = millis/Millis.fromHours(1);
        long minute = (millis % Millis.fromHours(1))/Millis.fromMinutes(1);
        long second = (millis % Millis.fromMinutes(1))/Millis.fromSeconds(1);
        long millisecond = millis % Millis.fromSeconds(1);
        return String.format("%02d:%02d:%02d,%03d", hour, minute, second, millisecond);
    }
    
    public static long parse(String string) {
        int index = string.indexOf(" ");
        if (index > 0) {
            return TimeUnit.valueOf(string.substring(index + 1)).toMillis(Long.parseLong(string.substring(0, index)));
        } else if (string.length() >= 2 &&
                Character.isLowerCase(string.charAt(string.length() - 1)) && 
                Character.isDigit(string.charAt(string.length() - 2))) {    
            long value = Long.parseLong(string.substring(0, string.length() - 1));    
            if (string.endsWith("d")) {
                return TimeUnit.DAYS.toMillis(value);
            } else if (string.endsWith("h")) {
                return TimeUnit.HOURS.toMillis(value);
            } else if (string.endsWith("m")) {
                return TimeUnit.MINUTES.toMillis(value);
            } else if (string.endsWith("s")) {
                return TimeUnit.SECONDS.toMillis(value);
            }
        }  
        throw new ParseRuntimeException(string);
    }
}
