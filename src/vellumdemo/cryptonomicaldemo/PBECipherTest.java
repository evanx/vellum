package vellumdemo.cryptonomicaldemo;

public class PBECipherTest {
    PBECipher cipher = new PBECipher();
        
    protected void test(String text) throws Exception {
        text = cipher.encrypt(text);
        System.out.println(text);
        text = cipher.decrypt(text);
        System.out.println(text);
    }
    
    protected void test() throws Exception {
        test("Let's get us some PBE with DES");
        test("Let's get us some more PBE with DES");
    }
    
    public static void main(String[] args) {
        try {
            new PBECipherTest().test();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
