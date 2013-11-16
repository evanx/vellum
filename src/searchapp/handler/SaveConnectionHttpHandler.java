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
import searchapp.util.http.HttpExchangeInfo;

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
        HttpExchangeInfo exchangeInfo = new HttpExchangeInfo(exchange);
        String connectionName = exchangeInfo.getLastPathArg();
        logger.info("path {} {}", path, connectionName);
        try {
            ConnectionEntity connection = new ConnectionEntity(exchangeInfo.getPostMap());
            logger.info("connection {}", connection);
            if (app.getStorage().getConnectionStorage().containsKey(connectionName)) {
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
