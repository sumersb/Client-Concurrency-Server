public class ClientThreadManager {

    private static final float MILLISECOND_TO_SECOND= (float) 1 /1000;

    private static final long SECOND_TO_MILLISECOND = 1000;

    private static final int STARTUP_THREAD_COUNT = 10;

    private static  final int ITERATIONS_PER_STARTUP_THREAD = 100;

    private static final int ITERATIONS_PER_THREAD = 1000;

    private int threadGroupSize;

    private int numThreadGroups;

    private int delay;

    private String IPAddr;

    public ClientThreadManager(int threadGroupSize, int numThreadGroups, int delay, String IPAddr) {
        this.threadGroupSize = threadGroupSize;
        this.numThreadGroups = numThreadGroups;
        this.delay = delay;
        this.IPAddr = IPAddr;
    }

    public void callThreads() throws InterruptedException {

        // Initialize jagged threadArray with Startup Threads in First Index
        Thread[][] threadArray = new Thread[numThreadGroups + 1][];
        threadArray[0] = new Thread[STARTUP_THREAD_COUNT];
        for (int i = 1; i <= numThreadGroups;i++) {
            threadArray[i] = new Thread[threadGroupSize];
        }
        // Startup threads initiated
        for (int i = 0; i < threadArray[0].length ; i++) {
            threadArray[0][i] = new Client(IPAddr,ITERATIONS_PER_STARTUP_THREAD);
            threadArray[0][i].start();
        }
        //Wait for completion of startup threads
        for (int i = 0; i < threadArray[0].length ; i++) {
            threadArray[0][i].join();
        }

        long startTime = System.currentTimeMillis();

        //Iterate through threadGroups and start threads with delay in between
        for (int i = 1; i < threadArray.length ; i++) {
            for (int j = 0; j < threadArray[i].length; j++) {
                threadArray[i][j] = new Client(IPAddr, ITERATIONS_PER_THREAD);
                threadArray[i][j].start();
            }
            Thread.sleep( delay * SECOND_TO_MILLISECOND);
        }

        //Wait for completion of all threads
        for (int i = 1; i < threadArray.length ; i++) {
            for (int j = 0; j < threadArray[i].length; j++) {
                threadArray[i][j].join();
            }
        }
        long endTime = System.currentTimeMillis();

        float wallTime = (endTime - startTime) * MILLISECOND_TO_SECOND;

        //ITERATIONS_PER_THREAD multiplied BY 2 to account for get and post request
        float throughput = (float) numThreadGroups * threadGroupSize*2*ITERATIONS_PER_THREAD / wallTime;

        System.out.println("Number Thread Groups: " + numThreadGroups);

        System.out.println("Thread Group Size: " + threadGroupSize);

        System.out.println("Server Calls: " + numThreadGroups * threadGroupSize * 2 * ITERATIONS_PER_THREAD);

        System.out.println("Wall Time: " + wallTime + " seconds");

        System.out.println("Throughput: " + throughput + " calls per second");

    }


    public int getThreadGroupSize() {
        return threadGroupSize;
    }

    public void setThreadGroupSize(int threadGroupSize) {
        this.threadGroupSize = threadGroupSize;
    }

    public int getNumThreadGroups() {
        return numThreadGroups;
    }

    public void setNumThreadGroups(int numThreadGroups) {
        this.numThreadGroups = numThreadGroups;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public String getIPAddr() {
        return IPAddr;
    }

    public void setIPAddr(String IPAddr) {
        this.IPAddr = IPAddr;
    }


}
