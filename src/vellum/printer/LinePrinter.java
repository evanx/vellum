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
package vellum.printer;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author evan.summers
 */
public class LinePrinter implements Printer {
    List<String> lineList = new LinkedList();

    public LinePrinter() {
    }

    public void printf(String format, Object ... args) {
        lineList.add(String.format(format, args));
    }

    public void printlnf(String format, Object ... args) {
        lineList.add(String.format(format, args));
    }

    public void println() {
        lineList.add(null);
    }

    public void println(Object object) {
        lineList.add(object.toString());
    }

    public void print(Object object) {
        lineList.add(object.toString());
    }


    public List<String> getLineList() {
        return lineList;
    }

    public void flush() {
    }

    public void close() {
    }


}
