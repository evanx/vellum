
package searchapp.entity;

import vellum.util.Args;

/**
 *
 * @author evans
 */
public class Match {
    private String connectionName;
    private String tableName;
    private String columnName;
    private String content;
    private Long rowId;

    public Match() {
    }
    
    public Match(String connectionName, String tableName, String columnName, 
            String content, Long rowId) {
        this.connectionName = connectionName;
        this.tableName = tableName;
        this.columnName = columnName;
        this.content = content;
        this.rowId = rowId;
    }
    
    public String getConnectionName() {
        return connectionName;
    }

    public String getTableName() {
        return tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public Long getRowId() {
        return rowId;
    }
    
    @Override
    public String toString() {
        return Args.format(connectionName, tableName, columnName, rowId, content);
    }
}
