/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package vellumdemo.enigmademo;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import vellumexp.logr.Logr;
import vellumexp.logr.LogrFactory;

/**
 *
 * @author evan.summers
 */
public class EnigmaSocket {
    static final String CHARSET = "UTF8";
    static Logr logger = LogrFactory.getLogger(EnigmaSocket.class);
    
    Socket socket;

    public EnigmaSocket(Socket socket) {
        this.socket = socket;
    }
            
    public void init() throws IOException {
    }
    
    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public <T> T sendRequest(Object request, Class responseClass) throws Exception {
        socket.getOutputStream().write(new Gson().toJson(request).getBytes(CHARSET));
        InputStreamReader reader = new InputStreamReader(socket.getInputStream());
        T response = (T) new Gson().fromJson(reader, responseClass);
        return response;
    }

    public void writeObject(Object object) throws Exception {
        socket.getOutputStream().write(new Gson().toJson(object).getBytes(CHARSET));
    }

    public <T> T readObject(Class responseClass) throws Exception {    
        InputStreamReader reader = new InputStreamReader(socket.getInputStream());
        T response = (T) new Gson().fromJson(reader, responseClass);
        return response;
    }
        
}
