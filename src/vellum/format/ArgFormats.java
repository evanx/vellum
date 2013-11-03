/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package vellum.format;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import vellum.util.Strings;
import vellum.util.Types;

/**
 *
 * @author evan.summers
 */
public class ArgFormats {

    public static ArgFormats formatter = new ArgFormats();
    public static ArgFormats verboseFormatter = new ArgFormats();
    public static ArgFormats displayFormatter = new ArgFormats();

    public static final String DEFAULT_DELIMITER = ", ";
    public static final String COMMA_DELIMITER = ", ";
    public static final String SINGLE_QUOTE = "'";
    public static final String DOUBLE_QUOTE = "\"";
    
    static {
        verboseFormatter.verbose = true;
        displayFormatter.displayable = true;
    }
    
    boolean displayable = true;
    boolean verbose = false;
    String delimiter = COMMA_DELIMITER;
    String quote = DOUBLE_QUOTE;
            
    public ArgFormats() {
    }
    
    public ArgFormats(boolean displayable, String delimiter) {
        this.displayable = displayable;
        this.delimiter = delimiter;
    }
    
    public String format(Object arg) {
        if (arg == null) {
            if (displayable) return "";
            return "null";
        } else if (arg instanceof Class) {
            return ((Class) arg).getSimpleName();
        } else if (arg instanceof Date) {
            return CalendarFormats.timestampFormat.format((Date) arg);
        } else if (Strings.isEmpty(arg.toString())) {
            return "empty";
        } else if (arg instanceof byte[]) {
            return String.format("[%s]", formatArray(toList((byte[]) arg)));
        } else if (arg instanceof Object[]) {
            return String.format("[%s]", formatArray((Object[]) arg));
        } else if (arg instanceof String[]) {
            return String.format("[%s]", formatArray((String[]) arg));
        } else {
            return arg.toString();
        }
    }

    public String formatArgs(Object ... args) {
        return formatArray(args);
    }

    public String formatArray(Collection collection) {
        return formatArray(collection.toArray());
    }
    
    public String formatArray(Object[] args) {
        if (args == null) {
            if (displayable) return "";
            return "null[]";
        }
        StringBuilder builder = new StringBuilder();
        for (Object arg : args) {
            if (builder.length() > 0) {
                builder.append(delimiter);
            }
            String string = format(arg);
            if (string.contains(delimiter)) {
                builder.append("{");
                builder.append(string);
                builder.append("}");
            } else {
                builder.append(string);
            }
        }
        return builder.toString();
    }

    public String formatQuote(Object[] args) {
        if (args == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (Object arg : args) {
            if (arg != null) {
                if (builder.length() > 0) {
                    builder.append(delimiter);
                }
                builder.append(quote);
                builder.append(Types.formatPrint(arg));
                builder.append(quote);
            }
        }
        return builder.toString();
    }
    
    public static String formatVerbose(Object[] args) {
        StringBuilder builder = new StringBuilder();
        for (Object arg : args) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            if (arg == null) {
                builder.append("null");
            } else {
                String string = Types.formatPrint(arg);
                if (Strings.isEmpty(string)) {
                    string = "empty";
                }
                if (arg.getClass() != String.class && !arg.getClass().isPrimitive()) {
                    builder.append("(");
                    builder.append(arg.getClass().getSimpleName());
                    builder.append(") ");
                }
                builder.append(string);
            }
        }
        return builder.toString();
    }

    public List toList(byte[] array) {
        List list = new ArrayList();
        for (byte element : array) {
            list.add(element);
        }
        return list;
    }
    
}
