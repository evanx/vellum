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
package searchapp.util.http;

import com.sun.net.httpserver.HttpExchange;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.parameter.StringMap;
import vellum.util.Streams;

/**
 *
 * @author evan.summers
 */
public class EncodedMap {    
    static Logger logger = LoggerFactory.getLogger(EncodedMap.class);
    StringMap map = new StringMap();

    public EncodedMap() {
    }
    
    public StringMap parse(String string) throws UnsupportedEncodingException {
        int index = 0;
        while (index < string.length()) {
            int endIndex = string.indexOf("&", index);
            if (endIndex > 0) {
                put(string.substring(index, endIndex));
                index = endIndex + 1;
            } else if (index < string.length()) {
                put(string.substring(index));
                break;
            }
        }
        return map;
    }

    private void put(String string) throws UnsupportedEncodingException {
        int index = string.indexOf("=");
        if (index > 0 && index < string.length()) {
            map.put(string.substring(0, index), 
                    URLDecoder.decode(string.substring(index + 1), "UTF-8"));
        }
    }
}
