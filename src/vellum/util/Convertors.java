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

import vellum.format.CalendarFormats;
import vellum.exception.ArgsRuntimeException;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author evan.summers
 */
public class Convertors {

   public static Object convert(Class propertyType, Object value) {
      if (value == null) {
         return null;
      }
      return parse(propertyType, value.toString());
   }

   public static Object parse(Class propertyType, String string) {
      if (string == null || string.isEmpty()) {
         return null;
      } else if (propertyType.isPrimitive()) {
         return parseIntrinsic(propertyType, string);
      } else if (propertyType == String.class) {
         return string;
      } else if (propertyType == Float.class) {
         return Float.valueOf(string);
      } else if (propertyType == Double.class) {
         return Double.valueOf(string);
      } else if (propertyType == Integer.class) {
         return Integer.valueOf(string);
      } else if (propertyType == Long.class) {
         return Long.valueOf(string);
      } else if (propertyType == Boolean.class) {
         return Boolean.valueOf(string);
      } else if (propertyType == BigDecimal.class) {
         return BigDecimal.valueOf(Double.parseDouble(string));
      } else if (propertyType == Date.class) {
         return CalendarFormats.parseDate(string);
      }
      throw new ArgsRuntimeException(propertyType, string);
   }

   protected static Object parseIntrinsic(Class propertyType, String string) {
      if (propertyType == char.class) {
         if (string.length() != 1) {
            throw new ArgsRuntimeException(propertyType, string);
         }
         return string.charAt(0);
      } else if (propertyType == float.class) {
         return Float.parseFloat(string);
      } else if (propertyType == double.class) {
         return Double.parseDouble(string);
      } else if (propertyType == int.class) {
         return Integer.parseInt(string);
      } else if (propertyType == long.class) {
         return Long.parseLong(string);
      } else if (propertyType == boolean.class) {
         return Boolean.parseBoolean(string);
      }
      throw new ArgsRuntimeException(propertyType, string);
   }

}
