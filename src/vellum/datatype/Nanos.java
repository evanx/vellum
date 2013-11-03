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

import java.util.concurrent.TimeUnit;

/**
 *
 * @author evan.summers
 */
public class Nanos {

    public static long toMicos(long nanos) {
        return nanos/1000;
    }
    
    public static long toMillis(long nanos) {
        return nanos/1000/1000;
    }
    
    public static long toSeconds(long nanos) {
        return nanos/1000/1000/1000;
    }

    public static long toMinutes(long nanos) {
        return toSeconds(nanos)/60;
    }

    public static long toHours(long nanos) {
        return toSeconds(nanos)/60/60;
    }

    public static long toDays(long nanos) {
        return toHours(nanos)/24;
    }
    
    public static long fromSeconds(long seconds) {
        return seconds*1000*1000;
    }

    public static long fromMinutes(long minutes) {
        return fromSeconds(minutes*60);
    }

    public static long fromHours(long hours) {
        return fromMinutes(hours*60);
    }

    public static long fromDays(long days) {
        return TimeUnit.DAYS.toNanos(days);
    }
    
    public static boolean isElapsed(long nanos, long duration) {
        return elapsed(nanos) > duration;
    }

    public static long elapsed(long nanos) {
        return System.nanoTime() - nanos;
    }
    
    public static String formatMillis(long nanos) {
        return String.format("%.3fms", 1.*nanos/1000/1000);
    }
    
    public static String formatSeconds(long nanos) {
        return String.format("%.3fs", 1.*nanos/1000/1000/1000);
    }
}
