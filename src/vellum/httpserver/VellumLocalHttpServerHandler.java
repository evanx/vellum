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

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import vellum.util.Streams;

/**
 *
 * @author evan.summers
 */
public class VellumLocalHttpServerHandler implements HttpHandler {

    final Logr logger = LogrFactory.getLogger(VellumLocalHttpServerHandler.class);
    final VellumLocalHttpServer server;
    final VellumLocalHttpServerConfig config;

    public VellumLocalHttpServerHandler(VellumLocalHttpServer app) {
        this.server = app;
        this.config = app.getConfig();
    }

    public void init() throws IOException {
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String path = httpExchange.getRequestURI().getPath();
            httpExchange.getResponseHeaders().add("Cache-Control", "no-cache, no-store, must-revalidate");
            if (path.endsWith("/log")) {
                String message = Streams.readString(httpExchange.getRequestBody());
                logger.info(message);
                httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                httpExchange.close();
                return;
            }
            if (!path.startsWith("/bootstrap")) {
                logger.trace("path", path);
            }
            if (path.endsWith(".png")) {
                httpExchange.getResponseHeaders().set("Content-type", "image/png");
            } else if (path.endsWith(".ico")) {
                httpExchange.getResponseHeaders().set("Content-type", "image/ico");
            } else if (path.endsWith(".html")) {
                httpExchange.getResponseHeaders().set("Content-type", "text/html");
            } else if (path.endsWith(".css")) {
                httpExchange.getResponseHeaders().set("Content-type", "text/css");
            } else if (path.endsWith(".js")) {
                httpExchange.getResponseHeaders().set("Content-type", "text/javascript");
            } else if (path.endsWith(".txt")) {
                httpExchange.getResponseHeaders().set("Content-type", "text/plain");
            } else if (path.endsWith(".html")) {
                httpExchange.getResponseHeaders().set("Content-type", "text/html");
            } else if (path.endsWith(".ttf")) {
                httpExchange.getResponseHeaders().set("Content-type", "font/truetype");
            } else if (path.endsWith(".woff")) {
                httpExchange.getResponseHeaders().set("Content-type", "application/font-woff");
            } else {
                logger.info(path);
                httpExchange.getResponseHeaders().set("Content-type", "text/html");
            }
            File file = getFile(config.getRootFile());
            if (path.length() > 0) {
                file = getFile(path);
                if (!file.exists() || file.isDirectory()) {
                    file = getFile(config.getRootFile());
                }
            }
            FileInputStream inputStream = new FileInputStream(file);
            byte[] bytes = Streams.readBytes(inputStream);
            logger.trace("path", path, bytes.length);
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
            httpExchange.getResponseBody().write(bytes);
        } catch (Exception e) {
            logger.warn(e);
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
        }
        httpExchange.close();
    }
    
    private File getFile(String path) {
        return new File(config.getRootDir() + '/' + path);
    }
}
