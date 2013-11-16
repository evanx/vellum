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
import vellum.logr.Logr;
import vellum.logr.LogrFactory;

/**
 *
 * @author evan.summers
 */
public class DelegatingHttpHandler implements HttpHandler {
    Logr logger = LogrFactory.getLogger(DelegatingHttpHandler.class);
    HttpHandlerFactory factory;
    HttpHandler delegate;

    public DelegatingHttpHandler(HttpHandlerFactory factory, HttpHandler delegate) {
        this.factory = factory;
        this.delegate = delegate;
    }
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        HttpHandler handler = factory.getHandler(exchange);
        if (handler != null) {
            if (factory.filter(exchange)) {
                handler.handle(exchange);
            }
        } else {
            delegate.handle(exchange);
        }
    }
}