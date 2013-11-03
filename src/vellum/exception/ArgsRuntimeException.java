/*
 * Apache Software License 2.0
 * Supported by iPay (Pty) Ltd, BizSwitch.net   
 */
package vellum.exception;

/**
 * Utility methods related to using loggers.
 *
 * @author evan.summers
 */
public class ArgsRuntimeException extends RuntimeException {
    Object[] args;
    
    public ArgsRuntimeException(Object ... args) {
        super(Exceptions.getMessage(args), Exceptions.getThrowable(args));
        this.args = args;
    }

}
