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
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import vellum.lifecycle.Startable;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;

/**
 *
 * @author evan.summers
 */
public class VellumHttpServer implements Startable {
    private Logr logger = LogrFactory.getLogger(VellumHttpServer.class);
    HttpServer httpServer;
    HttpServerConfig config;     
    ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 8, 0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(4));
    
    public VellumHttpServer(HttpServerConfig config) {
        this.config = config;
    }

    private boolean portAvailable(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            serverSocket.close();
            return true;
        } catch (Exception e) {
            logger.warn("portAvailable", e.getMessage());
            return false;
        }
    }
    
    private boolean waitPort(int port, long millis, long sleep) {
        long time = System.currentTimeMillis() + millis;
        while (!portAvailable(port)) {
            if (System.currentTimeMillis() > time) {
                return false;
            }
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                logger.warn(e, "waitPort");
            }
        }
        return true;
    }

    public void start(HttpHandler httpHandler) throws Exception {
        start();
        httpServer.createContext("/", httpHandler);
    }
    
    @Override
    public void start() throws Exception {
        waitPort(config.getPort(), 4000, 500);
        InetSocketAddress socketAddress = new InetSocketAddress(config.getPort());
        httpServer = HttpServer.create(socketAddress, 4);
        httpServer.setExecutor(executor);
        httpServer.start();
        logger.info("start", config.getPort());
    }

    public void startContext(String contextName, HttpHandler httpHandler) {
        httpServer.createContext(contextName, httpHandler);
    }

    @Override
    public boolean stop() {
        if (httpServer != null) {
            httpServer.stop(0); 
            executor.shutdown();
            return true;
        }  
        return false;
    }
}
