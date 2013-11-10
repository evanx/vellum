/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package crumapp;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import static crumapp.CrumRecord.logger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.httpserver.Httpx;
import vellum.util.Streams;

/**
 *
 * @author evan.summers
 */
public class CrumHttpHandler implements HttpHandler {

    Logger logger = LoggerFactory.getLogger(CrumHttpHandler.class);
    CrumApp app;
    
    public CrumHttpHandler(CrumApp app) {
        this.app = app;
    }
    
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        logger.info("path {}", path);
        try {
            CrumRecord record = CrumRecord.parse(Streams.readString(
                    httpExchange.getRequestBody()));
            logger.info("record {}", record);            
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
        } catch (Exception e) {
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);            
        }
        httpExchange.close();
    }
    
}
