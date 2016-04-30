package com.happyr.mq2php.util;

import com.happyr.mq2php.util.PathResolver;
import junit.framework.TestCase;

/**
 *
 * @author Tobias Nyholm
 */
public class PathResolverTest extends TestCase {

    public void testResolve() {

        assertEquals("/home/test/baz", PathResolver.resolve("/home/test/foobar/../baz"));
        assertEquals("/home/baz", PathResolver.resolve("/home/test/foobar/../../baz"));
        assertEquals("/home/test/baz/file.php", PathResolver.resolve("/home/test/foobar/../baz/file.php"));


        assertEquals("/home/test/baz/file.php", PathResolver.resolve("/home/test/baz/file.php"));
        assertEquals("/", PathResolver.resolve("/"));

    }
}
