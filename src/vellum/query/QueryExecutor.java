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

import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import com.sun.rowset.CachedRowSetImpl;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.sql.RowSet;
import javax.sql.rowset.CachedRowSet;

/**
 *
 * @author evan.summers
 */
public class QueryExecutor {

   Logr logger = LogrFactory.getLogger(getClass());
   Connection connection;
   Statement statement;
   long durationMillis;

   public QueryExecutor() {
   }

   public RowSet execute(QueryInfo queryInfo) throws Exception {
      try {
         connection = RowSets.getConnection(queryInfo);
         statement = connection.createStatement();
         durationMillis = System.currentTimeMillis();
         ResultSet res = statement.executeQuery(queryInfo.getQuery());
         CachedRowSet rowSet = new CachedRowSetImpl();
         rowSet.populate(res);
         res.close();
         statement.close();
         connection.close();
         queryInfo.setRowSet(rowSet);
         return rowSet;
      } catch (Exception e) {
         logger.warn(null, queryInfo, queryInfo.getQuery());
         throw e;
      } finally {
         durationMillis = System.currentTimeMillis() - durationMillis;
      }
   }
}
