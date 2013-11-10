/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package vellum.config;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import vellum.datatype.Millis;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import vellum.util.Bytes;
import vellum.util.DefaultDateFormats;


/**
 *
 * @author evan.summers
 */
public class ConfigMap extends HashMap<String, String> {
    Logr logger = LogrFactory.getLogger(ConfigMap.class);
    
    private String get(String name) {
        String string = super.get(name);
        logger.trace("get", name, string);
        return string;
    }
    
    public String getString(String name, String defaultValue) {
        if (!containsKey(name)) {
            return defaultValue;
        }
        return get(name);
    }

    public String findString(String name) {
        return get(name);
    }
    
    public String getString(String name) {
        if (!containsKey(name)) {
            throw new ConfigException(ConfigExceptionType.NOT_FOUND, name);
        }
        return get(name);
    }
    
    public Integer getInt(String name, Integer defaultValue) {
        if (!containsKey(name)) {
            return defaultValue;
        }
        return new Integer(get(name));
    }
    
    public int getInt(String name) {
        if (!containsKey(name)) {
            throw new ConfigException(ConfigExceptionType.NOT_FOUND, name);
        }
        return Integer.parseInt(get(name));
    }

    public Long getLong(String name, Long defaultValue) {
        if (!containsKey(name)) {
            return defaultValue;
        }
        return new Long(get(name));
    }
    
    public long getLong(String name) {
        if (!containsKey(name)) {
            throw new ConfigException(ConfigExceptionType.NOT_FOUND, name);
        }
        return Long.parseLong(get(name));
    }

    public boolean getBoolean(String name) {
        return getBoolean(name, false);
    }

    public boolean getBoolean(String name, boolean defaultValue) {
        if (!containsKey(name)) {
            return defaultValue;
        }
        return Boolean.parseBoolean(get(name));
    }

    public List<String> splitCsv(String name) {
        List<String> list = new ArrayList();
        if (containsKey(name)) {
            for (String string : get(name).split(",")) {
                list.add(string.trim());
            }
        }
        return list;
    }

    public <T extends Enum<T>> T getEnum(String key, Class<T> enumType, T defaultValue) {
        String name = get(key);
        if (name == null) {
            return defaultValue;
        }
        return Enum.valueOf(enumType, name);
    }

    public Long getMillis(String name, Long defaultMillis) {
        if (!containsKey(name)) return defaultMillis;
        return Millis.parse(get(name));
    }

    public long getMillis(String name) {
        String string = get(name);
        if (string == null) {
            throw new ConfigException(ConfigExceptionType.NOT_FOUND, name);
        }
        return Millis.parse(string);
    }

    public Date getTime(String name, Date defaultTime) {
        return DefaultDateFormats.timeSecondsFormat.parse(get(name), defaultTime);
    }

    public long getByteSize(String name, Long defaultSize) {
        return Bytes.parseConfig(get(name), defaultSize);
    }
}
