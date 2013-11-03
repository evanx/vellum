/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package vellum.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author evan.summers
 */
public class ConnectionEntry {
    Connection connection;
    long created = System.currentTimeMillis();
    long offeredTime;
    long releasedTime;
    int offeredCount;
    int releasedCount;
    boolean ok = false;

    public ConnectionEntry(Connection connection) {
        this.connection = connection;
    }
    
    public Connection getConnection() {
        return connection;
    }

    public void taken() {
        ok = false;
        offeredCount++;
        offeredTime = System.currentTimeMillis();
    }
    
    public void returned() {
        releasedCount++;
        releasedTime = System.currentTimeMillis();
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public boolean isOk() {
        return ok;
    }

    public boolean isClosed() throws SQLException {
        return connection == null || connection.isClosed();
    }

    public void close() {
        try {
            if (!isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new StorageRuntimeException(StorageExceptionType.CONNECTION_ERROR, e);
        }
    }
    
    public Statement createStatement() throws SQLException {
        return connection.createStatement();
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }
}
