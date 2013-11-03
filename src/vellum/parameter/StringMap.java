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
package vellum.parameter;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import vellum.exception.EnumRuntimeException;

/**
 *
 * @author evan.summers
 */
public class StringMap extends HashMap<String, String> {
    List<Entry<String, String>> entryList = new ArrayList();

    public StringMap() {
    }

    public StringMap(Map m) {
        super(m);
    }

    public String put(String key, String value) {
        return super.put(key, value);
    }

    
    public String put(String key, Object object) {
        if (object == null) {
            return super.put(key, null);
        } else {
            return super.put(key, object.toString());    
        }
    }

    public long getLong(String key) {
        return Long.parseLong(getString(key));
    }
    
    public String getString(String key) {
        String value = super.get(key);
        if (value == null) {
            throw new EnumRuntimeException(StringMapExceptionType.NOT_FOUND);
        }
        return value;
    }
    
    public String getString(String key, String defaultValue) {
        String value = super.get(key);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }
    
    public long getLong(String key, long defaultValue) {
        String string = super.get(key);
        if (string == null) {
            return defaultValue;
        }
        return Long.parseLong(string);
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}

