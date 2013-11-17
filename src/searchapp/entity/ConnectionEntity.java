package searchapp.entity;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import searchapp.util.storage.AbstractEntity;
import vellum.parameter.StringMap;

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

    public ConnectionEntity(StringMap map) {
        this.connectionName = map.get("connectionName");
        this.driver = map.get("driver");
        this.url = map.get("url");
        this.user = map.get("user");
        this.password = map.get("password");
    }
    
    public Map toMap() {
        Map map = new HashMap();
        map.put("connectionName", connectionName);
        map.put("driver", driver);
        map.put("url", url);
        map.put("user", user);
        map.put("password", password);
        return map;
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

    @Override
    public String toString() {
        return toMap().values().toString();
    }
    
    
    public Connection getConnection() throws SQLException {
        if (connection == null) {
            connection = DriverManager.getConnection(url, user, password);
        }
        return connection;
    }

    public boolean isValid() {
        if (connectionName == null || connectionName.isEmpty()) {
            return false;
        }
        if (driver == null || driver.isEmpty()) {
            return false;
        }
        if (url == null || url.isEmpty()) {
            return false;
        }
        if (user == null || user.isEmpty()) {
            return false;
        }
        return true;
    }
}
