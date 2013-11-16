package vellumdemo.cryptonomicaldemo;

import java.io.IOException;
import java.net.ServerSocket;
import java.security.NoSuchAlgorithmException;

public class CryptonomicalServer extends Thread {
    ServerSocket serverSocket;
    boolean isRunning = true; 
    AsymmetricCipher asymmetricCipher = new AsymmetricCipher();
    
    public void bind(int port) throws IOException, NoSuchAlgorithmException {
        serverSocket = new ServerSocket(port);
        asymmetricCipher.generateKeyPair();
    }
    
    public void run() {
        while (isRunning) {
            try {
                new CryptonomicalThread(serverSocket.accept(), asymmetricCipher).start();
            } catch (Exception e) {
                e.printStackTrace();
            }  
            break;
        }
    }  
}
