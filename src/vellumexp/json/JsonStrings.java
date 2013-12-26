/*
 * Source https://code.google.com/p/vellum by @evanxsummers

       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements. See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership. The ASF licenses this file to
       you under the Apache License, Version 2.0 (the "License").
       You may not use this file except in compliance with the
       License. You may obtain a copy of the License at:

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.  
 */
package vellumexp.json;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.parameter.StringMap;

/**
 *
 * @author evan.summers
 */
public class JsonStrings {
    static Logger logger = LoggerFactory.getLogger(JsonStrings.class);

    public static String get(String json, String key) {
        return getAsJsonObject(json).get(key).getAsString();
    }

    public static StringMap getStringMap(String json) {
        StringMap map = new StringMap();
        for (Entry<String, JsonElement> entry : getAsJsonObject(json).entrySet()) {
            map.put(entry.getKey(), entry.getValue().getAsString());
        }
        return map;
    }

    public static JsonObject getAsJsonObject(String json) {
        return new JsonParser().parse(json).getAsJsonObject();
    }
    
    public static String buildJson(StringMap map) {
        return new Gson().toJson(map);
    }
        
}
