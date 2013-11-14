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

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpsServer;
import dualcontrol.ExtendedProperties;
import java.net.InetSocketAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.lifecycle.Startable;
import vellum.security.HttpsConfiguratorFactory;

/**
 *
 * @author evan.summers
 */
public class VellumHttpsServer implements Startable {
    Logger logger = LoggerFactory.getLogger(VellumHttpsServer.class);
    SSLContext sslContext;
    HttpsServer httpsServer;
    ExtendedProperties properties;     
    ThreadPoolExecutor executor;
    
    public VellumHttpsServer(ExtendedProperties properties) {
        this.properties = properties;
    }

    public void init(SSLContext sslContext) throws Exception {
        this.sslContext = sslContext;
    }
    
    @Override
    public void start() throws Exception {
        int port = properties.getInt("port");
        boolean needClientAuth = properties.getBoolean("needClientAuth", false);
        executor = new ThreadPoolExecutor(4, 8, 0, TimeUnit.MILLISECONDS, 
            new ArrayBlockingQueue<Runnable>(4));
        InetSocketAddress socketAddress = new InetSocketAddress(port);
        httpsServer = HttpsServer.create(socketAddress, 4);
        httpsServer.setHttpsConfigurator(HttpsConfiguratorFactory.
                createHttpsConfigurator(sslContext, needClientAuth));
        httpsServer.setExecutor(executor);
        httpsServer.start();
        logger.info("start {}", port);
    }

    public void createContext(String contextName, HttpHandler httpHandler) {
        httpsServer.createContext(contextName, httpHandler);
    }

    @Override
    public void shutdown() {
        if (httpsServer != null) {
            httpsServer.stop(0);
            executor.shutdown();
        }  
    }
}
