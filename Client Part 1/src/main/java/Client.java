import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.methods.multipart.Part;


import java.io.*;

public class Client extends Thread {

    private int iterations;
    private HttpClient client;
    private PostMethod postMethod;
    private GetMethod getMethod;


        public Client(String url, int iterations) {
            this.iterations = iterations;
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

            // Create a get method instance
            getMethod = new GetMethod(url+"/1");


        }

    public void run() {
            int statusCode;


    for (int i = 0; i<iterations ; i++) {
        try {
            // Execute the post method.
            statusCode = client.executeMethod(postMethod);

            if (statusCode != HttpStatus.SC_CREATED) {
                System.err.println("Method failed: " + postMethod.getStatusLine());
            }


            // Execute the get method.
            statusCode = client.executeMethod(getMethod);
            if (statusCode != HttpStatus.SC_OK) {
                System.err.println("Method failed: " + getMethod.getStatusLine());
            }
;

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