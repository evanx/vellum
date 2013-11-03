package vellumdemo.cryptonomicaldemo;

public class CryptonomicalDemo {
    public static CryptonomicalConfig config = new CryptonomicalConfig();
    
    CryptonomicalServer server = new CryptonomicalServer();
    CryptonomicalClient client = new CryptonomicalClient();
    
    protected void test() throws Exception {
        server.bind(config.serverPort);
        server.start();
        client.connect("localhost", config.serverPort);
        client.start();
    }
    
    public static void main(String[] args) throws Exception {
        try {
            new CryptonomicalDemo().test();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
