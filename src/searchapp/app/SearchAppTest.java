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

import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import searchapp.entity.ConnectionEntity;
import searchapp.util.ssl.EphemeralSSLContext;

/**
 *
 * @author evan.summers
 */
public class SearchAppTest {

    Logger logger = LoggerFactory.getLogger(getClass());
    SearchApp app;

    public SearchAppTest(SearchApp app) {
        this.app = app;
    }
        
    public void test() throws Exception {
        app.getStorage().getConnectionStorage().insert(
                new ConnectionEntity("connection1", "org.h2.Driver", "jdbc:h2:mem:", "sa", null)
                );
        logger.info("select {}", app.getStorage().getConnectionStorage().select("connection1"));
        HttpsURLConnection connection = new EphemeralSSLContext().createConnection(
                "client", new URL("https://localhost:8443/shutdown"));
        connection.connect();
    }                
}
