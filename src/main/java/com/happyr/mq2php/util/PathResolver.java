package com.happyr.mq2php.util;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * com.happyr.java.deferredEventWorker
 *
 * @author Tobias Nyholm
 */
public class PathResolver {

    /**
     * Make sure that the path does not include any ..
     *
     * @param path
     * @return String
     */
    public static String resolve(String path) {
        String normalized = null;

        try {
            normalized = new URI(path).normalize().getPath();
        } catch (URISyntaxException e) {

        }

        return normalized;
    }
}
