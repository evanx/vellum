/*
 * 
 */
package vellum.storage;

/**
 *
 * @author evan.summers
 */
public enum StorageExceptionType {
    ALREADY_EXISTS,
    DUPLICATE,
    NULL_ID,
    NOT_FOUND, 
    NO_KEY,
    NOT_INSERTED,
    NOT_UPDATED,
    UPDATE_COUNT,
    MULTIPLE_RESULTS,
    NOT_DELETED, 
    CONNECTION_ERROR,
    DISABLED;

}
