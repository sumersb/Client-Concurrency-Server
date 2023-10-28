public class Main {
    private static final String GO_IP_ADDR = "http://34.208.201.244:8080/albums";

    private static final String JAVA_IP_ADDR = "http://34.208.201.244:8080/AlbumStore_war/albums";

    private static final String LOCAL_GO_IP_ADDR = "http://localhost:8080/albums";

    private static final String LOCAL_JAVA_IP_ADDR = "http://localhost:8080/AlbumStore_war_exploded/albums";

    public static void main(String[] args) throws InterruptedException {
        ClientThreadManager threadManager = new ClientThreadManager(10,20,2,GO_IP_ADDR);
        threadManager.callThreads();
    }
}
