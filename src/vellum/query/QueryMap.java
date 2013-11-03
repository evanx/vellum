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
import vellum.util.Streams;
import java.io.BufferedReader;
import java.io.InputStream;
import java.util.HashMap;

/**
 *
 * @author evan.summers
 */
public class QueryMap extends HashMap<String, String> {

    public QueryMap(Class parent) {
        this(parent, parent.getSimpleName() + ".sql");
    }

    public QueryMap(Class parent, String name) {
        try {
            InputStream stream = parent.getResourceAsStream(parent.getSimpleName() + ".sql");
            BufferedReader reader = Streams.newBufferedReader(stream);
            StringBuilder builder = null;
            String key = null;
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                } else if (line.trim().length() == 0) {
                    if (key != null) {
                        put(key, builder.toString().trim());
                        key = null;
                    }
                } else if (line.startsWith("--")) {
                    if (line.length() > 3) {
                        key = line.substring(2).trim();
                        builder = new StringBuilder();
                    }
                } else if (key != null) {
                    builder.append(line);
                    builder.append("\n");
                }
            }
            if (key != null) {
                put(key, builder.toString());
            }
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        }
    }
}
