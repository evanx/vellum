/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package crumapp;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.httpserver.Httpx;

/**
 *
 * @author evan.summers
 */
public class CrumHttpHandler implements HttpHandler {

    Logger logger = LoggerFactory.getLogger(CrumHttpHandler.class);
    CrumApp app;
    CrumStorage storage;
    
    public CrumHttpHandler(CrumApp app) {
        this.app = app;
        storage = app.getStorage();
    }
    
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        logger.info("path {}", path);
        HttpHandler handler = getHandler(path);
        if (handler == null) {
            app.handle(httpExchange);
        } else {
            handler.handle(httpExchange);
        }
    }

    public HttpHandler getHandler(String path) throws IOException {
        return null;
    }
}
