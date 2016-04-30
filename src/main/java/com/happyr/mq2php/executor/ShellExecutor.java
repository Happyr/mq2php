package com.happyr.mq2php.executor;

import com.happyr.mq2php.message.Message;
import com.happyr.mq2php.util.Marshaller;
import com.happyr.mq2php.util.PathResolver;
import com.happyr.mq2php.util.Serializer;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Execute the message payload with the php cli
 *
 * @author Tobias Nyholm
 */
public class ShellExecutor implements ExecutorInterface {

    /**
     *
     * @param message
     * @return
     */
    public String execute(Message message) {

        StringBuffer output = new StringBuffer();

        String command = message.getHeaderValueByName("php_bin") + " " +
                PathResolver.resolve(message.getHeaderValueByName("dispatch_path")) + " " +
                Serializer.serialize(Marshaller.toBytes(message));

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            String line = "";

            //Read std out
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            //read str err
            reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

        } catch (Exception e) {
            return e.getMessage();
        }

        //if error
        if (0 != p.exitValue()) {
            return output.toString().replaceAll("\n", "").replaceAll("\r", "");
        }

        return null;
    }
}
