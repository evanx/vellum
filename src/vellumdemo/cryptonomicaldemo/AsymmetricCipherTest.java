package vellumdemo.cryptonomicaldemo;

public class AsymmetricCipherTest {
    AsymmetricCipher cipher = new AsymmetricCipher();
    
    protected void test() throws Exception {
        cipher.generateKeyPair();
        String text = "Let's test this baby...";
        byte[] bytes = text.getBytes();
        bytes = cipher.encrypt(bytes);
        bytes = cipher.decrypt(bytes);
        text = new String(bytes);
        System.out.println(text);
    }
    
    public static void main(String[] args) {
        try {
            new AsymmetricCipherTest().test();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
