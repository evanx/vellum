package vellumdemo.cryptonomicaldemo;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import vellum.util.Base64;

public class SymmetricCipher {
    static final String symmetricAlgorithm = "AES";
    static final int symmetricKeySize = 128;
    static final String defaultSecretKey = "public";
            
    SecretKey secretKey = 
            new SecretKeySpec(defaultSecretKey.getBytes(), symmetricAlgorithm); 
            
    public SymmetricCipher() {
    }

    
    public void generateSecretKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(symmetricAlgorithm);
        keyGenerator.init(symmetricKeySize);
        secretKey = keyGenerator.generateKey();
    }
        
    public String getEncodedSecretKey() {
        byte[] encodedKey = secretKey.getEncoded();
        String keyString = Base64.encode(encodedKey);
        return keyString;
    }

    public SecretKey getSecretKey() throws NoSuchAlgorithmException {
        if (secretKey == null) generateSecretKey();
        return secretKey;
    }
    
    public void setEncodedSecretKey(byte[] encodedKey) 
    throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(encodedKey, symmetricAlgorithm);
        secretKey = secretKeySpec;
    }
    
    public Cipher createEncryptCipher() 
    throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException  {
        Cipher encryptCipher = Cipher.getInstance(symmetricAlgorithm);
        encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return encryptCipher;
    }

    public Cipher createDecryptCipher() 
    throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException  {
        Cipher decryptCipher = Cipher.getInstance(symmetricAlgorithm);
        decryptCipher.init(Cipher.DECRYPT_MODE, secretKey);
        return decryptCipher;
    }

    public byte[] encrypt(byte[] bytes) throws Exception {
        return createEncryptCipher().doFinal(bytes);
    }
    
    public byte[] decrypt(byte[] bytes) throws Exception {
        return createDecryptCipher().doFinal(bytes);
    }  
}
