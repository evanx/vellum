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
import java.net.HttpURLConnection;
import java.net.InetAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.lifecycle.Shutdownable;

/**
 *
 * @author evan.summers
 */
public class ShutdownHttpHandler implements HttpHandler {
    Logger logger = LoggerFactory.getLogger(ShutdownHttpHandler.class);
    Shutdownable app;
    
    public ShutdownHttpHandler(Shutdownable app) {
        this.app = app;
    }
    
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            if (httpExchange.getRemoteAddress().getAddress().equals(
                    InetAddress.getLocalHost())) {
                app.shutdown();
                httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);          
            } else {
                httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
        } finally {
            httpExchange.close();
        }
    }    
}
