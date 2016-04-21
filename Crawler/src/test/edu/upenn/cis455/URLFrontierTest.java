package test.edu.upenn.cis455;
import java.util.ArrayList;
import java.util.List;
import edu.upenn.cis455.storage.Channel;
import edu.upenn.cis455.storage.DatabaseWrapper;
import edu.upenn.cis455.storage.User;
import edu.upenn.cis455.storage.WebDocument;
import junit.framework.TestCase;
public class URLFrontierTest extends TestCase
{
	URLFrontierPartitioned URLQueue;
	/**
	 * Setup the test suite
	 * @throws Exception if the database cannot be opened
	 */
	protected void setUp()
	{
		URLQueue = new URLFrontierPartitioned();
	}

	/**
	 * test channel entity
	 */
	public void testChannel()
	{
	}

	/**
	 * test user entity
	 */
	public void testUser()
	{
	}

	/**
	 * test document entity
	 */
	public void testDocument()
	{
	}
}
