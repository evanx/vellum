/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package vellum.exception;

import vellum.util.Args;

/**
 *
 * @author evan.summers
 */
public class EnumExceptions {
    
    public static String formatMessage(Enum exceptionType) {
        return exceptionType.name();
    }

    public static String formatMessage(Enum exceptionType, Object[] args) {
        return exceptionType.name() + " (" + Args.format(args) + ")";
    }

    public static String formatMessage(Throwable exception, Enum exceptionType) {
        return exceptionType.name() + " (" + exception.getMessage() + ")";
    }
    
    public static EnumRuntimeException newRuntimeException(Enum exceptionType) {
        return new EnumRuntimeException(exceptionType);
    }
    
    public static EnumException newException(Enum exceptionType) {
        return new EnumException(exceptionType);
    }
    
}
