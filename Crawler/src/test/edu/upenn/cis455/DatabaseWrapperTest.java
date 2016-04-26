package test.edu.upenn.cis455;
import java.util.ArrayList;
import java.util.List;
import edu.upenn.cis455.storage.Channel;
import edu.upenn.cis455.storage.DatabaseWrapper;
import edu.upenn.cis455.storage.User;
import edu.upenn.cis455.storage.WebDocument;
import junit.framework.TestCase;
public class DatabaseWrapperTest extends TestCase
{
	DatabaseWrapper db;
	String path;
	/**
	 * Setup the test suite
	 * @throws Exception if the database cannot be opened
	 */
	protected void setUp() throws Exception
	{
		path = "./testDatabase";
		db = new DatabaseWrapper(path);
	}

	/**
	 * test document entity
	 */
	public void testDocument()
	{
		// db already created
		assertFalse(db == null);
		WebDocument doc = new WebDocument("www.google.com");
		doc.setDocumentContent("Test");
		db.addDocument(doc);
		assertEquals(db.getDocumentList().size(), 1);
		assertEquals(doc.getURL(), "www.google.com");
		assertEquals(doc.getDocumentContent(), "Test");
	}
}
