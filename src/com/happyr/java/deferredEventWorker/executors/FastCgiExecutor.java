package com.happyr.java.deferredEventWorker.executors;

import com.googlecode.fcgi4j.FCGIConnection;
import com.happyr.java.deferredEventWorker.Message;
import com.happyr.java.deferredEventWorker.PathResolver;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * Execute the message payload with HTTP
 *
 * @author Tobias Nyholm
 */
public class FastCgiExecutor implements ExecutorInterface {
    /**
     * @param message
     * @return
     */
    @Override
    public String execute(Message message) {
        try {
            return doExecute(message);
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);

            return "Unknown java error: " + sw.toString();
        }
    }

    /**
     * @param message
     * @return
     * @throws Exception
     */
    private String doExecute(Message message) throws IOException {

        //create FastCGI connection
        FCGIConnection connection = FCGIConnection.open();
        connection.connect(new InetSocketAddress(message.getHeader("fastcgi_host"), Integer.parseInt(message.getHeader("fastcgi_port"))));

        connection.beginRequest(PathResolver.resolve(message.getHeader("dispatch_path")));
        connection.setRequestMethod("POST");

        byte[] postData = ("DEFERRED_DATA=" + message.getData()).getBytes();

        //set contentLength, it's important
        connection.setContentLength(postData.length);
        connection.write(ByteBuffer.wrap(postData));

        //print response headers
        /*Map<String, String> responseHeaders = connection.getResponseHeaders();
        for (String key : responseHeaders.keySet()) {
            System.out.println("HTTP HEADER: " + key + "->" + responseHeaders.get(key));
        }*/

        //read response data
        ByteBuffer buffer = ByteBuffer.allocate(10240);
        connection.read(buffer);
        buffer.flip();

        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);

        String response = new String(data);
        //close the connection
        connection.close();

        if (connection.hasOutputOnStdErr())
            return response;

        return null;
    }
}