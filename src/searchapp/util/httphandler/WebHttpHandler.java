/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package searchapp.util.httphandler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import vellum.util.Streams;

/**
 *
 * @author evan.summers
 */
public class WebHttpHandler implements HttpHandler {

    Logr logger = LogrFactory.getLogger(WebHttpHandler.class);
    Map<String, byte[]> cache = new HashMap();
    String webPath;
    
    public WebHttpHandler(String webPath) {
        this.webPath = webPath;
    }
    
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        if (httpExchange.getRequestURI().getPath().endsWith(".png")) {
            httpExchange.getResponseHeaders().set("Content-type", "image/png");
        } else if (httpExchange.getRequestURI().getPath().endsWith(".html")) {
            httpExchange.getResponseHeaders().set("Content-type", "text/html");
        } else if (httpExchange.getRequestURI().getPath().endsWith(".css")) {
            httpExchange.getResponseHeaders().set("Content-type", "text/css");
        } else if (httpExchange.getRequestURI().getPath().endsWith(".js")) {
            httpExchange.getResponseHeaders().set("Content-type", "text/javascript");
        } else if (httpExchange.getRequestURI().getPath().endsWith(".txt")) {
            httpExchange.getResponseHeaders().set("Content-type", "text/plain");
        } else if (httpExchange.getRequestURI().getPath().endsWith(".html")) {
            httpExchange.getResponseHeaders().set("Content-type", "text/html");
        } else {
            httpExchange.getResponseHeaders().set("Content-type", "text/html");
            path = "index.html";
        }
        try {
            byte[] bytes = cache.get(path);
            if (bytes == null) {
                logger.info("get {}", webPath + path);
                InputStream resourceStream = getClass().getResourceAsStream(
                        webPath + path);
                bytes = Streams.readBytes(resourceStream);
                cache.put(path, bytes);
            }
            logger.info("path", path, bytes.length);
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
            httpExchange.getResponseBody().write(bytes);
        } catch (Exception e) {
            logger.warn(e);
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
        }
        httpExchange.close();
    }
}
