/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package vellum.entity;

/**
 *
 * @author evan.summers
 */
public class IdPair implements Comparable<IdPair> {
    Comparable id;
    Comparable otherId;

    public IdPair(Comparable id, Comparable otherId) {
        this.id = id;
        this.otherId = otherId;
    }
    
    @Override
    public int compareTo(IdPair idPair) {
        int value = id.compareTo(idPair.id);
        if (value != 0) {
            return value;
        }
        return otherId.compareTo(idPair.otherId);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode() ^ otherId.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof IdPair) {
            IdPair idPair = (IdPair) object;
            return id.equals(idPair.id) && otherId.equals(idPair.otherId);
        }
        return false;
    }
    
    @Override
    public String toString() {
        return id.toString() + "-" + otherId.toString();
    }
}
