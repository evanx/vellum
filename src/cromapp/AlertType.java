/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package cromapp;

/**
 *
 * @author evan.summers
 */
public enum AlertType {
    NOT_OK,
    WARN,
    ERROR,
    PATTERN,
    STATUS_CHANGED,
    OUTPUT_CHANGED,
    ALWAYS;
    
    public static AlertType parse(String string) {
        for (AlertType type : values()) {
            if (string.equalsIgnoreCase(type.name())) {
                return type;
            }            
        }        
        return null;
    }
}
