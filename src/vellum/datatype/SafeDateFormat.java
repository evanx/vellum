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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import vellum.exception.ParseRuntimeException;

/**
 *
 * @author evan.summers
 */
public class SafeDateFormat {

   SimpleDateFormat dateFormat;
   String pattern;

   public SafeDateFormat(String pattern) {
      this.pattern = pattern;
      dateFormat = new SimpleDateFormat(pattern);
   }

   public String getPattern() {
      return pattern;
   }

   public synchronized String format(Date date) {
      if (date == null) {
         return "";
      }
      return dateFormat.format(date);
   }

   public synchronized Date parse(String string){
       return parse(string, null);
       
   }
   
   public synchronized Date parse(String string, Date defaultValue) {
      if (string == null || string.isEmpty()) {
         return defaultValue;
      }
      if (string.length() > pattern.length()) {
          string = string.substring(0, pattern.length());
      }
        try {
            return dateFormat.parse(string);
        } catch (ParseException e) {
            throw new ParseRuntimeException(string, e);
        }
   }
}
