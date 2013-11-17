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
import searchapp.search.SearchConnection;
import searchapp.util.http.EncodedMap;
import vellum.parameter.StringMap;
import vellum.util.Streams;

/**
 *
 * @author evan.summers
 */
public class SearchHttpHandler implements HttpHandler {

    Logger logger = LoggerFactory.getLogger(SearchHttpHandler.class);
    SearchApp app;

    public SearchHttpHandler(SearchApp app) {
        this.app = app;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String data = Streams.readString(exchange.getRequestBody());
        logger.info("path {} {}", path, data);
        try {
            StringMap map = new EncodedMap().parse(data);
            String connectionName = map.get("connection");
            String searchString = map.get("search");
            ConnectionEntity connection = app.getStorage().getConnectionStorage().select(
                    connectionName);
            if (connection == null) {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
            } else {
                exchange.getResponseHeaders().set("Content-type", "text/json");
                String json = new Gson().toJson(
                        new SearchConnection(connection, searchString).search());
                logger.info(json);
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                exchange.getResponseBody().write(json.getBytes());
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
        }
        exchange.close();
    }
}
