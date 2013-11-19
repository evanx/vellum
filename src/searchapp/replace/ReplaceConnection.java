
package searchapp.replace;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import searchapp.entity.ConnectionEntity;
import searchapp.entity.Match;

/**
 *
 * @author evans
 */
public class ReplaceConnection {

    static Logger logger = LoggerFactory.getLogger(ReplaceConnection.class);
    ConnectionEntity connectionEntity;
    Connection connection;
    DatabaseMetaData databaseMetaData;
    Collection<Match> matches;
    String catalog;
    String table;
    String searchString;
    String replaceString;
    
    public ReplaceConnection(ConnectionEntity connectionEntity, 
            String searchString, String replaceString, Collection<Match> matches) {
        this.connectionEntity = connectionEntity;
        this.searchString = searchString;
        this.replaceString = replaceString;
        this.matches = matches;
    }
    
    public Collection<Match> replace() throws SQLException {
        logger.info("replace {}", connectionEntity.getConnectionName());
        this.connection = connectionEntity.getConnection();
        try {
            for (Match match : matches) {                
                replace(match);
            }
            return matches;
        } finally {
        }
    }

    private int replace(Match match) throws SQLException {
        logger.info("replace {}", match);
        String sql = String.format("update %s set %s = ? where %s = ?", 
                match.getTableName(), match.getColumnName(), match.getIdColumnName());
        PreparedStatement statement = connection.prepareStatement(sql);
        String content = match.getContent().replaceAll(searchString, replaceString);
        statement.setString(1, content);
        statement.setObject(2, match.getRowId());
        match.setContent(content);
        return statement.executeUpdate();
    }

}
