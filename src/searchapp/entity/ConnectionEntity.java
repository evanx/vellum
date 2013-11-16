package searchapp.entity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import searchapp.util.storage.AbstractEntity;

/**
 *
 * @author evans
 */
public class ConnectionEntity extends AbstractEntity {

    private String connectionName;
    private String driver;
    private String url;
    private String user;
    private String password;
    private transient Connection connection;
    
    public ConnectionEntity() {
    }
    
    public ConnectionEntity(String connectionName, String driver, String url, 
            String user, String password) {
        this.connectionName = connectionName;
        this.driver = driver;
        this.url = url;
        this.user = user;
        this.password = password;
    }

    @Override
    public String getKey() {
        return connectionName;
    }
    
    public String getConnectionName() {
        return connectionName;
    }

    public String getDriver() {
        return driver;
    }

    public Map toMap() {
        Map map = new HashMap();
        map.put("connectionName", connectionName);
        return map;
    }
    public Connection getConnection() throws SQLException {
        if (connection == null) {
            connection = DriverManager.getConnection(url, user, password);
        }
        return connection;
    }
}
