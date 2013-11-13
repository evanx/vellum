/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package searchapp.util;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.lifecycle.Shutdownable;

/**
 *
 * @author evan.summers
 */
public class ShutdownHttpHandler implements HttpHandler {
    Logger logger = LoggerFactory.getLogger(ShutdownHttpHandler.class);
    Shutdownable app;
    
    public ShutdownHttpHandler(Shutdownable app) {
        this.app = app;
    }
    
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            if (httpExchange.getRemoteAddress().getAddress().equals(
                    InetAddress.getLocalHost())) {
                if (app.shutdown()) {
                    httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                    return;
                }
            }
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
        } finally {
            httpExchange.close();
        }
    }    
}
