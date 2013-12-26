package vellumdemo.enigmademo;

import java.io.IOException;
import java.security.cert.X509Certificate;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import vellumexp.logr.Logr;
import vellumexp.logr.LogrFactory;

public class SimpleClient extends Thread {
    static Logr logger = LogrFactory.getLogger(SimpleClient.class);
    
    SSLSocket clientSocket;
    EnigmaSocket enigmaSocket;
    
    public void connect(String host, int port) throws Exception {
        SocketFactory socketFactory = SSLSocketFactory.getDefault();
        clientSocket = (SSLSocket) socketFactory.createSocket(host, port);
        inspectCertificates();
        this.enigmaSocket = new EnigmaSocket(clientSocket);
    }
    
    protected void inspectCertificates() throws IOException {
        clientSocket.startHandshake();
        X509Certificate[] serverCertificates = (X509Certificate[])
                clientSocket.getSession().getPeerCertificates();
        for (X509Certificate certificate : serverCertificates) {
            logger.info(certificate.getIssuerDN().toString());
        }
    }
    
    public void run() {
        try {
            enigmaSocket.init();
            process();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            enigmaSocket.close();
        }
    }
    
    protected void process() throws Exception {
        String response = enigmaSocket.sendRequest("SOMEONE SET US UP THE BOMB", String.class);
        logger.info(response);
    }
}
