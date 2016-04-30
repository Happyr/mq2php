package com.happyr.mq2php.message;

import com.happyr.mq2php.message.Header;

import java.util.ArrayList;

/**
 * Message
 */
public class Message {

    private ArrayList<Header> headers;
    private String body;

    public String getHeaderValueByName(String name) {
        Header header = getHeaderByName(name);

        if (header == null) {
            return null;
        }

        return header.getValue();
    }

    public Header getHeaderByName(String name) {
        for(Header header: headers) {
            if (header.getKey().equals(name)) {
                return header;
            }
        }

        return null;
    }

    public void setHeader(String key, String value) {
        Header header = getHeaderByName(key);

        if (header != null) {
            header.setValue(value);

            return;
        }

        // Create new header
        header = new Header(key, value);
        headers.add(header);
    }

    public ArrayList<Header> getHeaders() {
        return headers;
    }

    public void setHeaders(ArrayList<Header> headers) {
        this.headers = headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
