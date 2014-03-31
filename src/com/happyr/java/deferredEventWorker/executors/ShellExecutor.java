package com.happyr.java.deferredEventWorker.executors;

import com.happyr.java.deferredEventWorker.Message;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by tobias on 30/03/14.
 */
public class ShellExecutor implements ExecutorInterface {

    public String execute(Message message) {

        StringBuffer output = new StringBuffer();

        String command = message.getHeader("php_bin") + " " + message.getHeader("console_path") + " fervo:deferred-event:dispatch " + message.getData();
        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            String line = "";

            //Read std out
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }

            //read str err
            reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
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
