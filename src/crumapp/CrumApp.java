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
package crumapp;

import com.sun.net.httpserver.HttpExchange;
import javax.net.ssl.SSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.httpserver.VellumHttpsServer;
import vellum.security.DefaultKeyStores;

/**
 *
 * @author evan.summers
 */
public class CrumApp {

    Logger logger = LoggerFactory.getLogger(getClass());
    CrumConfig config = new CrumConfig();
    CrumStorage storage = new CrumStorage();
    Thread serverThread;
    VellumHttpsServer httpsServer;

    public void init() throws Exception {
        config.init();
        storage.init();
        httpsServer = new VellumHttpsServer(config.getProperties("httpsServer"));
        SSLContext sslContext = DefaultKeyStores.createSSLContext(
                new CrumTrustManager(this));
        httpsServer.init(sslContext);
        
    }

    public void start() throws Exception {
        if (httpsServer != null) {
            httpsServer.start();
            httpsServer.startContext("/", new CrumHttpHandler(this));
            logger.info("HTTPS server started");
        }
    }

    public void stop() throws Exception {
        if (httpsServer != null) {
            httpsServer.stop();
        }
    }

    public CrumStorage getStorage() {
        return storage;
    }
    
    public static void main(String[] args) throws Exception {
        try {
            CrumApp app = new CrumApp();
            app.init();
            app.start();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public void handle(HttpExchange httpExchange) {
        String path = httpExchange.getRequestURI().getPath();
        logger.info("path", path);
    }
}