package com.happyr.java.deferredEventWorker.executors;

import com.happyr.java.deferredEventWorker.Message;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Execute the message payload with HTTP
 *
 * @author Tobias Nyholm
 */
public class HttpExecutor implements ExecutorInterface {
    /**
     * @param message
     * @return
     */
    @Override
    public String execute(Message message) {
        try {
            return doExecute(message);
        } catch (Exception e) {
            return "Unknown java error: " + e.getMessage();
        }
    }

    /**
     * @param message
     * @return
     * @throws Exception
     */
    private String doExecute(Message message) throws IOException {
        String url = "http://" + message.getHeader("fastcgi_host") + ":" + message.getHeader("fastcgi_post") + message.getHeader("dispatch_path");

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add request header
        con.setRequestMethod("POST");

        String urlParameters = "DEFERRED_DATA=" + message.getData();

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();


        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //if error
        if (responseCode >= 400 || responseCode == 0) {
            return response.toString();
        }

        return null;
    }

}