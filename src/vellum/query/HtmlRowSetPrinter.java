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
package vellum.query;

import vellum.exception.Exceptions;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import vellum.printer.Printer;
import vellum.util.Types;
import java.sql.ResultSetMetaData;
import javax.sql.RowSet;
import vellum.format.ArgFormats;

/**
 *
 * @author evan.summers
 */
public class HtmlRowSetPrinter {

   Logr logger = LogrFactory.getLogger(getClass());
   Printer out;
   boolean outputInfo;

   public HtmlRowSetPrinter(Printer out, boolean outputInfo) {
      this.out = out;
      this.outputInfo = outputInfo;
   }

   public void print(RowSet set) {
      try {
         ResultSetMetaData md = set.getMetaData();
         out.printf("<table class='resultSet'>\n");
         out.printf("<thead>\n");
         for (int index = 1; index <= md.getColumnCount(); index++) {
            out.printf("<th>%s\n", md.getColumnName(index));
         }
         out.printf("</thead>\n");
         out.printf("<tbody>\n");
         int resultCount = 0;
         set.beforeFirst();
         while (set.next()) {
            out.printf("<tr class='row%d'>\n", resultCount % 2);
            for (int index = 1; index <= md.getColumnCount(); index++) {
               Object value = set.getObject(index);
               String string = ArgFormats.displayFormatter.format(value);
               if (string.endsWith(".0")) {
                  string = string.substring(0, string.length() - 2);
               }
               out.printf("<td class='%sCell'>%s\n", md.getColumnTypeName(index), string);
            }
            resultCount++;
         }
         out.printf("</tbody>\n");
         out.printf("</table>\n");
         out.flush();
         if (outputInfo) {
            String results = "no results";
            if (resultCount == 1) {
               results = "1 result";
            } else if (resultCount > 1) {
               results = String.format("%d results", resultCount);
            }

            out.printf("<div class='resultInfo'>%s</div>\n", results);
            out.flush();
         }
      } catch (Exception e) {
         throw Exceptions.newRuntimeException(e);
      }
   }
}
