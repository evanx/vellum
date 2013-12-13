/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package vellumdemo.enigmademo;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import javax.net.ssl.*;

/**
 *
 * @author evan.summers
 */
public class EnigmaServer extends Thread {
    EnigmaServerConfig config = new EnigmaServerConfig();
    
    KeyStore keyStore;
    SSLContext sslContext;
    KeyManager[] keyManagers;
    TrustManager[] trustManagers;
    SecureRandom secureRandom;
    SSLServerSocket serverSocket;
    boolean isRunning = true;
    
    public void init() throws Exception {
        initKeyManagers();
        initTrustManagers();
        initSSLContext();
    }
    
    protected void initKeyManagers() throws Exception {
        keyStore = KeyStore.getInstance("JKS");
        InputStream inputStream = new FileInputStream(
                config.serverKeyStoreFileName);
        keyStore.load(inputStream,
                config.serverKeyStorePassword.toCharArray());
        KeyManagerFactory keyManagerFactory =
                KeyManagerFactory.getInstance("SunX509");
        keyManagerFactory.init(keyStore,
                config.serverKeyPassword.toCharArray());
        keyManagers = keyManagerFactory.getKeyManagers();
    }

    protected void initTrustManagers() throws Exception {
        TrustManagerFactory trustManagerFactory = 
                TrustManagerFactory.getInstance("SunX509");
        trustManagerFactory.init(keyStore);
        this.trustManagers = trustManagerFactory.getTrustManagers();
    }
    
    protected void initSSLContext() throws Exception {
        sslContext = SSLContext.getInstance("SSL");
        sslContext.init(keyManagers, trustManagers, secureRandom);
    }
    
    public void bind(int port) throws Exception {
        SSLServerSocketFactory sslServerSocketFactory =
                sslContext.getServerSocketFactory();
        this.serverSocket = (SSLServerSocket) sslServerSocketFactory.
                createServerSocket(port);
    }
    
    public void run() {
        while (isRunning) {
            try {
                new EnigmaThread(serverSocket.accept()).start();
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
            break;
        }
    }
}
