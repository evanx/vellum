package vellumdemo.cryptonomicaldemo;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import vellumexp.logr.Logr;
import vellumexp.logr.LogrFactory;

public class CryptonomicalClient extends Thread {
    CryptonomicalSocket cryptoSocket;
    Logr logger = LogrFactory.getLogger(CryptonomicalDemo.class);
    
    public CryptonomicalClient() {
    }
    
    public void connect(String host, int port) throws UnknownHostException,
            IOException, NoSuchAlgorithmException {
        Socket clientSocket = new Socket(host, port);
        cryptoSocket = new CryptonomicalSocket(clientSocket);
        cryptoSocket.init();
    }
    
    public void run() {
        try {
            String publicKey = sendRequest(CryptonomicalCommon.secureRequest);
            cryptoSocket.setEncodedPublicKey(publicKey);
            cryptoSocket.generateSecretKey();
            String secretKey = cryptoSocket.encryptSecretKey();
            String response = sendRequest(secretKey);
            if (!response.equals(CryptonomicalCommon.secureAcknowledge)) throw new RuntimeException();
            cryptoSocket.setEncrypt(true);
            process();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cryptoSocket.close();
        }
    }
    
    protected void process() throws Exception {
        String response = sendRequest("ALL YOUR BASE ARE BELONG TO US.");
        logger.info(response);
    }
    
    public <T> T sendRequest(Object request) throws Exception {
        logger.info("Write: " + request);
        cryptoSocket.writeObject(request);
        T response = (T) cryptoSocket.readObject();
        logger.info("Read: " + response);
        return response;
    }
    
}
