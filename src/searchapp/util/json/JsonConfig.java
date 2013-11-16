/*
 Source https://code.google.com/p/vellum by @evanxsummers

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
package searchapp.util.json;

import dualcontrol.ExtendedProperties;
import java.io.File;
import java.io.FileInputStream;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import searchapp.util.json.JsonParser;

/**
 * @TODO implement properly to support different sections
 *
 * @author evan.summers
 */
public class JsonConfig extends JsonParser {

    Logger logger = LoggerFactory.getLogger(JsonConfig.class);
    ExtendedProperties properties = new ExtendedProperties(System.getProperties());
    Pattern keyValuePattern = Pattern.compile(
            "\\s*[\"']*(\\w+)[\"']*:\\s*[\"']*(\\w+[^\"';,]+)[\"';,]*");

    public void init(Class parent, String prefix) throws Exception {
        String confFileName = properties.getString(prefix + ".json", prefix + ".json");
        File confFile = new File(confFileName);
        if (confFile.exists()) {            
            properties.putAll(parse(new FileInputStream(confFile)));
        } else {            
            properties.putAll(parse(parent.getResourceAsStream(prefix + ".json")));
            properties.put("confParentClass", parent);
        }
    }
   
    public ExtendedProperties getProperties() {
        return properties;
    }

    public ExtendedProperties getProperties(String prefix) {
        return properties;
    }
}
