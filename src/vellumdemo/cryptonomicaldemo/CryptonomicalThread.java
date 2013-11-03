package vellumdemo.cryptonomicaldemo;

import java.net.Socket;

public class CryptonomicalThread extends Thread {
    CryptonomicalSocket cryptoSocket;
    
    public CryptonomicalThread(Socket clientSocket, 
            AsymmetricCipher asymmetricCipher) {
        this.cryptoSocket = new CryptonomicalSocket(clientSocket, asymmetricCipher);
    }
    
    public void run() {
        try {
            cryptoSocket.init();
            String string = cryptoSocket.readObject();
            if (!string.equals(CryptonomicalCommon.secureRequest)) throw new RuntimeException(string);
            String publicKey = cryptoSocket.getEncodedPublicKey();
            cryptoSocket.writeObject(publicKey);
            String secretKey = cryptoSocket.readObject();
            cryptoSocket.setEncodedSecretKey(secretKey);
            cryptoSocket.writeObject(CryptonomicalCommon.secureAcknowledge);
            cryptoSocket.setEncrypt(true);
            process();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cryptoSocket.close();
        }
    }
    
    protected void process() throws Exception {
        Object request = cryptoSocket.readObject();
        cryptoSocket.writeObject("WHAT YOU SAY !! " + request);
    }
}
