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
package searchapp.app;

import dualcontrol.ExtendedProperties;

/**
 *
 * @author evan.summers
 */
public class SearchProperties {
    ExtendedProperties properties;
    String confFileName = "search.conf";
    int port = 8443;
    boolean test = true;
    String domainName = "localhost";
    
    public void init(ExtendedProperties properties) {
        this.properties = properties;
        port = properties.getInt("port", port);
        test = properties.getBoolean("test", test);
        if (test) {            
        }
    }
    
    public String getConfFileName() {
        return confFileName;
    }

    public int getPort() {
        return port;
    }    

    public boolean isTest() {
        return test;
    }        

    public String getDomainName() {
        return domainName;
    }
    
}
