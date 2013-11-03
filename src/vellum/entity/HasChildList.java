/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package vellum.entity;

import java.util.List;

/**
 *
 * @author evan.summers
 */
public interface HasChildList<T> {
    public List<T> getChildList();
    
}
