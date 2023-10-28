import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.methods.multipart.Part;


import java.io.*;
import java.util.concurrent.LinkedBlockingQueue;

public class Client extends Thread {

    private String url;
    private int iterations;
    private HttpClient client;
    private PostMethod postMethod;
    private GetMethod getMethod;
    private final boolean isRecordingThread;
    private final LinkedBlockingQueue<CallInfo> latencyQueue;


    public Client(String url, int iterations, LinkedBlockingQueue<CallInfo> latencyQueue, boolean isRecordingThread) {
        this.url = url;
        this.iterations = iterations;
        this.latencyQueue = latencyQueue;
        this.isRecordingThread = isRecordingThread;

        client = new HttpClient();

        // Create a post method instance.
        postMethod = new PostMethod(url);
        Part[] parts = {
                new StringPart("profile", "{\"artist\": \"Shakira\", \"title\": \"waka waka\", \"year\": \"2012\"}", "UTF-8"),
                new StringPart("image","")
        };
        MultipartRequestEntity requestEntity = new MultipartRequestEntity(parts, postMethod.getParams());
        postMethod.setRequestEntity(requestEntity);
        postMethod.setRequestHeader("accept", "application/json");
        postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                new DefaultHttpMethodRetryHandler(5, false));

        getMethod = new GetMethod(url+"/1");

    }

    public void run() {
        if (isRecordingThread) {recordingThread();}
        else {startupThread();}
    }

    public void startupThread() {
        int statusCode;


        for (int i = 0; i<iterations ; i++) {
            try {
                // Execute the method.

                statusCode = client.executeMethod(postMethod);
                if (statusCode != HttpStatus.SC_CREATED) {
                    System.err.println("Method failed: " + postMethod.getStatusLine());
                }

                statusCode = client.executeMethod(getMethod);
                if (statusCode != HttpStatus.SC_OK) {
                    System.err.println("Method failed: " + getMethod.getStatusLine());
                }

            } catch (HttpException e) {
                System.err.println("Fatal protocol violation: " + e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                System.err.println("Fatal transport error: " + e.getMessage());
                e.printStackTrace();
            } finally {
                // Release the connection.
                postMethod.releaseConnection();
                getMethod.releaseConnection();
            }
        }
    }

    public void recordingThread() {
        int statusCode;
        long startTime;
        long endTime;
        long latency;


        for (int i = 0; i<iterations ; i++) {
            try {
                // Execute the method.


                startTime = System.currentTimeMillis();
                statusCode = client.executeMethod(postMethod);
                if (statusCode != HttpStatus.SC_CREATED) {
                    System.err.println("Method failed: " + postMethod.getStatusLine());
                }
                endTime = System.currentTimeMillis();
                latency = endTime - startTime;
                latencyQueue.add(new CallInfo(startTime,"POST",latency,statusCode));


                startTime = System.currentTimeMillis();
                statusCode = client.executeMethod(getMethod);
                if (statusCode != HttpStatus.SC_OK) {
                    System.err.println("Method failed: " + getMethod.getStatusLine());
                }
                endTime = System.currentTimeMillis();
                latency = endTime - startTime;
                latencyQueue.add(new CallInfo(startTime,"GET",latency,statusCode));



            } catch (HttpException e) {
                System.err.println("Fatal protocol violation: " + e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                System.err.println("Fatal transport error: " + e.getMessage());
                e.printStackTrace();
            } finally {
                // Release the connection.
                postMethod.releaseConnection();
                getMethod.releaseConnection();
            }
        }
    }
}