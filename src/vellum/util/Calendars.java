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

import static java.util.Calendar.*;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import vellum.datatype.SafeDateFormat;

/**
 *
 * @author evan.summers
 */
public class Calendars {

    public static long getIntervalMillis(Date from, Date to) {
        return Math.abs(to.getTime() - from.getTime());
    }

    public static long toSeconds(long millis) {
        return millis / 1000;
    }

    public static long toMinutes(long millis) {
        return millis / 1000 / 60;
    }

    public static Date getYesterdayDate() {
        return getDate(-1);
    }

    public static Date getDate(int offset) {
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DATE, offset);
        return calendar.getTime();
    }

    public static boolean isToday(Date date) {
        Calendar calendar = newCalendar(date);
        Calendar today = new GregorianCalendar();
        if (calendar.get(Calendar.YEAR) != today.get(Calendar.YEAR)) {
            return false;
        }
        return (calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR));
    }

    public static Date nextDay(Date date) {
        Calendar calendar = newCalendar(date);
        calendar.add(Calendar.DATE, 1);
        return calendar.getTime();
    }

    public static Calendar newCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static Calendar newCalendar(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));
        return calendar;
    }
    
    public static Calendar newCalendar() {
        Calendar calendar = Calendar.getInstance();
        return calendar;
    }

    public static Date newDate() {
        return new Date();
    }

    public static Calendar parseCalendar(SafeDateFormat dateFormat, String string) {
        return Calendars.newCalendar(dateFormat.parse(string, null));
    }
    
    public static int getHourOfDay(Date date) {
        return newCalendar(date).get(HOUR_OF_DAY);
    }

    public static int getMinute(Date date) {
        return newCalendar(date).get(MINUTE);
    }

    public static String formatHH(Date date) {
        return String.format("%02d", newCalendar(date).get(HOUR_OF_DAY));
    }

    public static int getCurrentYear() {
        Calendar calendar = new GregorianCalendar();
        return calendar.get(Calendar.YEAR);
    }

    public static void setTime(Calendar calendar, int hour, int minute, int second) {
        calendar.set(HOUR_OF_DAY, hour);
        calendar.set(MINUTE, minute);
        calendar.set(SECOND, second);
        calendar.set(MILLISECOND, 0);
    }
    
    public static void setTime(Calendar calendar, Date time) {
        Calendar timeCal = newCalendar(time);
        setTime(calendar, timeCal.get(HOUR_OF_DAY), timeCal.get(MINUTE), timeCal.get(SECOND));
    }
}
