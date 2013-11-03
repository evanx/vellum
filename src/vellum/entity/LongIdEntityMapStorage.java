/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package vellum.entity;

import java.sql.SQLException;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;

/**
 *
 * @author evan.summers
 */
public class LongIdEntityMapStorage<I extends Comparable, E extends IdEntity<I>> extends EntityMap<I, E> implements EntityStorage<I, E> {
    protected Logr logger = LogrFactory.getLogger(getClass());
    long lastId = 0;
    
    @Override
    public synchronized I insert(E entity) throws SQLException {
        if (entity.getId() == null) {
            lastId++;
        }
        super.put(entity);
        return entity.getId();
    }

    @Override
    public synchronized void update(E entity) throws SQLException {
        super.put(entity);
    }
    
    @Override
    public E find(I id) throws SQLException {
        return super.get(id);
    }    
}
