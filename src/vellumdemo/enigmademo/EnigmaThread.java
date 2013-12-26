/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package vellumdemo.enigmademo;

import java.net.Socket;
import vellumexp.logr.Logr;
import vellumexp.logr.LogrFactory;

/**
 *
 * @author evan.summers
 */
public class EnigmaThread extends Thread {
    static Logr logger = LogrFactory.getLogger(EnigmaThread.class);
    EnigmaSocket enigmaSocket;
    
    public EnigmaThread(Socket clientSocket) {
        this.enigmaSocket = new EnigmaSocket(clientSocket);
    }
    
    public void run() {
        try {
            enigmaSocket.init();
            process();
        } catch (Exception e) {
            logger.warn(e, null);
        } finally {
            enigmaSocket.close();
        }
    }
    
    protected void process() throws Exception {
        String request = enigmaSocket.readObject(String.class);
        logger.info(request);
        enigmaSocket.writeObject("That's it man, game over man, game over!");
    }
}
