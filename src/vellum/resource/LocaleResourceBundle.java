/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package vellum.resource;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class LocaleResourceBundle {
    Locale locale;
    Map<Class, ResourceBundle> map = new HashMap();

    public LocaleResourceBundle(Locale locale) {
        this.locale = locale;
    }
        
    public ResourceBundle getBundle(Class type) {
        ResourceBundle bundle = map.get(type);
        if (bundle == null) {
            bundle = ResourceBundle.getBundle("/vellum/resource/" + type.getSimpleName());
            map.put(type, bundle);
        }
        return bundle;
    }
}