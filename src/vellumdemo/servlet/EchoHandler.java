/*
 * Source https://github.com/evanx by @evanxsummers
 * Apache Software License 2.0
 * Supported by iPay (Pty) Ltd, BizSwitch.net
 */
package vellumdemo.servlet;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author evan.summers
 */
public class EchoHandler implements HttpHandler {

    public EchoHandler() {
    }

    
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        StringBuilder response = new StringBuilder();
        Headers headers = httpExchange.getRequestHeaders();
        Set<Map.Entry<String, List<String>>> entries = headers.entrySet();
        for (Map.Entry<String, List<String>> entry : entries) {
            response.append(entry.toString());
            response.append("\n");
        }
        response.append("hello, ");
        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.toString().getBytes());
        os.close();
    }
}