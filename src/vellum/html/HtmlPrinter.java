/*
 * Source https://code.google.com/p/vellum by @evanxsummers 2011, iPay (Pty) Ltd
 */
package vellum.html;

import java.io.OutputStream;
import vellum.printer.PrintStreamAdapter;
import vellum.printer.Printer;
import vellum.printer.PrinterDelegator;
import vellum.util.Types;

/**
 *
 * @author evan.summers
 */
public class HtmlPrinter extends PrinterDelegator {

    int index = 0;
    int columnCount = 0;

    public HtmlPrinter(OutputStream stream) {
        super(new PrintStreamAdapter(stream));
    }
    
    public HtmlPrinter(Printer out) {
        super(out);
    }
    
    public void h(int i, String text) {
        String element = "h" + i;
        out.printf("<%s>%s</%s>", element, text, element);
    }

    public void divId(String id) {
        out.printf("<div id='%s'>\n", id);
    }
    
    public void div(String style) {
        out.printf("<div class='%s'>\n", style);
    }

    public void pf(String format, Object ... args) {
        out.printf("<p>%s</p>\n", String.format(format, args));
    }
    
    public void divClose() {
        out.printf("</div>\n");
    }

    public void span(String style, String string) {
        out.printf("<span class='%s'>%s</span>\n", style, string);
    }
    
    public void aClosed(String href, String text) {
        out.printf("<a href='%s'>%s</a>\n", href, text);
    }
    
    public void a(String href) {
        out.printf("<a href='%s'>\n", href);
    }

    public void img(String src) {
        out.printf("<img src='%s'/>\n",src);
    }

    public void aimg(String href, String src) {
        out.printf("<a href='%s'><img src='%s'/></a>\n", href, src);
    }
    
    public void a(String style, String href) {
        out.printf("<a class='%s' href='%s'>\n", style, href);
    }
    
    public void aClose() {
        out.printf("</a>\n");
    }
    
    public void tableDiv(String style) {
        out.printf("<div class='%s'>\n", style);
        out.printf("<table class='%s'>\n", style);
        index = 0;
    }

    public void table() {
        out.printf("<table>\n");
        index = 0;
    }
    
    public void table(String style) {
        out.printf("<table class='%s'>\n", style);
        index = 0;
    }
    
    public void thead() {
        out.printf("<thead>\n");
    }

    public void tbody() {
        out.printf("<tbody>\n");
    }

    public void trh(String... names) {
        thead();
        trh();
        columnCount = names.length;
        for (String name : names) {
            th(name);
        }
        theadClose();
    }

    public void trh() {
        out.printf("<tr>\n");
    }

    public void th(String string) {
        out.printf("<th>%s</th>\n", string);
    }

    public void thh(String string) {
        out.printf("<th class='sub'>%s</th>\n", string);
    }

    public void trhh() {
        out.printf("<tr>\n");
    }

    public void theadClose() {
        out.printf("</thead>\n");
        out.flush();
    }

    public void tr0() {
        out.printf("<tr class='row%d'>\n", 0);
    }
    
    public void tr() {
        out.printf("<tr class='row%d'>\n", index++ % 2);
    }

    public void td(String type, Object value) {
        out.printf("<td class='%sCell'>%s</td>\n", type, Types.formatDisplay(value));
    }

    public void trd(Object... values) {
        tr();
        for (Object value : values) {
            if (value == null) {
                td("null", "");
            } else {
                td(Types.getStyleClass(value.getClass()), value);
            }
        }
        trClose();
    }

    public void trhd(String label, Object value) {
        tr();
        out.printf("<td class='rowLabel'>%s</td>\n", label);
        out.printf("<td>%s</td>\n", value);
        trClose();
    }
    
    public void tdClose() {
        out.printf("</td>\n");
    }
    
    public void trClose() {
        out.printf("</tr>\n");
    }
    
    public void tbodyClose() {
        out.printf("</tbody>\n");
    }

    public void tableClose() {
        out.printf("</table>\n");
    }

    public void tableDivClose() {
        out.printf("</div>\n");
        out.printf("</table>\n");
    }
    
    public int getIndex() {
        return index;
    }

    public void pre(String string) {
        if (string != null) {
            out.printf("<pre>");
            out.printf(string);
            out.printf("</pre>\n");
        }
    }

    public void form() {
        out.printf("<form>\n");
    }

    public void formClose() {
        out.printf("</form>\n");
    }

    public void input(String name, String type, Object value) {
        out.printf("<input name='%s' type='%s' value='%s'/>\n", name, type, value);
    }
    
    public void textarea(String name, int rows, int columns, String text) {
        if (text == null) text = "";
        out.printf("<textarea name='%s' rows='%d' cols='%d'>%s</textarea>\n", name, rows, columns, text);
    }

}
