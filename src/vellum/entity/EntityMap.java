/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package vellum.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author evan.summers
 */
public class EntityMap<I extends Comparable, E extends IdEntity> {

    Map<I, IdEntity> entityMap = new HashMap();
    
    public void put(E entity) {
        entityMap.put((I) entity.getId(), entity);
    }
    
    public E get(I id) {
        return (E) entityMap.get(id);
    }

    public List<E> getExtentList() {
        return new ArrayList(entityMap.values());
    }

    public List<E> getList(Class<E> entityType, Matcher<E> matcher) {
        List<E> entityList = new ArrayList();
        for (E entity : getExtentList()) {
            if (matcher.matches(entity)) {
                entityList.add(entity);
            }
        }
        return entityList;
    }
    
    public List<IdEntity> getEntityList() {
        return new ArrayList(entityMap.values());
    }
}
