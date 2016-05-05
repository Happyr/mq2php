package com.happyr.mq2php.message;

/**
 * A representation of a message header.
 */
public class Header {

    private String key;
    private String value;

    public Header() {
    }

    public Header(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
