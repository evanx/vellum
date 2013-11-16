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
import searchapp.util.http.EncodedMap;
import searchapp.util.http.HttpExchangeInfo;
import vellum.util.Streams;

/**
 *
 * @author evan.summers
 */
public class InsertConnectionHttpHandler implements HttpHandler {

    Logger logger = LoggerFactory.getLogger(InsertConnectionHttpHandler.class);
    SearchApp app;

    public InsertConnectionHttpHandler(SearchApp app) {
        this.app = app;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        HttpExchangeInfo exchangeInfo = new HttpExchangeInfo(exchange);
        String data = Streams.readString(exchange.getRequestBody());
        logger.info("path {} {}", path, data);
        try {
            ConnectionEntity connection = new ConnectionEntity(
                    new EncodedMap().parse(data));
            logger.info("connection {}", connection);
            if (!connection.isValid()) {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_ACCEPTABLE, 0);
            } else if (app.getStorage().getConnectionStorage().containsKey(
                    connection.getConnectionName())) {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_CONFLICT, 0);
            } else {
                app.getStorage().getConnectionStorage().insert(connection);
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
        }
        exchange.close();
    }
}
