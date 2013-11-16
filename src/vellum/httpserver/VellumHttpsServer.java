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
import ephemeral.EphemeralSSLContextFactory;
import java.net.InetSocketAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import searchapp.util.httphandler.DelegatingHttpHandler;
import searchapp.util.httphandler.FilteringHttpHandler;
import searchapp.util.httphandler.HttpHandlerFactory;
import searchapp.util.httphandler.WebHttpHandler;
import vellum.lifecycle.Shutdownable;
import vellum.security.HttpsConfiguratorFactory;

/**
 *
 * @author evan.summers
 */
public class VellumHttpsServer implements Shutdownable {

    Logger logger = LoggerFactory.getLogger(VellumHttpsServer.class);
    SSLContext sslContext;
    HttpsServer httpsServer;
    ExtendedProperties properties; 
    ThreadPoolExecutor executor;
    
    public VellumHttpsServer() {
    }

    public void start(ExtendedProperties properties, HttpHandler handler) throws Exception {
        start(properties, new EphemeralSSLContextFactory().create(properties), handler);
    }
    
    public void start(ExtendedProperties properties, SSLContext sslContext,
            HttpHandler handler) throws Exception {
        int port = properties.getInt("port", 8443);
        boolean needClientAuth = properties.getBoolean("needClientAuth", false);
        executor = new ThreadPoolExecutor(4, 8, 0, TimeUnit.MILLISECONDS, 
            new ArrayBlockingQueue<Runnable>(4));
        InetSocketAddress socketAddress = new InetSocketAddress(port);
        httpsServer = HttpsServer.create(socketAddress, 4);
        httpsServer.setHttpsConfigurator(HttpsConfiguratorFactory.
                createHttpsConfigurator(sslContext, needClientAuth));
        httpsServer.setExecutor(executor);
        httpsServer.createContext("/", handler);
        httpsServer.start();
        logger.info("init {}", port);
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
    
    public static VellumHttpsServer start(ExtendedProperties properties, String webPath,
            String appContext, HttpHandlerFactory httpHandlerFactory) throws Exception {
        VellumHttpsServer server = new VellumHttpsServer();
        server.start(properties, new DelegatingHttpHandler(appContext, httpHandlerFactory, 
                new WebHttpHandler(webPath)));
        return server;
    }
    
}
