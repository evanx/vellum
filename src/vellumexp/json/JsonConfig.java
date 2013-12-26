/*
 Source https://code.google.com/p/vellum by @evanxsummers

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

import vellum.util.ExtendedProperties;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @TODO implement properly to support different sections
 *
 * @author evan.summers
 */
public class JsonConfig extends JsonMapParser {

    private Logger logger = LoggerFactory.getLogger(JsonConfig.class);
    private ExtendedProperties properties = new ExtendedProperties(System.getProperties());

    public static ExtendedProperties parseProperties(Class parent, String prefix) throws IOException {
        return new JsonConfig().init(parent, prefix).getProperties();
    }
        
    public JsonConfig init(Class parent, String prefix) throws IOException {
        String confFileName = properties.getString(prefix + ".json", prefix + ".json");
        File confFile = new File(confFileName);
        if (confFile.exists()) {            
            properties.putAll(parse(new FileInputStream(confFile)));
        } else {
            logger.warn("resource {} {}", parent, prefix);
            properties.putAll(parse(parent.getResourceAsStream(prefix + ".json")));
            properties.put("confParentClass", parent);
        }
        return this;
    }
   
    public ExtendedProperties getProperties() {
        return properties;
    }

    public ExtendedProperties getProperties(String prefix) {
        return properties;
    }
}
