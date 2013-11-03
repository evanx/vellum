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
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.sql.RowSet;

/**
 *
 * @author evan.summers
 */
public class ResultMap<K> {

   String keyColumn;
   List<String> columnNameList;
   List<String> columnTypeNameList = new ArrayList();
   Map<String, String> columnTypeNameMap = new HashMap();
   List<Map> rowList = new ArrayList();
   Map<K, Map<String, Object>> rowMap = new TreeMap();
   int rowCount;

   public ResultMap(QueryInfo queryInfo, String keyColumn) {
      this(RowSets.getRowSet(queryInfo), keyColumn);
   }

   public ResultMap(RowSet rowSet, String keyColumn) {
      this.keyColumn = keyColumn;
      try {
         ResultSetMetaData md = rowSet.getMetaData();
         columnNameList = RowSets.getColumnNameList(md);
         for (int i = 1; i <= md.getColumnCount(); i++) {
            String columnName = md.getColumnName(i);
            columnTypeNameList.add(md.getColumnTypeName(i));
            columnTypeNameMap.put(columnName, md.getColumnTypeName(i));
         }
         rowSet.beforeFirst();
         while (rowSet.next()) {
            Map map = new HashMap();
            for (String columnName : columnNameList) {
               map.put(columnName, rowSet.getObject(columnName));
            }
            rowMap.put((K) rowSet.getObject(keyColumn), map);
            rowList.add(map);
            rowCount++;
         }
      } catch (Exception e) {
         throw Exceptions.newRuntimeException(e);
      }
   }

   public List<String> getColumnNameList() {
      return columnNameList;
   }

   public Map<String, String> getColumnTypeNameMap() {
      return columnTypeNameMap;
   }

   public Map<K, Map<String, Object>> getRowMap() {
      return rowMap;
   }

   public Map<String, Object> getColumnMap(K key) {
      return rowMap.get(key);
   }

   public Object getCell(K key, String columnName) {
      if (!rowMap.containsKey(key)) {
         if (true) {
            return null;
         }
         throw new NullPointerException(key.toString());
      }
      return rowMap.get(key).get(columnName);
   }

   public List<Map> getRowList() {
      return rowList;
   }

   @Override
   public String toString() {
      return String.format("rows %d, mapped %d", rowCount, rowMap.size());
   }


}
