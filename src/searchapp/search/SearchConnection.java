
package searchapp.search;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import searchapp.entity.ConnectionEntity;
import searchapp.entity.Match;

/**
 *
 * @author evans
 */
public class SearchConnection {

    static Logger logger = LoggerFactory.getLogger(SearchConnection.class);
    ConnectionEntity connectionEntity;
    Connection connection;
    DatabaseMetaData databaseMetaData;
    Collection<Match> matches = new ArrayList();
    String catalog;
    String table;
    String searchString;
    
    public SearchConnection(ConnectionEntity connectionEntity, String searchString) {
        this.connectionEntity = connectionEntity;
        this.searchString = searchString;
    }
    
    public Collection<Match> search() throws SQLException {
        logger.info("search: connection {}, string [{}]", 
                connectionEntity.getConnectionName(), searchString);
        this.connection = connectionEntity.getConnection();
        try {
            databaseMetaData = connection.getMetaData();
            search(connection.getCatalog(), "PUBLIC");
            return matches;
        } finally {
        }
    }

    private void search(String catalog, String schema) throws SQLException {
        logger.info("search: catalog {}, schema {}", catalog, schema);
        ResultSet resultSet = databaseMetaData.getTables(catalog, schema, null, null);
        while (resultSet.next()) {
            searchTable(resultSet.getString("TABLE_NAME"));
        }
    }

    private void searchTable(String tableName) throws SQLException {
        String sql = "select * from " + tableName;
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        List<String> columnList = getSearchableColumns(resultSet.getMetaData());
        logger.info("searchTable: {} {}", tableName, columnList);
        String rowIdColumnName = getRowIdColumnName(resultSet.getMetaData());
        while (resultSet.next()) {
            for (String columnName : columnList) {
                String string = resultSet.getString(columnName);
                if (string.length() > 500) {
                    string = string.substring(0, 500);
                }
                if (string.contains(searchString)) {
                    matches.add(new Match(connectionEntity.getConnectionName(),
                            tableName, columnName, rowIdColumnName, string,
                            resultSet.getLong(rowIdColumnName)));
                }
            }
        }
    }
    
    public static List<String> getSearchableColumns(ResultSetMetaData metaData) 
            throws SQLException {
        List<String> columnList = new ArrayList();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            logger.trace("check {} {}", metaData.getColumnName(i), 
                    metaData.getColumnClassName(i));
            if (metaData.getColumnClassName(i).equals(String.class.getName())) {
                columnList.add(metaData.getColumnName(i));
            }
        }
        return columnList;
    }
    
    private String getRowIdColumnName(ResultSetMetaData metaData) throws SQLException {
        for (int i = 1; i < metaData.getColumnCount(); i++) {
            if (isRowIdType(metaData.getColumnType(i)) && 
                    metaData.getColumnName(i).endsWith("_ID")) {
                return metaData.getColumnName(i);
            }
        }
        return metaData.getColumnName(1);
    }

    public static boolean isSearchableType(int type) {
        return type == Types.VARCHAR || type == Types.CHAR;
    }

    public static boolean isRowIdType(int type) {
        return type == Types.INTEGER || type == Types.BIGINT;
    }
    
}
