/*
 * 
 */
package vellum.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import vellum.type.ComparableTuple;

/**
 *
 * @author evan.summers
 */
public class ConfigMap extends HashMap<ComparableTuple, ConfigEntry> {
    List<ConfigEntry> entryList = new ArrayList();

    public ConfigEntry find(String type, String name) {
        ConfigEntry entry = get(ComparableTuple.create(type, name));
        if (entry == null) {
            throw new ConfigException(ConfigExceptionType.NOT_FOUND, type, name);
        }
        return entry;
    }

    public ConfigEntry get(String type, String name) {
        ConfigEntry entry = get(ComparableTuple.create(type, name));
        if (entry == null) {
            entry = new ConfigEntry(type, name);
        }
        return entry;
    }
    
    public ConfigEntry put(ConfigEntry entry) {
        entryList.add(entry);
        return super.put(entry.getKey(), entry);
    }

    public void putAll(ConfigMap configMap) {
        entryList.addAll(configMap.entryList);
        super.putAll(configMap);
    }

    public List<ConfigEntry> getList(String type) {
        List<ConfigEntry> list = new ArrayList();
        for (ConfigEntry entry: entryList) {
            if (entry.getType().equalsIgnoreCase(type)) {
                list.add(entry);
            }
        }
        return list;
    }

    public List<ConfigEntry> getEntryList() {
        return entryList;
    }
}
