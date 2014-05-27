import com.happyr.java.deferredEventWorker.PathResolver;
import junit.framework.TestCase;
import org.junit.Test;

/**
 * PACKAGE_NAME
 *
 * @author Tobias Nyholm
 */

public class PathResolverTest extends TestCase {

    @Test
    public void testResolve() {

        assertEquals("/home/test/baz", PathResolver.resolve("/home/test/foobar/../baz"));
        assertEquals("/home/baz", PathResolver.resolve("/home/test/foobar/../../baz"));
        assertEquals("/home/test/baz/file.php", PathResolver.resolve("/home/test/foobar/../baz/file.php"));


        assertEquals("/home/test/baz/file.php", PathResolver.resolve("/home/test/baz/file.php"));
        assertEquals("/", PathResolver.resolve("/"));

    }

}
