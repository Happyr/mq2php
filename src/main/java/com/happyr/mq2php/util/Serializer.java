package com.happyr.mq2php.util;

import org.apache.commons.codec.binary.Base64;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 *
 */
public class Serializer {
    /**
     * Return a serialized version of this message
     * @return
     */
    public static String serialize(byte[] bytes){
        try {
            return URLEncoder.encode(new String(Base64.encodeBase64(bytes)), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
}
