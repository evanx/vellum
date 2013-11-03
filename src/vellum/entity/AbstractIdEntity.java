/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package vellum.entity;

/**
 *
 * @author evan.summers
 */
public abstract class AbstractIdEntity<T extends Comparable> implements IdEntity<T>, Comparable<IdEntity> {

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IdEntity) {
            IdEntity other = (IdEntity) obj;
            return Comparables.equals(getId(), other.getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
    @Override
    public int compareTo(IdEntity o) {
        return Comparables.compareTo(getId(), o.getId());
    }
    
    @Override
    public String toString() {
        if (getId() == null) return getClass().getSimpleName();
        return getId().toString();
    }
        
}
