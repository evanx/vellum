package vellumdemo.cryptonomicaldemo;

import java.security.spec.KeySpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import vellum.util.Base64;

public class PBECipher {
    private static final String ALGORITHM = "PBEWithMD5AndDES"; // PBEWithSHAAnd3KeyTripleDES
    private static final String DEFAULT_PASSWORD = "Ssh ssh!";
    private static byte[] SALT = {
        (byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03,
        (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32
    };
    private static final int ITERATION_COUNT = 5;
    
    SecretKey secretKey;
    PBEParameterSpec parameterSpec;
    Cipher encryptCipher;
    Cipher decryptCipher;

    public PBECipher() {
        this(DEFAULT_PASSWORD);
    }
    
    public PBECipher(String password) {
        try {
            parameterSpec = new PBEParameterSpec(SALT, ITERATION_COUNT);
            secretKey = createSecretKey(password);
            encryptCipher = createCipher(Cipher.ENCRYPT_MODE);
            decryptCipher = createCipher(Cipher.DECRYPT_MODE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private SecretKey createSecretKey(String secretKey) throws Exception {
        KeySpec keySpec = new PBEKeySpec(secretKey.toCharArray());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        return keyFactory.generateSecret(keySpec);
    }
    
    private Cipher createCipher(int mode) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(mode, secretKey, parameterSpec);
        return cipher;
    }
    
    public String encrypt(String string) {
        try {
            return Base64.encode(encryptCipher.doFinal(string.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public String decrypt(String string) {
        try {
            return new String(decryptCipher.doFinal(Base64.decode(string)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
