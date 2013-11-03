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
package vellum.query;

import vellum.exception.Exceptions;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author evan.summers
 */
public final class QueryInfoMap {

    static final Map<QueryResource, QueryInfoMap> instanceMap = new HashMap();
    Logr logger = LogrFactory.getLogger(getClass());
    Map<String, QueryInfo> map = new TreeMap();
    Map<String, String> optionMap = new HashMap();
    List<String> optionList = new ArrayList();
    List<QueryInfo> list = new ArrayList();
    String database;
    String schema;
    String user;
    boolean exclude = false;

    public QueryInfoMap() {
    }

    public QueryInfoMap(QueryResource resource) {
        this(resource.getStream());
    }

    public QueryInfoMap(InputStream stream) {
        init(stream);
    }

    public QueryInfoMap(Class parent) {
        this(parent, parent.getSimpleName() + ".sql");
    }

    public QueryInfoMap(Class parent, String resourceName) {
        init(parent.getResourceAsStream(resourceName));
    }

    public QueryInfoMap(Class parent, String resourceName, String schema) {
        this(parent, resourceName);
        setSchema(schema);
    }
    
    public void setSchema(String schema) {
        this.schema = schema;
        for (QueryInfo queryInfo : map.values()) {
            queryInfo.setSchema(schema);
            queryInfo.setUser(schema + "_" + queryInfo.getDatabase());
        }
    }

    protected final void init(InputStream stream) {
        try {
            initImpl(stream);
        } catch (IOException e) {
            throw Exceptions.newRuntimeException(e);
        }
    }

    protected final void initImpl(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder builder = new StringBuilder();
        boolean inBlock = false;
        String queryName = null;
        String query = null;
        while (true) {
            String line = reader.readLine();
            if (line == null) {
                return;
            }
            if (line.startsWith("\\q")) {
                return;
            }
            String trimmedLine = line.trim();
            if (inBlock) {
                if (trimmedLine.equals(";") || trimmedLine.isEmpty()) {
                    inBlock = false;
                    query = builder.toString().trim();
                    builder.setLength(0);
                    QueryInfo queryInfo = new QueryInfo(queryName, query, database, user);
                    if (!exclude) {
                        map.put(queryName, queryInfo);
                        list.add(queryInfo);
                    }
                } else {
                    builder.append(line).append("\n");
                }
            } else if (line.startsWith("\\connect ")) {
                String[] words = line.split(" ");
                if (words.length > 1) {
                    database = words[1];
                }
                if (words.length > 2) {
                    user = words[2];
                } else {
                    user = null;
                }
                if (schema != null) {
                    user = schema + "_" + database;
                }
            } else if (line.toLowerCase().startsWith("select")) {
                inBlock = true;
                queryName = "unnamed";
            } else if (line.startsWith("--")) {
                inBlock = true;
                if (trimmedLine.length() > 3) {
                    queryName = trimmedLine.substring(3);
                } else {
                    queryName = "unnamed";
                }
            } else if (line.startsWith("\\exclude")) {
                exclude = true;
            } else if (line.startsWith("\\include")) {
                exclude = false;
            } else if (line.startsWith("\\")) {
                optionList.add(line);
                String[] words = line.split(" ");
                if (words.length >= 2) {
                    optionMap.put(words[0], words[1]);
                }
            }
        }
    }

    public QueryInfo get(String queryName) {
        if (map.containsKey(queryName)) {
            return map.get(queryName);
        }
        throw new IllegalArgumentException(queryName);
    }

    public Map<String, QueryInfo> getMap() {
        return map;
    }

    public List<String> getOptionList() {
        return optionList;
    }

    public List<QueryInfo> getList() {
        return list;
    }

    public static QueryInfoMap getInstance(QueryResource resource) {
        QueryInfoMap queryInfoMap = instanceMap.get(resource);
        if (queryInfoMap == null) {
            queryInfoMap = new QueryInfoMap(resource);
            instanceMap.put(resource, queryInfoMap);
        }
        return queryInfoMap;
    }

    public static QueryInfo get(Class parent, String queryName) {
        return new QueryInfoMap(parent).get(queryName);

    }
}
