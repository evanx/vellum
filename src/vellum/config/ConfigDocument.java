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
public class ConfigDocument extends HashMap<ComparableTuple, ConfigSection> {
    List<ConfigSection> entryList = new ArrayList();

    public ConfigSection find(String type, String name) {
        ConfigSection entry = get(ComparableTuple.newInstance(type, name));
        if (entry == null) {
            throw new ConfigException(ConfigExceptionType.NOT_FOUND, type, name);
        }
        return entry;
    }

    public ConfigSection get(String type, String name) {
        ConfigSection entry = get(ComparableTuple.newInstance(type, name));
        if (entry == null) {
            entry = new ConfigSection(type, name);
        }
        return entry;
    }
    
    public ConfigSection put(ConfigSection entry) {
        entryList.add(entry);
        return super.put(entry.getKey(), entry);
    }

    public void putAll(ConfigDocument configMap) {
        entryList.addAll(configMap.entryList);
        super.putAll(configMap);
    }

    public List<ConfigSection> getList(String type) {
        List<ConfigSection> list = new ArrayList();
        for (ConfigSection entry: entryList) {
            if (entry.getType().equalsIgnoreCase(type)) {
                list.add(entry);
            }
        }
        return list;
    }

    public List<ConfigSection> getEntryList() {
        return entryList;
    }
}
