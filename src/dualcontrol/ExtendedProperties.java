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
package dualcontrol;

import java.util.HashMap;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author evan.summers
 */
public class ExtendedProperties extends Properties {
    static Logger logger = LoggerFactory.getLogger(ExtendedProperties.class);

    public ExtendedProperties() {
        super();
    }

    public ExtendedProperties(HashMap map) {
        super.putAll(map);
    }
    
    public ExtendedProperties(Properties properties) {
        super.putAll(properties);
    }

    public ExtendedProperties(Properties properties, String prefix) {
        for (Object object : properties.keySet()) {
            String key = object.toString();
            if (key.startsWith(prefix) && 
                    key.charAt(prefix.length()) == '.') {
                super.put(key.substring(prefix.length() + 1), 
                        properties.get(key));
            }
        }
    }
    
    public String getString(String key) {
        String propertyValue = super.getProperty(key);
        if (propertyValue == null) {
            throw new RuntimeException("Missing property: " + key);
        }
        return propertyValue;
    } 

    public String getString(String key, String defaultValue) {
        String propertyValue = super.getProperty(key);
        if (propertyValue == null) {
            return defaultValue;
        }
        return propertyValue;
    } 
    
    public int getInt(String name) {
        Object value = get(name);
        if (value == null) {
            throw new RuntimeException("Missing property: " + name);
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return Integer.parseInt(value.toString());
    }
    
    public int getInt(String key, int defaultValue) {
        String propertyString = super.getProperty(key);
        if (propertyString == null) {
            return defaultValue;
        }
        return Integer.parseInt(propertyString);
    }

    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }
    
    public boolean getBoolean(String key, boolean defaultValue) {
        Object object = super.get(key);
        if (object == null) {
            return defaultValue;
        }
        if (object instanceof String) {
            return Boolean.parseBoolean((String) object);
        }
        if (object instanceof Boolean) {
            return (Boolean) object;
        }
        throw new RuntimeException("Property value is not boolean: " + key);
    }
    
    public char[] getPassword(String name) {
        return getPassword(name, new SystemConsole());
    }    
    
    public char[] getPassword(String key, MockableConsole console) {
        Object object = super.get(key);
        if (object == null) {
            return console.readPassword(key);
        }
        if (object instanceof String) {
            return object.toString().toCharArray();
        }
        if (object instanceof char[]) {
            return (char[]) object;
        }
        throw new RuntimeException("Invalid passwordpassword property type: " + key);
    }        
    
    public Class getClass(String key) throws ClassNotFoundException {
        Object object = get(key);
        if (object instanceof Class) {
            return (Class) object;
        } else if (object instanceof String) {
            return Class.forName((String) key);
        }
        throw new RuntimeException("Invalid class property type: " + key);
    }
}
