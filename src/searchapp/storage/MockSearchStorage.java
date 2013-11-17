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
package searchapp.storage;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import org.h2.tools.Server;

/**
 *
 * @author evan.summers
 */
public class MockSearchStorage extends TemporaryConnectionStorage implements SearchStorage {
    Server h2Server;
    ConnectionStorage connectionStorage = new TemporaryConnectionStorage();
    MatchesStorage matchesStorage = new TemporaryMatchesStorage();
    Map<String, Connection> connectionMap = new HashMap();
    
    @Override
    public void init() throws Exception {
        h2Server = Server.createTcpServer().start();
    }

    @Override
    public void shutdown() {
        if (h2Server != null) {
            h2Server.stop();
        }
    }

    @Override
    public ConnectionStorage getConnectionStorage() {
        return connectionStorage;
    }

    @Override
    public MatchesStorage getMatchesStorage() {
        return matchesStorage;
    }
}
