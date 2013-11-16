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
import searchapp.util.storage.StorageException;
import vellum.util.Streams;

/**
 *
 * @author evan.summers
 */
public class UpdateConnectionHttpHandler implements HttpHandler {

    Logger logger = LoggerFactory.getLogger(UpdateConnectionHttpHandler.class);
    SearchApp app;

    public UpdateConnectionHttpHandler(SearchApp app) {
        this.app = app;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        HttpExchangeInfo exchangeInfo = new HttpExchangeInfo(exchange);
        String connectionName = exchangeInfo.getLastPathArg();
        String data = Streams.readString(exchange.getRequestBody());
        logger.info("path {} {}", path, data);
        int responseCode;
        try {
            ConnectionEntity connection = new ConnectionEntity(
                    new EncodedMap().parse(data));
            logger.info("connection {}", connection);
            if (!connection.isValid()) {
                responseCode = HttpURLConnection.HTTP_NOT_ACCEPTABLE;
            } else if (!connection.getConnectionName().equals(connectionName)) {
                responseCode = HttpURLConnection.HTTP_NOT_ACCEPTABLE;
            } else if (!app.getStorage().getConnectionStorage().containsKey(connectionName)) {
                responseCode = HttpURLConnection.HTTP_NOT_FOUND;
            } else {
                app.getStorage().getConnectionStorage().update(connection);
                responseCode = HttpURLConnection.HTTP_OK;
            }
        } catch (IOException | StorageException e) {
            logger.warn(e.getMessage(), e);
            responseCode = HttpURLConnection.HTTP_INTERNAL_ERROR;
        }
        exchange.sendResponseHeaders(responseCode, 0);
        exchange.close();
    }
}
