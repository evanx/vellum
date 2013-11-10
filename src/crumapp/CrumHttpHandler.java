/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package crumapp;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
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
        String text = Streams.readString(httpExchange.getRequestBody());
        String[] lines = text.trim().split("\n");
        CrumRecord record = new CrumRecord();
        boolean inHeader = true;
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            System.out.println(line);
            if (line.length() == 0) {
                inHeader = false;
                System.out.println("--");
            } else if (inHeader) {
            } else {
            }
            if (line.startsWith("From: ")) {
                record.setFromLine(line);
            } else if (line.startsWith("Subject: ")) {
                record.setSubjectLine(line);
            } else if (line.startsWith("Content-Type: ")) {
                record.setContentTypeLine(line);
            } else {                
            }
        }
        logger.info("record {}", record);
        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
        httpExchange.close();
    }
}
