package searchapp.entity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author evans
 */
public class ConnectionEntity {

    private String connectionName;
    private String url;
    private String user;
    private String password;

    public ConnectionEntity() {
    }
    
    public ConnectionEntity(String connectionName, String url, String user, String password) {
        this.connectionName = connectionName;
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public String getConnectionName() {
        return connectionName;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
