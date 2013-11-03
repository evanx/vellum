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

import vellum.parameter.Parameters;
import vellum.util.Args;
import java.util.Map;

/**
 *
 * @author evan.summers
 */
public class QueryParameters {
    String database;
    String schema;
    String query;
            
    public QueryParameters() {
    }

    public QueryParameters(String database, String schema, String query) {
        this.database = database;
        this.schema = schema;
        this.query = query;
    }

    public void init(String[] args) {
        init(Parameters.createMap(args));
    }

    public void init(Map<String, String> map) {
        database = map.get("database");
        schema = map.get("schema");
        query = map.get("query");
    }

    @Override
    public String toString() {
        return Args.format(database, schema, query);
    }
            
}
