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
import vellum.util.Streams;

/**
 *
 * @author evan.summers
 */
public class DeleteConnectionHttpHandler implements HttpHandler {

   Logger logger = LoggerFactory.getLogger(DeleteConnectionHttpHandler.class);
   SearchApp app;

   public DeleteConnectionHttpHandler(SearchApp app) {
      this.app = app;
   }

   @Override
   public void handle(HttpExchange exchange) throws IOException {
      String path = exchange.getRequestURI().getPath();
      logger.info("path {}", path);
      try {
         String connectionName = Streams.readString(exchange.getRequestBody());
         logger.info("connectionName {}", connectionName);
         app.getStorage().getConnectionStorage().delete(connectionName);
         exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
      } catch (Exception e) {
         exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
      }
      exchange.close();
   }
}
