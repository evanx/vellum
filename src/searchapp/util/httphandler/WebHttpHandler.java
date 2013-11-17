/*
 Source https://code.google.com/p/vellum by @evanxsummers

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
package searchapp.util.httphandler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import vellum.util.Streams;

/**
 *
 * @author evan.summers
 */
public class WebHttpHandler implements HttpHandler {

    Logr logger = LogrFactory.getLogger(WebHttpHandler.class);
    Map<String, byte[]> cache = new HashMap();
    String webPath;
    
    public WebHttpHandler(String webPath) {
        this.webPath = webPath;
    }
    
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        String contentType = "text/html";
        if (httpExchange.getRequestURI().getPath().endsWith(".html")) {
        } else if (httpExchange.getRequestURI().getPath().endsWith(".png")) {
            contentType = "image/png";
        } else if (httpExchange.getRequestURI().getPath().endsWith(".css")) {
            contentType = "text/css";
        } else if (httpExchange.getRequestURI().getPath().endsWith(".js")) {
            contentType = "text/javascript";
        } else if (httpExchange.getRequestURI().getPath().endsWith(".txt")) {
            contentType = "text/plain";
        } else if (path.endsWith(".woff")) {
            contentType = "application/font-woff";
        } else {
            path = "app.html";
        }
        try {
            httpExchange.getResponseHeaders().set("Content-type", contentType);
            byte[] bytes = cache.get(path);
            if (bytes == null) {
                String resourcePath = webPath + '/' + path;
                if (path.startsWith("/")) {
                    resourcePath = webPath + path;
                } 
                logger.info("get {}", resourcePath);
                InputStream resourceStream = getClass().getResourceAsStream(
                        resourcePath);
                bytes = Streams.readBytes(resourceStream);
                cache.put(path, bytes);
            }
            logger.info("path", path, bytes.length);
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
            httpExchange.getResponseBody().write(bytes);
        } catch (Exception e) {
            logger.warn(e);
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
        }
        httpExchange.close();
    }
    
    public static HttpHandler create(String appContext, String webPath, 
            HttpHandlerFactory factory) {
        return new DelegatingHttpHandler(appContext, factory, new WebHttpHandler(webPath));
    }
}
