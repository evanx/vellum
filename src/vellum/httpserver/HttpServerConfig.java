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
package vellum.httpserver;

import vellum.config.PropertiesStringMap;

/**
 *
 * @author evan.summers
 */
public class HttpServerConfig {
    int port;
    boolean enabled;
    boolean clientAuth;
            
    public HttpServerConfig(PropertiesStringMap props) {
        this(props.getInt("port"),
                props.getBoolean("clientAuth", false),
                props.getBoolean("enabled", true));
    }
    
    public HttpServerConfig(int port, boolean clientAuth, boolean enabled) {
        this.port = port;
        this.clientAuth = clientAuth;
        this.enabled = enabled;
    }

    public int getPort() {
        return port;
    }

    public boolean isClientAuth() {
        return clientAuth;
    }
    
    public boolean isEnabled() {
        return enabled;
    }  
}
