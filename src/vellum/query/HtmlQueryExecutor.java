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
import vellum.printer.Printer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Date;
import javax.sql.RowSet;
import vellum.format.ArgFormats;
import vellum.format.CalendarFormats;

/**
 *
 * @author evan.summers
 */
public class HtmlQueryExecutor {

    Logr logger = LogrFactory.getLogger(getClass());
    QueryInfo queryInfo;
    Connection connection;
    Statement statement;
    int port;
    Printer out;
    boolean outputQuery;
    RowSet set;
    int resultCount;

    public HtmlQueryExecutor(int port, Printer out, boolean outputQuery) {
        this.port = port;
        this.out = out;
        this.outputQuery = outputQuery;
    }

    public void outputQuery(QueryInfo queryInfo) throws Exception {
        this.queryInfo = queryInfo;
        logger.info(null, queryInfo);
        out.printf("<p><pre class='connectInfo'><b>%s</b> [%s:%d, %s]</pre>\n", queryInfo.getQueryName(), queryInfo.getDatabase(), port, queryInfo.getUser());
        out.printf("<pre class='queryInfo'>%s</pre>\n", queryInfo.getQuery());
        out.flush();
    }

    public RowSet execute(QueryInfo queryInfo) throws Exception {
        this.queryInfo = queryInfo;
        logger.info(null, queryInfo);
        if (outputQuery) {
            outputQuery(queryInfo);
        }
        long startTime = System.currentTimeMillis();
        try {
            executeInner(queryInfo);
            long millis = (System.currentTimeMillis() - startTime);
            if (outputQuery) {
                String results = "no results";
                if (resultCount == 1) {
                    results = "1 result";
                } else if (resultCount > 1) {
                    results = String.format("%d results", resultCount);
                }
                String time = String.format("%dms", millis);
                long seconds = millis / 1000;
                if (seconds > 5) {
                    time = String.format("%d seconds</div>\n", seconds);
                }
                out.printf("<div class='resultInfo'>%s in %s</div>\n", results, time);
                out.flush();
            }
            return set;
        } catch (Exception e) {
            logger.warn(null, queryInfo, queryInfo.getQuery());
            throw Exceptions.newRuntimeException(e, queryInfo);
        } finally {
        }
    }

    protected void executeInner(QueryInfo queryInfo) throws Exception {
        connection = RowSets.getConnection(queryInfo);
        statement = connection.createStatement();
        ResultSet res = statement.executeQuery(queryInfo.getQuery());
        ResultSetMetaData md = res.getMetaData();
        set = RowSets.getRowSet(res);
        statement.close();
        connection.close();
        queryInfo.setRowSet(set);
        out.printf("<table class='resultSet'>\n");
        out.printf("<thead>\n");
        for (int index = 1; index <= md.getColumnCount(); index++) {
            out.printf("<th>%s\n", md.getColumnName(index));
        }
        if (outputQuery) {
            out.printf("<tr>\n");
            for (int index = 1; index <= md.getColumnCount(); index++) {
                out.printf("<th class='sub'>%s\n", md.getColumnTypeName(index));
            }
        }
        out.printf("</thead>\n");
        out.printf("<tbody>\n");
        resultCount = 0;
        while (set.next()) {
            out.printf("<tr class='row%d'>\n", resultCount % 2);
            for (int index = 1; index <= md.getColumnCount(); index++) {
                String typeName = md.getColumnTypeName(index);
                Object value = set.getObject(index);
                String string = ArgFormats.displayFormatter.format(value);
                if (string.endsWith(".0")) {
                    string = string.substring(0, string.length() - 2);
                } else if (typeName.equals("date")) {
                    string = CalendarFormats.dateFormat.format((Date) value);
                }
                out.printf("<td class='%sCell'>%s\n", typeName, string);
            }
            resultCount++;
        }
        out.printf("</tbody>\n");
        out.printf("</table>\n");
        set.beforeFirst();
    }
}
