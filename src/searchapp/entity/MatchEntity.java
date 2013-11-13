
package searchapp.entity;

/**
 *
 * @author evans
 */
public class MatchEntity {
    private String connectionName;
    private String tableName;
    private String columnName;
    private Long rowId;

    public MatchEntity() {
    }
    
    public MatchEntity(String connectionName, String tableName, String columnName, Long rowId) {
        this.connectionName = connectionName;
        this.tableName = tableName;
        this.columnName = columnName;
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
}
