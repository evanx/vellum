/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package vellum.entity;

/**
 *
 * @author evan.summers
 */
public abstract class AbstractLongIdEntity extends AbstractIdEntity {
    protected Long id;
    
    @Override
    public Comparable getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
       
}
