/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package searchapp.app;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public void handle(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        logger.info("path {}", path);
        try {
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
        } catch (Exception e) {
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);      
        }
        httpExchange.close();
    }    
}
