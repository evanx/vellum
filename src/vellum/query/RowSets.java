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

import vellum.exception.ArgsRuntimeException;
import vellum.exception.Exceptions;
import vellum.util.Beans;
import vellum.util.Strings;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import com.sun.rowset.CachedRowSetImpl;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.sql.RowSet;
import javax.sql.rowset.CachedRowSet;
import vellum.format.ListFormats;

/**
 *
 * @author evan.summers
 */
public class RowSets {

    public static Logr logger = LogrFactory.getLogger(RowSets.class);

    public static String formatArgsQuoted(Object[] array) {
        return ListFormats.formatter.formatQuote(array);
    }

    public static String formatArgs(Object ... args) {
        return ListFormats.formatter.formatArray(args);
    }
    
    public static void close(Statement statement) {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            throw Exceptions.newRuntimeException(e);
        }
    }

    public static void close(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw Exceptions.newRuntimeException(e);
        }
    }

    public static void close(ResultSet resultSet) {
        try {
            if (resultSet != null && !resultSet.isClosed()) {
                resultSet.close();
            }
        } catch (SQLException e) {
            throw Exceptions.newRuntimeException(e);
        }
    }

    public static List<String> getColumnNameList(ResultSetMetaData md) {
        try {
            List<String> list = new ArrayList();
            for (int i = 1; i <= md.getColumnCount(); i++) {
                list.add(md.getColumnName(i));
            }
            return list;
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        }
    }

    public static List<String> getColumnTypeNameList(ResultSetMetaData md) {
        try {
            List<String> list = new ArrayList();
            for (int i = 1; i <= md.getColumnCount(); i++) {
                list.add(md.getColumnTypeName(i));
            }
            return list;
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        }
    }

    public static void populateFields(ResultSet rs, Object bean) {
        try {
            Map<String, Field> fieldMap = Beans.getFieldMap(bean.getClass());
            for (String columnName : getColumnNameList(rs.getMetaData())) {
                Field field = fieldMap.get(columnName);
                if (field != null) {
                    Object value = rs.getObject(field.getName());
                    if (value != null) {
                        if (field.getType() == String.class) {
                            value = value.toString();
                        }
                        if (field.getType().isAssignableFrom(value.getClass())) {
                            field.setAccessible(true);
                            field.set(bean, value);
                        } else {
                            logger.info("populateFields", field.getType(), value.getClass(), value);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        }
    }

    public static ResultSetMetaData getMetaData(RowSet rowSet) {
        try {
            return rowSet.getMetaData();
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        }
    }

    public static ResultSetMetaData getMetaData(ResultSet resultSet) {
        try {
            return resultSet.getMetaData();
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        }
    }

    public static RowSet getRowSet(QueryInfo queryInfo) {
        try {
            return new QueryExecutor().execute(queryInfo);
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        }
    }

    public static RowSet getRowSet(Connection connection, String query) throws Exception {
        Statement statement = connection.createStatement();
        ResultSet res = statement.executeQuery(query);
        CachedRowSet rowSet = new CachedRowSetImpl();
        rowSet.populate(res);
        res.close();
        statement.close();
        return rowSet;
    }

    public static int executeUpdate(Connection connection, String query) throws Exception {
        Statement statement = connection.createStatement();
        int updateCount = statement.executeUpdate(query);
        statement.close();
        return updateCount;
    }

    public static RowSet getRowSet(ResultSet res) throws Exception {
        CachedRowSet rowSet = new CachedRowSetImpl();
        rowSet.populate(res);
        res.close();
        return rowSet;
    }

    public static Object getObject(QueryInfo queryInfo) {
        try {
            RowSet set = new QueryExecutor().execute(queryInfo);
            if (set.next()) {
                return set.getObject(1);
            }
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        }
        return null;
    }

    public static BigDecimal getBigDecimal(QueryInfo queryInfo) {
        try {
            RowSet set = new QueryExecutor().execute(queryInfo);
            if (set.next()) {
                return set.getBigDecimal(1);
            }
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        }
        return null;
    }

    public static String getSizeInfo(QueryInfo queryInfo) throws SQLException {
        try {
            return String.format("%s (%d)", queryInfo.getQueryName(), getRowCount(getRowSet(queryInfo)));
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        }
    }

    public static int getRowCount(RowSet rowSet) throws SQLException {
        int count = 0;
        rowSet.first();
        while (rowSet.next()) {
            count++;
        }
        return count;
    }

    public static boolean findRow(RowSet rowSet, String columnName, Object value) throws SQLException {
        rowSet.first();
        while (rowSet.next()) {
            Object v = rowSet.getObject(columnName);
            if (v != null && v.equals(value)) {
                return true;
            }
        }
        return false;
    }

    public static <T> List<T> getList(QueryInfo queryInfo, Class<T> beanClass) {
        try {
            return getList(getRowSet(queryInfo), beanClass);
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        }
    }

    public static <T> T get(QueryInfo queryInfo, Class<T> beanClass) {
        try {
            List<T> list = getList(queryInfo, beanClass);
            if (list.isEmpty()) {
                logger.info(queryInfo.getQuery());
                return null;
            }
            if (list.size() == 1) {
                return list.get(0);
            }
            throw new ArgsRuntimeException(beanClass, queryInfo.getQueryName(), list.size());
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        }
    }

    public static <T> T find(QueryInfo queryInfo, Class<T> beanClass) {
        try {
            List<T> list = getList(queryInfo, beanClass);
            if (list.size() == 1) {
                return list.get(0);
            }
            throw new ArgsRuntimeException(beanClass, queryInfo.getQueryName(), list.size());
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        }
    }

    public static <T> T getLast(QueryInfo queryInfo, Class<T> beanClass) {
        try {
            List<T> list = getList(queryInfo, beanClass);
            if (list.size() > 0) {
                return list.get(list.size() - 1);
            }
            throw new ArgsRuntimeException(beanClass, queryInfo.getQueryName(), list.size());
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        }
    }

    public static <T> List<T> getList(ResultSet rs, Class<T> beanClass) {
        try {
            List<T> list = new ArrayList();
            while (rs.next()) {
                T bean = (T) beanClass.newInstance();
                populateFields(rs, bean);
                list.add(bean);
            }
            return list;
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        }
    }

    public static void populate(RowSet rs, Map map) {
        try {
            for (String columnName : getColumnNameList(rs.getMetaData())) {
                map.put(columnName, rs.getObject(columnName));
            }
        } catch (SQLException e) {
            throw Exceptions.newRuntimeException(e);
        }
    }

    public static List getList(RowSet set, String columnName) throws SQLException {
        List list = new ArrayList();
        set.beforeFirst();
        while (set.next()) {
            list.add(set.getObject(columnName));
        }
        return list;
    }

    public static <T> List<T> getList(RowSet set, Class<T> beanClass) throws Exception {
        List<T> list = new ArrayList();
        set.beforeFirst();
        Map<String, PropertyDescriptor> propertyMap = Beans.getPropertyMap(beanClass);
        List<String> columnNames = getColumnNameList(set.getMetaData());
        while (set.next()) {
            T bean = beanClass.newInstance();
            for (String columnName : columnNames) {
                String propertyName = Strings.toCamelCase(columnName);
                PropertyDescriptor property = propertyMap.get(propertyName);
                if (property != null) {
                    Beans.convert(bean, property, set.getObject(columnName));
                }
            }
            list.add(bean);
        }
        return list;
    }

    public static void putAll(RowSet rowSet, Map<String, String> map) {
        try {
            ResultSetMetaData md = rowSet.getMetaData();
            for (int i = 1; i <= md.getColumnCount(); i++) {
                Object value = rowSet.getObject(i);
                if (value != null) {
                    map.put(md.getColumnName(i), rowSet.getObject(i).toString());
                }
            }
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        }
    }

    static Connection getConnection(QueryInfo queryInfo) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
