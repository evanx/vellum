package vellumdemo.cryptonomicaldemo;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import vellum.util.Base64;

public class AsymmetricCipher {
    static final String asymmetricAlgorithm = "RSA";
    static final String asymmetricAlgorithmModePadding = "RSA/ECB/PKCS1Padding";
    static final int keySize = 1024;
    static final int blockCapacity = 117;
    
    KeyPair keyPair;
    PublicKey publicKey;
    
    public AsymmetricCipher() {
    }
    
    public void generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(asymmetricAlgorithm);
        keyPairGenerator.initialize(keySize);
        keyPair = keyPairGenerator.generateKeyPair();
        publicKey = keyPair.getPublic();
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public String getEncodedPublicKey() {
        byte[] encodedKey = publicKey.getEncoded();
        return Base64.encode(encodedKey);
    }
    
    public void setEncodedPublicKey(String key)
    throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] encodedKey = Base64.decode(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encodedKey);
        publicKey = KeyFactory.getInstance(asymmetricAlgorithm).generatePublic(keySpec);
    }
    
    public Cipher createEncryptCipher() 
    throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException  {
        Cipher encryptCipher = Cipher.getInstance(asymmetricAlgorithmModePadding);
        encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return encryptCipher;
    }

    public Cipher createDecryptCipher() 
    throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException  {
        Cipher decryptCipher = Cipher.getInstance(asymmetricAlgorithmModePadding);
        decryptCipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
        return decryptCipher;
    }

    public byte[] encrypt(byte[] bytes) throws Exception {
        return createEncryptCipher().doFinal(bytes);
    }
    
    public byte[] decrypt(byte[] bytes) throws Exception {
        return createDecryptCipher().doFinal(bytes);
    }
}
