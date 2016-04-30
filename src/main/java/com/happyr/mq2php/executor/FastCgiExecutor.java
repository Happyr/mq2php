package com.happyr.mq2php.executor;

import com.googlecode.fcgi4j.FCGIConnection;
import com.happyr.mq2php.message.Message;
import com.happyr.mq2php.util.Marshaller;
import com.happyr.mq2php.util.PathResolver;
import com.happyr.mq2php.util.Serializer;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * Execute the message payload with HTTP
 *
 * @author Tobias Nyholm
 */
public class FastCgiExecutor implements ExecutorInterface {

    public static final int MAX_PAYLOAD = 65535;

    /**
     * @param message
     * @return
     */
    public String execute(Message message) {
        try {
            return doExecute(message);
        } catch (ConnectException e) {
            return "Could not connect to to fastcgi server: " + e.getMessage();
        } catch (IOException e) {
            return "IO exception: " + e.getMessage();
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
        connection.connect(new InetSocketAddress(message.getHeaderValueByName("fastcgi_host"), Integer.parseInt(message.getHeaderValueByName("fastcgi_port"))));

        connection.beginRequest(PathResolver.resolve(message.getHeaderValueByName("dispatch_path")));
        connection.setRequestMethod("POST");

        byte[] postData = ("DEFERRED_DATA=" + Serializer.serialize(Marshaller.toBytes(message))).getBytes();

        //set contentLength
        int dataLength = postData.length;
        connection.setContentLength(dataLength);

        /*
         * Send data to the fcgi server.
         */
        int offset = 0;
        while (offset + MAX_PAYLOAD < dataLength) {
            connection.write(ByteBuffer.wrap(postData, offset, MAX_PAYLOAD));
            offset += MAX_PAYLOAD;
        }
        connection.write(ByteBuffer.wrap(postData, offset, dataLength - offset));


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