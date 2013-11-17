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

import searchapp.test.SearchAppTest;
import searchapp.storage.SearchStorage;
import searchapp.storage.MockSearchStorage;
import searchapp.util.json.JsonConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.httpserver.VellumHttpsServer;
import vellum.lifecycle.Shutdownable;

/**
 *
 * @author evan.summers
 */
public class SearchApp implements Shutdownable {

    static Logger logger = LoggerFactory.getLogger(SearchApp.class);
    JsonConfig config = new JsonConfig();
    SearchStorage storage;
    VellumHttpsServer httpsServer;

    public void init() throws Exception {
        config.init(SearchAppTest.class, "search");
        httpsServer = VellumHttpsServer.start(config.getProperties("httpsServer"),
                "/searchapp/web", "/app/", new AppHttpHandlerFactory(this));
        logger.info("HTTPS server started");
        logger.info("started");
        if (config.getProperties().getBoolean("developing", true)) {
            storage = new MockSearchStorage();
            SearchAppTest test = new SearchAppTest(this);
            test.init();
            if (config.getProperties().getBoolean("testing", false)) {
                test.test();
                test.shutdown();
            }
        } else {
            throw new Exception("Production mode not implemented yet");
        }
    }

    @Override
    public void shutdown() {
        httpsServer.shutdown();
        storage.shutdown();
    }

    public SearchStorage getStorage() {
        return storage;
    }

    public static void main(String[] args) throws Exception {
        try {
            SearchApp app = new SearchApp();
            app.init();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}
