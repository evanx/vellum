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

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;

/**
 *
 * @author evan.summers
 */
public class VellumLocalHttpServer {
    private Logr logger = LogrFactory.getLogger(VellumLocalHttpServer.class);
    HttpServer httpServer;
    VellumLocalHttpServerConfig config;     
    ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 8, 0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(4));
    
    public VellumLocalHttpServer(VellumLocalHttpServerConfig config) {
        this.config = config;
    }

    public VellumLocalHttpServerConfig getConfig() {
        return config;
    }
    
    public void start() throws Exception {
        InetSocketAddress socketAddress = new InetSocketAddress(config.getPort());
        httpServer = HttpServer.create(socketAddress, 4);
        httpServer.setExecutor(executor);
        httpServer.createContext("/", new VellumLocalHttpServerHandler(this));
        httpServer.start();
        logger.info("start", config.getPort());
    }

    public boolean stop() {
        if (httpServer != null) {
            httpServer.stop(0); 
            executor.shutdown();
            return true;
        }  
        return false;
    }
    
    public static void main(String[] args) throws Exception {
        VellumLocalHttpServer server = new VellumLocalHttpServer(new VellumLocalHttpServerConfig());
        server.start();
    }
}
