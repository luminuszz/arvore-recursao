import adapter.HttpAdapter;

public class Main {
    public static void main(String[] args) {
        try {
            int port = 8081;
            HttpAdapter.startServer(port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
