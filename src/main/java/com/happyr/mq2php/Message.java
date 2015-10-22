package com.happyr.mq2php;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import org.apache.commons.codec.binary.Base64;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

/**
 * This class represent a message on the message queue.
 *
 * @author Tobias Nyholm
 */
public class Message {

    private Map headers;

    private String data;

    public Message(String raw) {
        headers = new Hashtable();

        try {
            parseRawData(raw);
        } catch (IOException e) {

        }
    }

    /**
     * Parse the raw data from the message queue
     *
     * @param raw
     * @throws IOException
     */
    private void parseRawData(String raw) throws IOException {

        BufferedReader reader = new BufferedReader(new StringReader(raw));
        String line;
        int idx;

        line = reader.readLine();
        while (!line.equals("")) {
            idx = line.indexOf(':');
            if (idx < 0) {
                headers = null;
                break;
            } else {
                headers.put(line.substring(0, idx).toLowerCase(), line.substring(idx + 1).trim());
            }
            line = reader.readLine();
        }

        data = reader.readLine();
    }

    /**
     * Return a HTTP like message
     */
    public String getFormattedMessage() {
        String value;
        StringBuilder sb = new StringBuilder();
        Set<String> keys = headers.keySet();
        for (String key : keys) {
            value = (String) headers.get(key);
            if (value.isEmpty()) {
                continue;
            }

            sb.append(key + ": " + value + "\n");
        }
        sb.append("\n" + data);

        return sb.toString();
    }

    public String getData() {
        return data;
    }

    public String getHeader(String name) {
        return (String) headers.get(name);
    }

    public void addHeader(String key, String value) {
        headers.put(key, value.replaceAll(":", "=").replaceAll("[\r\n]", " #> "));
    }

    /**
     * Return a serialized version of this message
     * @return
     */
    public String serialize(){
        byte[] bytes = this.getFormattedMessage().getBytes();
        return new String(Base64.encodeBase64(bytes));
    }
}
