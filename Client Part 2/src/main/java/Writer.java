import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.util.concurrent.LinkedBlockingQueue;

import com.tdunning.math.stats.MergingDigest;




public class Writer extends Thread {



    private LinkedBlockingQueue<CallInfo> callInfoQueue;

    private MergingDigest digest;

    private long latencySum = 0;

    private long maxLatency = 0;

    private long minLatency = Long.MAX_VALUE;

    private final int MILLISECONDS_PER_SECOND = 1000;


    public Writer(LinkedBlockingQueue<CallInfo> callInfoQueue, MergingDigest digest) {
        this.callInfoQueue = callInfoQueue;
        this.digest = digest;
    }

    public void run() {
        //Creates variables to track throughput every second
        long throughputEndTime = 0;
        long callEndTime;
        int callCount = 0;
        int second=0;

        CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader("Start Time","Request Type","Latency","Response Code");
        try {
            FileWriter fileWriter = new FileWriter("latency.csv");
            CSVPrinter csvPrinter = new CSVPrinter(fileWriter,csvFormat);
            while (true) {
                CallInfo callInfo = callInfoQueue.take();
                long latency = callInfo.getLatency();
                if (callInfo.getResponseCode() == -1) {
                    break;
                } else {
                    csvPrinter.printRecord(
                            callInfo.getStartTime(),
                            callInfo.getRequestType(),
                            latency,
                            callInfo.getResponseCode()
                    );
                    latencySum += latency;
                    digest.add(latency);
                    maxLatency = Math.max(maxLatency,latency);
                    minLatency = Math.min(minLatency,latency);
                    callEndTime = callInfo.getStartTime() + latency;

                    //Calculates and prints throughput every second
                    if (callEndTime>=throughputEndTime) {
                        System.out.print("("+second+","+callCount+"),");
                        throughputEndTime=callEndTime+MILLISECONDS_PER_SECOND;
                        callCount=1;
                        second+=1;
                    } else {
                        callCount+=1;
                    }
                }

            }
            System.out.println("("+second+","+callCount+"),");
            csvPrinter.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

         ;
    }

    public LinkedBlockingQueue<CallInfo> getCallInfoQueue() {
        return callInfoQueue;
    }

    public void setCallInfoQueue(LinkedBlockingQueue<CallInfo> callInfoQueue) {
        this.callInfoQueue = callInfoQueue;
    }

    public MergingDigest getDigest() {
        return digest;
    }

    public void setDigest(MergingDigest digest) {
        this.digest = digest;
    }

    public long getLatencySum() {
        return latencySum;
    }

    public void setLatencySum(long latencySum) {
        this.latencySum = latencySum;
    }

    public long getMaxLatency() {
        return maxLatency;
    }

    public void setMaxLatency(long maxLatency) {
        this.maxLatency = maxLatency;
    }

    public long getMinLatency() {
        return minLatency;
    }

    public void setMinLatency(long minLatency) {
        this.minLatency = minLatency;
    }

}
