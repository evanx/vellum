
package searchapp.entity;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import vellum.util.Args;

/**
 *
 * @author evans
 */
public class Match {

    private String connectionName;
    private String tableName;
    private String columnName;
    private String idColumnName;
    private String content;
    private Long rowId;

    public Match() {
    }
    
    public Match(JsonObject object) {
        connectionName = object.get("connectionName").getAsString();
        tableName = object.get("tableName").getAsString();
        columnName = object.get("columnName").getAsString();
        idColumnName = object.get("idColumnName").getAsString();
        content = object.get("content").getAsString();
        rowId = object.get("rowId").getAsLong();
    }
    
    public Match(String connectionName, String tableName, String columnName, String idColumnName,
            String content, Long rowId) {
        this.connectionName = connectionName;
        this.tableName = tableName;
        this.columnName = columnName;
        this.idColumnName = idColumnName;
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

    public String getIdColumnName() {
        return idColumnName;
    }
    
    public Long getRowId() {
        return rowId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    
    @Override
    public String toString() {
        return Args.format(connectionName, tableName, columnName, idColumnName, rowId, content);
    }
    
    public static Collection<Match> getCollection(JsonArray array) {
        List<Match> matches = new ArrayList();
        for (int i = 0; i < array.size(); i++) {
            matches.add(new Match(array.get(i).getAsJsonObject()));
        }
        return matches;
    }
    
}
