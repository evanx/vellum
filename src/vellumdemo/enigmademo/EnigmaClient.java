/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package vellumdemo.enigmademo;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.*;
import vellumexp.logr.Logr;
import vellumexp.logr.LogrFactory;

/**
 *
 * @author evan.summers
 */
public class EnigmaClient extends Thread {

    static EnigmaConfig config = new EnigmaConfig();
    static Logr logger = LogrFactory.getLogger(EnigmaClient.class);
    SSLSocket clientSocket;
    EnigmaSocket enigmaSocket;
    KeyStore keyStore;
    KeyManager[] keyManagers;
    TrustManager[] trustManagers;
    SecureRandom secureRandom;
    SSLContext sslContext;

    public EnigmaClient() {
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
        this.enigmaSocket = new EnigmaSocket(clientSocket);
    }

    protected void inspectCertificates() throws IOException {
        clientSocket.startHandshake();
        X509Certificate[] serverCertificates = (X509Certificate[]) clientSocket.getSession().getPeerCertificates();
        for (X509Certificate certificate : serverCertificates) {
            logger.info(certificate.getIssuerDN().toString());
        }
    }

    protected void initKeyManagers() throws Exception {
        this.keyStore = KeyStore.getInstance("JKS");
        InputStream inputStream = getClass().getResourceAsStream(
                config.serverPublicKeyStoreResource);
        keyStore.load(inputStream,
                config.serverPublicKeyStorePassword.toCharArray());
        KeyManagerFactory keyManagerFactory =
                KeyManagerFactory.getInstance("SunX509");
        keyManagerFactory.init(keyStore, null);
        this.keyManagers = keyManagerFactory.getKeyManagers();
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

    @Override
    public void run() {
        try {
            enigmaSocket.init();
            process();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        } finally {
            enigmaSocket.close();
        }
    }

    protected void process() throws Exception {
        EnigmaRequest request = new EnigmaRequest("How could they cut the power? They're animals!");
        String response = enigmaSocket.sendRequest(request, EnigmaResponse.class);
        logger.info(response);
    }
}
