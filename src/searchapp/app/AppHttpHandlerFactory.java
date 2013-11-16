/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package searchapp.app;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import searchapp.util.httphandler.HttpHandlerFactory;
import searchapp.util.httphandler.ShutdownHttpHandler;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;

/**
 *
 * @author evan.summers
 */
public class AppHttpHandlerFactory implements HttpHandlerFactory {

    Logr logger = LogrFactory.getLogger(AppHttpHandlerFactory.class);
    SearchApp app;

    public AppHttpHandlerFactory(SearchApp app) {
        this.app = app;
    }

    @Override
    public HttpHandler getHandler(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        logger.info("path", path);
        if (path.equals("/shutdown")) {
            return new ShutdownHttpHandler(app);
        } else if (path.equals("/search")) {
            return new SearchHttpHandler(app);
        }
        return null;
    }

    @Override
    public boolean filter(HttpExchange exchange) {
        return true;
    }
}
