package com.happyr.mq2php.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Serialize a message so it can be put on the queue.
 */
public class Serializer {
    /**
     * Return a serialized version of this message.
     */
    public static String serialize(byte[] bytes) {
        try {
            return URLEncoder.encode(new String(bytes), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
}
