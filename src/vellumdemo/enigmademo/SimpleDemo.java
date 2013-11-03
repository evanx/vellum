package vellumdemo.enigmademo;


public class SimpleDemo {
    
    SimpleServer server = new SimpleServer();
    SimpleClient client = new SimpleClient();
    
    protected void test() throws Exception {
        server.bind(443);
        server.start();
        client.connect("localhost", 443);
        client.start();
    }
    
    public static void main(String[] args) throws Exception {
        try {
            new SimpleDemo().test();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Thread.sleep(1000);
        System.exit(0);
    }
}
