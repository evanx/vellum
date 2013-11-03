package vellumdemo.cryptonomicaldemo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;

public class SymmetricCipherTest {
    SymmetricCipher cipher = new SymmetricCipher();
    
    protected void test() throws Exception {
        cipher.generateSecretKey();
        String text = "Let's test this baby...";
        byte[] bytes = text.getBytes();
        bytes = cipher.encrypt(bytes);
        bytes = cipher.decrypt(bytes);
        text = new String(bytes);
        System.out.println(text);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        OutputStream outputStream = new CipherOutputStream(byteArrayOutputStream, 
                cipher.createEncryptCipher());
        outputStream.write(bytes);
        outputStream.flush();
        InputStream inputStream = new ByteArrayInputStream(
                byteArrayOutputStream.toByteArray());
        inputStream = new CipherInputStream(inputStream, 
                cipher.createDecryptCipher());
        inputStream.read(bytes);
        text = new String(bytes);
        System.out.println(text);
    }
    
    public static void main(String[] args) {
        try {
            new SymmetricCipherTest().test();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
