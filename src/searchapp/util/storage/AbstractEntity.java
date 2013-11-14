/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package searchapp.util.storage;

import vellum.entity.*;

/**
 *
 * @author evan.summers
 */
public abstract class AbstractEntity implements Comparable<AbstractEntity> {

    public abstract Comparable getKey();
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AbstractEntity) {
            AbstractEntity other = (AbstractEntity) obj;
            return Comparables.equals(getKey(), other.getKey());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
    @Override
    public int compareTo(AbstractEntity o) {
        return Comparables.compareTo(getKey(), o.getKey());
    }
    
    @Override
    public String toString() {
        if (getKey() == null) return getClass().getSimpleName();
        return getKey().toString();
    }
        
}
