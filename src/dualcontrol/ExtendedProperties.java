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
    
    public ExtendedProperties(Properties properties) {
        super.putAll(properties);
    }

    public ExtendedProperties(Properties properties, String prefix) {
        for (Object key : properties.keySet()) {
            String propertyName = key.toString();
            if (propertyName.startsWith(prefix) && 
                    propertyName.charAt(prefix.length()) == '.') {
                super.put(propertyName.substring(prefix.length() + 1), 
                        properties.get(propertyName));
            }
        }
    }
    
    public String getString(String propertyName) {
        String propertyValue = super.getProperty(propertyName);
        if (propertyValue == null) {
            throw new RuntimeException("Missing -D property: " + propertyName);
        }
        return propertyValue;
    } 

    public String getString(String propertyName, String defaultValue) {
        String propertyValue = super.getProperty(propertyName);
        if (propertyValue == null) {
            return defaultValue;
        }
        return propertyValue;
    } 
    
    public int getInt(String name) {
        Object value = get(name);
        if (value == null) {
            throw new RuntimeException("Missing -D property: " + name);
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return Integer.parseInt(value.toString());
    }
    
    public int getInt(String propertyName, int defaultValue) {
        String propertyString = super.getProperty(propertyName);
        if (propertyString == null) {
            return defaultValue;
        }
        return Integer.parseInt(propertyString);
    }

    public boolean getBoolean(String propertyName) {
        return getBoolean(propertyName, false);
    }
    
    public boolean getBoolean(String propertyName, boolean defaultValue) {
        Object object = super.get(propertyName);
        if (object == null) {
            return defaultValue;
        }
        if (object instanceof String) {
            return Boolean.parseBoolean((String) object);
        }
        if (object instanceof Boolean) {
            return (Boolean) object;
        }
        throw new RuntimeException("Property value is not boolean: " + propertyName);
    }
    
    public char[] getPassword(String name) {
        return getPassword(name, new SystemConsole());
    }    
    
    public char[] getPassword(String propertyName, MockableConsole console) {
        Object object = super.get(propertyName);
        if (object == null) {
            return console.readPassword(propertyName);
        }
        logger.info("getPassword {} {}", propertyName, object.getClass().getName());        
        if (object instanceof String) {
            return object.toString().toCharArray();
        }
        if (object instanceof char[]) {
            return (char[]) object;
        }
        throw new RuntimeException("Unhandled password property type: " + propertyName);
    }        
}
