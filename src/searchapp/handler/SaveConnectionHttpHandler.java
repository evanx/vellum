/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package searchapp.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import searchapp.app.SearchApp;
import searchapp.entity.ConnectionEntity;
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
        String connectionName = getArg(exchange);
        logger.info("path {} {}", path, connectionName);
        try {
            ConnectionEntity connection = app.getStorage().getConnectionStorage().
                    select(connectionName);
            if (connection == null) {
                connection = new Gson().fromJson(Streams.readString(exchange.getRequestBody()), 
                        ConnectionEntity.class);
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

    public static String getArg(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        int index = path.lastIndexOf("/");
        if (index > 0) {
            return path.substring(index + 1);
        }
        throw new IllegalArgumentException(path);

    }
}
