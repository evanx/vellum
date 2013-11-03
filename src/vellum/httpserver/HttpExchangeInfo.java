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
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import vellum.exception.DisplayMessage;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import vellum.parameter.Entry;
import vellum.parameter.StringMap;
import vellum.parameter.Parameters;
import vellum.util.Beans;
import vellum.util.JsonStrings;
import vellum.util.Lists;
import vellum.util.Streams;
import vellum.util.Strings;

/**
 *
 * @author evan.summers
 */
public class HttpExchangeInfo {
    
    Logr logger = LogrFactory.getLogger(getClass());
    HttpExchange httpExchange;
    PrintStream out;
    StringMap parameterMap;
    String urlQuery;
    String requestBody;
    String[] args;
    boolean headersParsed = false;
    boolean acceptGzip = false;
    boolean agentWget = false;
    StringMap cookieMap;
    
    public HttpExchangeInfo(HttpExchange httpExchange) {
        this.httpExchange = httpExchange;
    }
    
    public String getRemoteHostName() {
        return httpExchange.getRemoteAddress().getHostName();
    }

    public String getQuery() {
        return httpExchange.getRequestURI().getQuery();
    }
    
    public String getPath() {
        return httpExchange.getRequestURI().getPath();
    }

    public String[] getPathArgs() {
        if (args == null) {
            args = httpExchange.getRequestURI().getPath().substring(1).split("/");
        }
        return args;
    }

    public int getPathLength() {
        return getPathArgs().length;
    }

    public String getRequestBody() {
        if (requestBody == null) {
            requestBody = Streams.readString(httpExchange.getRequestBody());
        }
        return requestBody;
    }
    
    public boolean isParameter(String name) {
        String value = getParameterMap().get(name);
        if (value == null) return false;
        return value.equals("on");
    }

    public StringMap getParameterMap() {
        if (parameterMap == null) {
            parseParameterMap();
        }
        return parameterMap;
    }
    
    private void parseParameterMap() {
        parameterMap = new StringMap();
        urlQuery = httpExchange.getRequestURI().getQuery();
        if (httpExchange.getRequestMethod().equals("POST")) {
            urlQuery = Streams.readString(httpExchange.getRequestBody());
        }
        logger.info("parseParameterMap", httpExchange.getRequestMethod());
        if (urlQuery == null) {
            return;
        }
        int index = 0;
        while (index < urlQuery.length()) {
            int endIndex = urlQuery.indexOf("&", index);
            if (endIndex > 0) {
                put(urlQuery.substring(index, endIndex));
                index = endIndex + 1;
            } else if (index < urlQuery.length()) {
                put(urlQuery.substring(index));
                return;
            }
        }
    }

    private void put(String string) {
        Entry<String, String> entry = Parameters.parseEntry(string);
        if (entry != null) {
            String value = Strings.decodeUrl(entry.getValue());
            parameterMap.put(entry.getKey(), value);
        }
    }
    
    public String getParameter(String key) {
        return parameterMap.get(key);
    }

    public Integer getInteger(String key) {
        String string = parameterMap.get(key);
        if (string != null) {
            return Integer.parseInt(key);
        } 
        return null;
    }
    
    public void clearCookie(Collection<String> keys) {
        for (String key : keys) {
            httpExchange.getResponseHeaders().add("Set-cookie", 
                    String.format("%s=; Expires=Thu, 01 Jan 1970 00:00:00 GMT", key));
            
        }
    }
    
    public void setCookie(StringMap map, long ageMillis) {
        String path = map.get("path");
        String version = map.get("version");
        for (String key : map.keySet()) {
            String value = map.get(key);
            StringBuilder builder = new StringBuilder();
            builder.append(String.format("%s=%s; Max-age=%d", key, value, ageMillis/1000));
            if (path != null) {
                builder.append("; Path=").append(path);
            }
            if (version != null) {
                builder.append("; Version=").append(version);
            }
            httpExchange.getResponseHeaders().add("Set-cookie", builder.toString());
        }
    }

    
    public StringMap getCookieMap() {
        if (cookieMap == null) {
            parseCookieMap(parseFirstRequestHeader("Cookie"));
        }
        return cookieMap;
    }
        
    public String getCookie(String key) {
        return getCookieMap().get(key);
    }

    private void parseCookieMap(List<String> cookies) {
        cookieMap = new StringMap();
        if (cookies != null) {
            for (String cookie : cookies) {
                int index = cookie.indexOf("=");
                if (index > 0) {
                    String key = cookie.substring(0, index);
                    String value = cookie.substring(index + 1);
                    cookieMap.put(key, value);
                }
            }
        }
    }
        
    public List<String> parseFirstRequestHeader(String key) {
        logger.info("parseFirstRequestHeader", key);
        String text = httpExchange.getRequestHeaders().getFirst(key);
        if (text != null) {
            List<String> list = new ArrayList();
            for (String string : text.split(";")) {
                list.add(string.trim());
            }
            return list;
        }
        return null;
    }
        
    public void parseHeaders() {
        headersParsed = true;
        for (String key : httpExchange.getRequestHeaders().keySet()) {
            List<String> values = httpExchange.getRequestHeaders().get(key);
            logger.info("parseHeaders", key, values);
            if (key.equals("Accept-encoding")) {
                if (values.contains("gzip")) {
                    acceptGzip = true;
                }
            } else if (key.equals("User-agent")) {
                for (String value : values) {
                    if (value.toLowerCase().contains("wget")) {
                        agentWget = true;
                    }
                }
            }
        }
    }

    public void setBean(Object bean) {
        for (PropertyDescriptor property : Beans.getPropertyMap(bean.getClass()).values()) {
            String stringValue = parameterMap.get(property.getName());
            if (stringValue != null) {
                Beans.parse(bean, property, stringValue);
            }
        }
    }

    public boolean isAgentWget() {
        if (!headersParsed) {
            parseHeaders();
        }
        return agentWget;
    }

    public boolean isAcceptGzip() {
        if (!headersParsed) {
            parseHeaders();
        }
        return acceptGzip;
    }

    public String getPathString(int index) {
        return getPathString(index, null);
    }

    public String getPathString(int index, String defaultValue) {
        String[] args = getPathArgs();
        if (args.length > index) {
            return args[index];
        }
        return defaultValue;
    }

    public void parsePathParameters(Map pathParameterMap, int fromIndex) {
        List<String> args = Lists.subList(getPathArgs(), fromIndex);
        for (String arg : args) {
            int index = arg.indexOf('=');
            if (index < 1) {
                throw new IllegalArgumentException(arg);
            } else {
                Parameters.put(pathParameterMap, arg);
            }
        }
    }

    public void setResponseHeader(String key, String value) throws IOException {
        httpExchange.getResponseHeaders().set(key, value);
    }

    public List<String> getRequestHeader(String key) throws IOException {
        return httpExchange.getRequestHeaders().get(key);
    }

    public void sendResponse(String contentType, byte[] bytes) throws IOException {
        httpExchange.getResponseHeaders().set("Content-type", contentType);
        httpExchange.getResponseHeaders().set("Content-length", Integer.toString(bytes.length));
        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
        getPrintStream().write(bytes);
    }
    
    public void sendResponseFile(String contentType, String fileName) throws IOException {
        httpExchange.getResponseHeaders().add("Content-Disposition",
                "attachment; filename=" + fileName);
        httpExchange.getResponseHeaders().set("Content-type", contentType);
        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
    }
    
    public void sendResponse(String contentType, boolean ok) throws IOException {
        httpExchange.getResponseHeaders().set("Content-type", contentType);
        if (ok) {
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
        } else {
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
        }
    }

    public void handleError(Exception e) {
        e.printStackTrace(System.err);
        handleError(e.getMessage());
    }

    public void handleError(DisplayMessage message) {
        handleError(message.getDisplayMessage());
    }
    
    public void handleError(String messageString) {
        try {
            logger.warn(messageString, parameterMap);
            httpExchange.getResponseHeaders().set("Content-type", "text/json");
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
            PrintStream out = new PrintStream(httpExchange.getResponseBody());
            out.printf("{ errorMessage: \"%s\"; }\n", messageString);
        } catch (Exception e) {
            logger.warn(e);
        }
    }
    
    public PrintStream getPrintStream() {
        if (out == null) {
            out = new PrintStream(httpExchange.getResponseBody());
        }
        return out;
    }

    public void write(StringMap map) throws IOException {
        sendResponse("text/json", true);
        getPrintStream().println(JsonStrings.buildJson(map));
    }
    
    public String getInputString() {
        return Streams.readString(httpExchange.getRequestBody());
    }

}
