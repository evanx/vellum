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

/**
 *
 * @author evan.summers
 */
public class ListConnectionsHttpHandler implements HttpHandler {

    Logger logger = LoggerFactory.getLogger(ListConnectionsHttpHandler.class);
    SearchApp app;

    public ListConnectionsHttpHandler(SearchApp app) {
        this.app = app;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        logger.info("path {}", path);
        try {
            exchange.getResponseHeaders().set("Content-type", "text/json");
            String json = new Gson().toJson(app.getStorage().getConnectionStorage().
                    selectCollection("select * from connections"));
            logger.info(json);
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
            exchange.getResponseBody().write(json.getBytes());
        } catch (Exception e) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
        }
        exchange.close();
    }
}
