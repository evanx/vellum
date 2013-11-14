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
import vellum.storage.DataSourceConfig;

/**
 *
 * @author evan.summers
 */
public class SearchProperties {
    private String confFileName = "search.conf";
    private int port = 8443;
    private boolean test = true;
    private String domainName = "localhost";
    private DataSourceConfig dataSourceConfig;
    
    public void init(ExtendedProperties properties) {
        port = properties.getInt("port", port);
        test = properties.getBoolean("test", test);
        dataSourceConfig = new DataSourceConfig(properties);
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

    public DataSourceConfig getDataSourceConfig() {
        return dataSourceConfig;
    }            
}
