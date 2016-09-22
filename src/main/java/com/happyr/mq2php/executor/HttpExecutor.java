package com.happyr.mq2php.executor;

import com.happyr.mq2php.message.Header;
import com.happyr.mq2php.message.Message;
import com.happyr.mq2php.util.Marshaller;
import com.happyr.mq2php.util.Serializer;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

/**
 * Execute a message by HTTP POST request.
 *
 * @author Tobias Nyholm
 */
public class HttpExecutor implements ExecutorInterface {
    public String execute(Message message) {
        Header headerUrl = message.getHeaderByName("http_url");
        if (headerUrl == null) {
            return "Message has no URL. Can't process this message with HttpExecutor";
        }

        try {
            return sendHttpPost(headerUrl, Serializer.serialize(Marshaller.toBytes(message)));
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    private String sendHttpPost(Header headerUrl, String body) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpPost httpPost = new HttpPost(headerUrl.getValue());
            httpPost.addHeader("Content-Type", "application/json");
            httpPost.setEntity(new StringEntity(body));
            CloseableHttpResponse response = httpclient.execute(httpPost);

            try {
                System.out.println(response.getStatusLine());
                int httpStatus = response.getStatusLine().getStatusCode();

                if (httpStatus != 200) {
                    return response.toString();
                }
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }

        return null;
    }
}
