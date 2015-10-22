package com.happyr.mq2php.executors;

import com.happyr.mq2php.Message;
import com.happyr.mq2php.PathResolver;

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

        String command = message.getHeader("php_bin") + " " +
                PathResolver.resolve(message.getHeader("dispatch_path")) + " " +
                message.serialize();

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
