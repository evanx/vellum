/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package vellum.config;

import vellum.type.ComparableTuple;

/**
 *
 * @author evan.summers
 */
public class ConfigSection {
    final String type;
    final String name;
    final ComparableTuple key;
    final ConfigMap properties = new ConfigMap();

    public ConfigSection(String type, String name) {
        this.type = type;
        this.name = name;
        this.key = ComparableTuple.newInstance(type, name);
    }

    public ComparableTuple getKey() {
        return key;
    }
    
    public String getType() {
        return type;
    }
    
    public String getName() {
        return name;
    }

    public ConfigMap getProperties() {
        return properties;
    }
    
    
}
