package vellumtest.util;

/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */



/**
 *
 * @author evan.summers
 */
public class Woohoo {

    public static void assertTrue(String message, boolean condition) {
        if (!condition) {
            System.err.printf("D'oh! %s\n", message);
        }
    }
    
    public static void assertEquals(String message, Object expected, Object actual) {
        if (actual.equals(expected)) {
            System.out.printf("%s: Woohoo! %s == %s\n", message, expected, actual);
        } else {
            System.out.printf("%s: D'oh! %s != %s\n", message, expected, actual);
        }
    }
    
}
