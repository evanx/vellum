/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package searchapp.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import searchapp.app.SearchApp;
import searchapp.entity.ConnectionEntity;
import searchapp.util.httphandler.HttpExchangeInfo;
import vellum.httpserver.Httpx;
import vellum.util.Streams;

/**
 *
 * @author evan.summers
 */
public class SaveConnectionHttpHandler implements HttpHandler {

    Logger logger = LoggerFactory.getLogger(SaveConnectionHttpHandler.class);
    SearchApp app;

    public SaveConnectionHttpHandler(SearchApp app) {
        this.app = app;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        Httpx hx = new Httpx(exchange);
        String connectionName = hx.getLastPathArg();
        logger.info("path {} {}", path, connectionName);
        try {
            ConnectionEntity connection = new ConnectionEntity(hx.getParameterMap());
            if (app.getStorage().getConnectionStorage().containsKey(connectionName)) {
                logger.info("connection {}", connection);
                app.getStorage().getConnectionStorage().insert(connection);
            } else {
                app.getStorage().getConnectionStorage().update(connection);
            }
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
        }
        exchange.close();
    }

    
}
