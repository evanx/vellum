/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package searchapp.handler;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import searchapp.app.SearchApp;
import searchapp.entity.ConnectionEntity;
import searchapp.entity.Match;
import searchapp.replace.ReplaceConnection;

/**
 *
 * @author evan.summers
 */
public class ReplaceHttpHandler implements HttpHandler {

    Logger logger = LoggerFactory.getLogger(ReplaceHttpHandler.class);
    SearchApp app;

    public ReplaceHttpHandler(SearchApp app) {
        this.app = app;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        logger.info("path {}", path);
        try {
            JsonObject object = new JsonParser().parse(
                    new InputStreamReader(exchange.getRequestBody())).getAsJsonObject();            
            String connectionName = object.get("connectionName").getAsString();
            String searchString = object.get("searchString").getAsString();
            String replaceString = object.get("replaceString").getAsString();
            Collection<Match> matches = Match.getCollection(object.get("matches").getAsJsonArray());
            ConnectionEntity connection = app.getStorage().getConnectionStorage().select(
                    connectionName);
            if (connection == null) {
                    logger.info("connections", app.getStorage().getConnectionStorage().
                            selectCollection(""));
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
            } else {
                exchange.getResponseHeaders().set("Content-type", "text/json");
                String json = new Gson().toJson(
                        new ReplaceConnection(connection, searchString, replaceString,
                        matches).replace());
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
