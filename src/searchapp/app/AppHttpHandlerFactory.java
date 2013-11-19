/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package searchapp.app;

import searchapp.handler.ListConnectionsHttpHandler;
import searchapp.handler.SearchHttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import searchapp.handler.DeleteConnectionHttpHandler;
import searchapp.handler.InsertConnectionHttpHandler;
import searchapp.handler.ReplaceHttpHandler;
import searchapp.handler.UpdateConnectionHttpHandler;
import searchapp.util.httphandler.HttpHandlerFactory;
import searchapp.util.httphandler.ShutdownHttpHandler;

/**
 *
 * @author evan.summers
 */
public class AppHttpHandlerFactory implements HttpHandlerFactory {

    Logger logger = LoggerFactory.getLogger(AppHttpHandlerFactory.class);
    SearchApp app;

    public AppHttpHandlerFactory(SearchApp app) {
        this.app = app;
    }

    @Override
    public HttpHandler getHandler(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        logger.info("path", path);
        if (filter(exchange)) {
            if (path.equals("/shutdown")) {
                return new ShutdownHttpHandler(app);
            } else if (path.equals("/app/connection/insert")) {
                return new InsertConnectionHttpHandler(app);
            } else if (path.startsWith("/app/connection/update/")) {
                return new UpdateConnectionHttpHandler(app);
            } else if (path.equals("/app/connection/list")) {
                return new ListConnectionsHttpHandler(app);
            } else if (path.startsWith("/app/connection/delete/")) {
                return new DeleteConnectionHttpHandler(app);
            } else if (path.equals("/app/search")) {
                return new SearchHttpHandler(app);
            } else if (path.equals("/app/replace")) {
                return new ReplaceHttpHandler(app);
            }
        }
        return null;
    }

    private boolean filter(HttpExchange exchange) {
        return true;
    }
}
