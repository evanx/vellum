/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package vellum.entity;

import vellum.config.ConfigProperties;

/**
 *
 * @author evan.summers
 */
public interface ConfigurableEntity<C> extends IdEntity, Named {
    public void setName(String name);
    public void config(C context, ConfigProperties properties);
}
