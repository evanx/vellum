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
package searchapp.test;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import searchapp.app.SearchApp;
import searchapp.entity.ConnectionEntity;
import searchapp.entity.Match;
import searchapp.search.SearchConnection;
import searchapp.util.ssl.EphemeralSSLContext;
import vellum.util.Streams;

/**
 *
 * @author evan.summers
 */
public class SearchAppTest {

    static Logger logger = LoggerFactory.getLogger(SearchAppTest.class);
    SearchApp app;

    ConnectionEntity[] connectionEntities = {
        new ConnectionEntity("connection1", "org.h2.Driver", "jdbc:h2:mem:", "sa", null),
        new ConnectionEntity("connection2", "org.h2.Driver", "jdbc:h2:mem:", "sa", null)
    };

    public SearchAppTest(SearchApp app) {
        this.app = app;
    }
        
    public void test() throws Exception {
        for (ConnectionEntity connectionEntity : connectionEntities) {
            app.getStorage().getConnectionStorage().insert(connectionEntity);
            Connection connection = connectionEntity.getConnection();
            connection.createStatement().execute(Streams.readResourceString(getClass(),
                    connectionEntity.getConnectionName() + ".sql"));
            logger.info("catalog {}", connection.getCatalog());
            print(connection.getMetaData().getColumns(
                    connection.getCatalog(), "PUBLIC", "%", "%"));
            logger.info("select {}", app.getStorage().getConnectionStorage().select(
                    connectionEntity.getConnectionName()));
            for (Match match : new SearchConnection(connectionEntity, "Evan").search()) {
                logger.info("match: {}", match);
            }
        }
        HttpsURLConnection urlConnection = new EphemeralSSLContext().createConnection(
                "client", new URL("https://localhost:8443/shutdown"));
        urlConnection.connect();
    }                
    
    private void print(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        List<String> columnNames = new ArrayList();
        for (int i = 1; i < metaData.getColumnCount(); i++) {
            columnNames.add(metaData.getColumnName(i));
        }
        logger.info("columns: {}", columnNames);
        while (resultSet.next()) {
            List columns = new ArrayList();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                if (metaData.getColumnClassName(i).equals(String.class.getName())) {
                    columns.add(resultSet.getObject(i));
                }
            }
            logger.info("row: {}", columns);
        }        
    }
}
