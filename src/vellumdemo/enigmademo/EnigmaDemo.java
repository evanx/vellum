package vellumdemo.enigmademo;


public class EnigmaDemo {
    static EnigmaConfig config = new EnigmaConfig();
    
    EnigmaServer server = new EnigmaServer();
    EnigmaClient client = new EnigmaClient();
    
    protected void test() throws Exception {
        server.init();
        server.bind(config.sslPort);
        server.start();
        client.init();
        client.connect(config.host, config.sslPort);
        client.start();
    }
    
    public static void main(String[] args) throws Exception {
        try {
            new EnigmaDemo().test();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Thread.sleep(1000);
        System.exit(0);
    }
}
