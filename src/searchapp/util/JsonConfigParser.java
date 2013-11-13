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
package searchapp.util;

import dualcontrol.ExtendedProperties;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import searchapp.app.SearchHttpHandler;

/**
 * @TODO implement properly using GSON for different sections for different prefixes
 * 
 * @author evan.summers
 */
public class JsonConfigParser {

    Logger logger = LoggerFactory.getLogger(SearchHttpHandler.class);
    ExtendedProperties properties = new ExtendedProperties(System.getProperties());
    Pattern keyValuePattern = Pattern.compile("\\s*(\\w+):\\s*[\"']*([/|\\w|.]+)[\"';,]*");

    
    public void init(String confFileName) throws FileNotFoundException, IOException {
        confFileName = properties.getString(confFileName, confFileName);
        BufferedReader reader = new BufferedReader(new FileReader(confFileName));
        while (true) {
            String line = reader.readLine();
            if (line == null) {
                return;
            }
            Matcher matcher = keyValuePattern.matcher(line);
            if (matcher.find()) {
                properties.put(matcher.group(1), matcher.group(2));
                logger.info("{} {}", matcher.group(1), matcher.group(2));
            }
        }
    }

    public ExtendedProperties getProperties() {
        return properties;
    }

    public ExtendedProperties getProperties(String prefix) {
        return properties;
    }
}
