package vellumdemo.cryptonomicaldemo;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import vellumexp.logr.Logr;
import vellumexp.logr.LogrFactory;
import vellum.util.Base64;

public class CryptonomicalSocket {
    static Logr logger = LogrFactory.getLogger(CryptonomicalSocket.class);
    Socket socket;
    InputStream inputStream;
    OutputStream outputStream;
    AsymmetricCipher asymmetricCipher;
    SymmetricCipher symmetricCipher = new SymmetricCipher();
    boolean encrypt = false;

    public CryptonomicalSocket(Socket socket) {
        this(socket, new AsymmetricCipher());
    }
    
    public CryptonomicalSocket(Socket socket, AsymmetricCipher asymmetricCipher) {
        this.socket = socket;
        this.asymmetricCipher = asymmetricCipher;
    }
        
    public void init() throws IOException {
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
    }
        
    public Object decodeXml(InputStream inputStream) {
        XMLDecoder decoder = new XMLDecoder(inputStream);
        return decoder.readObject();
    }
    
    public void encodeXml(Object object, OutputStream outputStream) {
        XMLEncoder encoder = new XMLEncoder(outputStream);
        encoder.writeObject(object);
        encoder.close();
    }
    
    public String encodeXml(Object object) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        encodeXml(object, outputStream);
        return new String(outputStream.toByteArray());
    }
    
    public void writeXml(Object object) {
        encodeXml(object, outputStream);
    }
    
    public Object readXml() {
        return decodeXml(inputStream);
    }
    
    public Object decodeXml(String string) {
        return decodeXml(new ByteArrayInputStream(string.getBytes()));
    }
    
    public void writeObject(Object object) throws Exception {
        String text = encodeXml(object);
        if (encrypt) text = Base64.encode(symmetricCipher.encrypt(text.getBytes()));
        text = text + "\n\n";
        outputStream.write(text.getBytes());
    }

    public <T> T readObject() throws Exception {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream));
        StringBuilder builder = new StringBuilder();
        while (true) {
            String string = reader.readLine();
            if (string == null) break;
            if (string.trim().length() == 0) break;
            builder.append(string);
        }
        String text = builder.toString().trim();
        if (encrypt) text = new String(symmetricCipher.decrypt(Base64.decode(text)));
        return (T) decodeXml(text);
    }

    
    public String encryptSecretKey() throws Exception {
        byte[] encodedKey = symmetricCipher.getSecretKey().getEncoded();
        encodedKey = asymmetricCipher.encrypt(encodedKey);
        String keyString = Base64.encode(encodedKey);
        logger.info(keyString);
        return keyString;
    }

    public void setEncodedSecretKey(String key) throws Exception {
        byte[] encodedKey = Base64.decode(key);
        encodedKey = asymmetricCipher.decrypt(encodedKey);
        symmetricCipher.setEncodedSecretKey(encodedKey);
    }
    
    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setEncodedPublicKey(String key) 
    throws NoSuchAlgorithmException, InvalidKeySpecException {
        asymmetricCipher.setEncodedPublicKey(key);
    }

    public void generateKeyPair() throws NoSuchAlgorithmException {
        asymmetricCipher.generateKeyPair();
    }

    public String getEncodedPublicKey() {
        return asymmetricCipher.getEncodedPublicKey();
    }

    public String getEncodedSecretKey() {
        return symmetricCipher.getEncodedSecretKey();
    }

    public void setEncrypt(boolean encrypt) {
        this.encrypt = encrypt;
        
    }

    public void generateSecretKey() throws NoSuchAlgorithmException {
        symmetricCipher.generateSecretKey();
    }
}
