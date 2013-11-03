/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package vellum.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author evan.summers
 */
public class SimpleConnectionPool implements ConnectionPool {

    DataSourceConfig dataSourceInfo;
    Queue<ConnectionEntry> availableQueue = new LinkedList();
    Queue<ConnectionEntry> takenQueue = new LinkedList();
    int poolSize = 0;
    int takenCount = 0;
    int releasedCount = 0;
    int validTimeoutSeconds = 2;    

    public SimpleConnectionPool(DataSourceConfig dataSourceInfo) {
        if (dataSourceInfo.getPoolSize() != null) {
            this.poolSize = dataSourceInfo.getPoolSize();
        }
        this.dataSourceInfo = dataSourceInfo;
    }

    @Override
    public synchronized ConnectionEntry takeEntry() throws SQLException {
        ConnectionEntry connectionEntry = availableQueue.poll();
        if (connectionEntry != null) {
            Connection connection = connectionEntry.getConnection();
            if (connection != null) {
                try {
                    if (connection.isClosed()) {
                        close(connectionEntry);
                    } else if (!connection.isValid(validTimeoutSeconds)) {
                        close(connectionEntry);
                    }
                } catch (SQLException e) {
                    close(connectionEntry);
                    throw new StorageRuntimeException(StorageExceptionType.CONNECTION_ERROR, e);
                }
            }
        }
        if (connectionEntry == null) {
            try {
                Connection connection = DriverManager.getConnection(
                        dataSourceInfo.getUrl(), dataSourceInfo.getUser(), dataSourceInfo.getPassword());
                connectionEntry = new ConnectionEntry(connection);
            } catch (SQLException e) {
                throw new StorageRuntimeException(StorageExceptionType.CONNECTION_ERROR, e);
            }
        }
        takenCount++;
        connectionEntry.taken();
        return connectionEntry;
    }

    @Override
    public synchronized void releaseConnection(ConnectionEntry connectionEntry) {
        releasedCount++;
        connectionEntry.returned();
        takenQueue.remove(connectionEntry);
        if (connectionEntry.isOk() && availableQueue.size() < poolSize) {
            if (availableQueue.offer(connectionEntry)) {
                return;
            }
        }
        close(connectionEntry);
    }

    static void close(ConnectionEntry connectionEntry) {
        try {
            if (!connectionEntry.isClosed()) {
                connectionEntry.getConnection().close();
            }
        } catch (SQLException e) {
            throw new StorageRuntimeException(StorageExceptionType.CONNECTION_ERROR, e);
        }
    }
}
