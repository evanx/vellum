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
package vellum.hype;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author evan.summers
 */
public class HypeMain {

    HypeContext context = new HypeContext();
    HypeReader reader = new HypeReader();

    public void start(InputStream inputStream, OutputStream outputStream) throws Exception {
        context.init();
        reader.read(inputStream, outputStream);
    }

    public void start(String[] args) throws Exception {
        InputStream in = System.in;
        OutputStream out = System.out;
        if (args.length > 0) {
            in = new FileInputStream(args[0]);
        }
        start(in, out);
    }

    public static void main(String[] args) throws Exception {
    }
}
