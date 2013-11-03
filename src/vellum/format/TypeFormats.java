/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package vellum.format;

import java.util.Collection;
import java.util.Date;
import vellum.util.DefaultDateFormats;
import vellum.util.Lists;
import vellum.util.Strings;
/**
 *
 * @author evan.summers
 */
public class TypeFormats {

    public static TypeFormats formatter = new TypeFormats(false);
    public static TypeFormats verboseFormatter = new TypeFormats(true);
    public static TypeFormats displayFormatter = new TypeFormats(true);
    
    static {
        verboseFormatter.verbose = true;
        displayFormatter.displayable = true;
    }
    
    boolean displayable = false;
    boolean verbose = false;
    
    TypeFormats(boolean displayable) {
        this.displayable = displayable;
    }
    
    public String format(Object arg) {
        if (arg == null) {
            if (displayable) return "";
            return "null";
        } else if (arg instanceof Class) {
            return ((Class) arg).getSimpleName();
        } else if (arg instanceof Date) {
            return DefaultDateFormats.timeMillisFormat.format((Date) arg);
        } else if (Strings.isEmpty(arg.toString())) {
            return "empty";
        } else if (arg instanceof byte[]) {
            return String.format("[%s]", Lists.format(Lists.toList((byte[]) arg)));
        } else if (arg instanceof Object[]) {
            return String.format("[%s]", Lists.format((Object[]) arg));
        } else if (arg instanceof String[]) {
            return String.format("[%s]", Lists.format((String[]) arg));
        } else if (arg instanceof Collection) {
            return String.format("[%s]", Lists.format((Collection) arg));
        } else {
            return arg.toString();
        }
    }

}
