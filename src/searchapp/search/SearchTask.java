
package searchapp.search;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import searchapp.entity.ConnectionEntity;
import searchapp.entity.MatchEntity;

/**
 *
 * @author evans
 */
public class SearchTask {
    
    Collection<ConnectionEntity> connections;
    String searchString;
    Collection<MatchEntity> matches = new ArrayList();
    
    public SearchTask(Collection<ConnectionEntity> connections, String searchString) {
        this.searchString = searchString;
        this.connections = Collections.unmodifiableCollection(connections);
    }
    
    public void search() throws SQLException {
        for (ConnectionEntity connectionEntity: connections) {
            matches.addAll(new SearchConnection(connectionEntity, searchString).search());
        }
        
    }
        
}
