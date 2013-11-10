/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package cromapp;

/**
 *
 * @author evan.summers
 */
public enum StatusType {
    OK,
    WARNING,
    CRITICAL,
    UNKNOWN;

    public boolean isAlertable() {
        return (this == OK || this == CRITICAL);
    }
}
