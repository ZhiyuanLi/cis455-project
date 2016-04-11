package test.edu.upenn.cis455;

import java.util.ArrayList;
import java.util.List;

import edu.upenn.cis455.storage.Channel;
import edu.upenn.cis455.storage.DatabaseWrapper;
import edu.upenn.cis455.storage.User;
import edu.upenn.cis455.storage.WebDocument;
import junit.framework.TestCase;

public class DatabaseWrapperTest extends TestCase {

	DatabaseWrapper db;
	String path;

	protected void setUp() throws Exception {
		path = "./testDatabase";
		db = new DatabaseWrapper(path);

	}

	/**
	 * test channel entity
	 */
	public void testChannel() {
		// db already created
		assertFalse(db == null);
		List<String> xPaths = new ArrayList<String>();
		xPaths.add("/test/path");
		Channel c = new Channel("testChannel", xPaths," ");
		User user = new User("hahaha");
		db.addUser(user);
		c.setUserName("hahaha");
		db.addChannel(c, "hahaha");

		Channel getChannel = db.getChannel("testChannel");
		assertEquals(getChannel.getCName(), "testChannel");
		assertEquals(getChannel.getChannelXpaths(), xPaths);
		assertEquals(getChannel.getUserName(), "hahaha");

		db.deleteChannel("hahaha", "testChannel");
		assertEquals(db.getChannel("testChannel"), null);
		assertEquals(db.getUser("hahaha").getChannelNames().size(), 0);
	}

	/**
	 * test user entity
	 */
	public void testUser() {
		// db already created
		assertFalse(db == null);
		User newUser = new User("weisong");
		db.addUser(newUser);
		assertEquals(db.getUser("weisong").getUserName(), "weisong");
		db.deleteUser("weisong");
		assertEquals(db.getUser("weisong"), null);
	}

	/**
	 * test documen entity
	 */
	public void testDocument() {
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
