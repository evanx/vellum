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

/**
 *
 * @author evan.summers
 */
public class PrinterDelegator implements Printer {
    protected Printer out;

    public PrinterDelegator(Printer out) {
        this.out = out;
    }
    
    @Override
    public void println() {
        out.println();
    }

    @Override
    public void println(Object object) {
        out.println(object);
    }

    @Override
    public void print(Object object) {
        out.print(object);
    }

    @Override
    public void printf(String format, Object... args) {
        out.print(String.format(format, args));
    }

    @Override
    public void printlnf(String format, Object... args) {
        out.println(String.format(format, args));
    }

    @Override
    public void flush() {
        out.flush();
    }

    @Override
    public void close() {
        out.close();
    }

}
