
package searchapp.entity;

import java.util.Collection;
import searchapp.util.storage.AbstractEntity;
import vellum.type.ComparableTuple;

/**
 *
 * @author evans
 */
public class Matches extends AbstractEntity {
    private String sessionId;
    private String searchString;
    private Collection<Match> matches;

    public Matches() {
    }

    public Matches(String sessionId, String searchString, Collection<Match> matches) {
        this.sessionId = sessionId;
        this.searchString = searchString;
        this.matches = matches;
    }

    public Comparable getKey() {
        return ComparableTuple.create(sessionId, searchString);
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getSearchString() {
        return searchString;
    }

    public Collection<Match> getMatches() {
        return matches;
    }        
}
