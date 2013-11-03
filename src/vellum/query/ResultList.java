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
import javax.sql.RowSet;
import vellum.query.QueryInfo;

/**
 *
 * @author evan.summers
 */
public class ResultList<K> {
    List<String> columnNameList;
    List<String> columnTypeNameList = new ArrayList();
    Map<String, String> columnTypeNameMap = new HashMap();
    List<Object[]> rowList = new ArrayList();

    public ResultList(QueryInfo queryInfo) {
        try {
            RowSet rowSet = RowSets.getRowSet(queryInfo);
            ResultSetMetaData md = rowSet.getMetaData();
            columnNameList = RowSets.getColumnNameList(md);
            for (int i = 0; i < md.getColumnCount(); i++) {
                int columnIndex = i + 1;
                String columnName = md.getColumnName(columnIndex);
                columnTypeNameList.add(md.getColumnTypeName(columnIndex));
                columnTypeNameMap.put(columnName, md.getColumnTypeName(columnIndex));
            }
            while (rowSet.next()) {
                Object[] array = new Object[columnNameList.size()];
                for (int i = 0; i < md.getColumnCount(); i++) {
                    int columnIndex = i + 1;
                    array[i] = rowSet.getObject(columnIndex);
                }
                rowList.add(array);
            }
            rowSet.close();
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

    public List<Object[]> getRowList() {
        return rowList;
    }
}
