/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package vellumdemo.enigmademo;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.*;
import vellumexp.logr.Logr;
import vellumexp.logr.LogrFactory;
import vellum.util.Lists;

/**
 *
 * @author evan.summers
 */
public class SimpleSSLClientDemo {

    static Logr logger = LogrFactory.getLogger(SimpleSSLClientDemo.class);
    static final String server = System.getProperty("server");
    static final int port = Integer.getInteger("port");
    static final String keyStoreLocation = System.getProperty("javax.net.ssl.keyStore");
    static final char[] keyStorePassword = System.getProperty("javax.net.ssl.keyStorePassword").toCharArray();
    static final char[] keyPassword = System.getProperty("javax.net.ssl.keyPassword").toCharArray();
    static final String trustStoreLocation = System.getProperty("javax.net.ssl.trustStore");
    static final char[] trustStorePassword = System.getProperty("javax.net.ssl.trustStorePassword").toCharArray();
    
    KeyManager[] keyManagers;
    TrustManager[] trustManagers;
    SecureRandom secureRandom;
    KeyStore keyStore;
    SSLContext sslContext;
    SSLSocket clientSocket;

    public SimpleSSLClientDemo() {
    }

    public void init() throws Exception {
        initKeyManagers();
        initTrustManagers();
        initSSLContext();
    }

    public void connect(String host, int port) throws Exception {
        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        clientSocket = (SSLSocket) sslSocketFactory.createSocket(host, port);
        inspectCertificates();
    }

    protected void inspectCertificates() throws IOException {
        clientSocket.startHandshake();
        X509Certificate[] serverCertificates = (X509Certificate[]) 
                clientSocket.getSession().getPeerCertificates();
        for (X509Certificate certificate : serverCertificates) {
            System.out.println("cert: " + certificate.getIssuerDN().toString());
        }
    }

    protected void initKeyManagers() throws Exception {
        this.keyStore = KeyStore.getInstance("JKS");
        InputStream inputStream = new FileInputStream(keyStoreLocation);
        keyStore.load(inputStream, keyStorePassword);
        KeyManagerFactory keyManagerFactory = 
                KeyManagerFactory.getInstance("SunX509");
        keyManagerFactory.init(keyStore, keyPassword);
        this.keyManagers = keyManagerFactory.getKeyManagers();
        for (String alias : Lists.list(keyStore.aliases())) {
            System.out.println("alias " + alias);
        }
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

    //@Test
    public void test() throws Exception {
        init();
        connect(server, port);
    }
    
    public static void main(String[] args) {
        try {
            new SimpleSSLClientDemo().test();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
    
}
