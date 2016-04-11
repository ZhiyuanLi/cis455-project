package test.edu.upenn.cis455;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

import edu.upenn.cis455.crawler.*;
import edu.upenn.cis455.storage.*;

/**
 * Test for XPathCrawler
 * 
 * @author weisong
 *
 */
public class XPathCrawlerTest extends TestCase {
	XPathCrawler crawler;
	String startURL;
	String dbPath;
	int maxSize;
	int maxNum;
	DatabaseWrapper db;

	protected void setUp() throws Exception {
		super.setUp();
		dbPath = "./testDatabase1";
		maxSize = 100000;
		maxNum = 1000000;
		db = new DatabaseWrapper(dbPath);

	}

	/**
	 * test crawl XML file
	 */
	public void testXML() {
		startURL = "http://crawltest.cis.upenn.edu/bbc/frontpage.xml";
		crawler = new XPathCrawler(startURL, dbPath, maxSize, maxNum);

		List<String> xPaths1 = new ArrayList<String>();
		List<String> xPaths2 = new ArrayList<String>();
		xPaths1.add("/rss/channel/title");
		xPaths2.add("/NotExist");
		Channel c1 = new Channel("channel1", xPaths1, "nothing");
		Channel c2 = new Channel("channel2", xPaths2, "nothing");
		c1.setUserName("user1");
		c2.setUserName("user2");
		db.addChannel(c1);
		db.addChannel(c2);
		assertEquals(c1.getMatchedURLs().size(), 0);
		assertEquals(c2.getMatchedURLs().size(), 0);

		crawler.startCrawling();

		Channel cReturn1 = db.getChannel("channel1");
		Channel cReturn2 = db.getChannel("channel2");
		assertTrue(cReturn1 != null);
		assertTrue(cReturn2 != null);
		assertEquals(cReturn1.getMatchedURLs().size(), 1);
		assertEquals(cReturn2.getMatchedURLs().size(), 0);
	}

	/**
	 * test crawl HTML file
	 */
	public void testHTML() {
		startURL = "http://crawltest.cis.upenn.edu/bbc/";
		crawler = new XPathCrawler(startURL, dbPath, maxSize, maxNum);

		List<String> xPaths1 = new ArrayList<String>();
		List<String> xPaths2 = new ArrayList<String>();
		xPaths1.add("/rss/channel/title");
		xPaths2.add("/NotExist");
		Channel c1 = new Channel("channel1", xPaths1, "nothing");
		Channel c2 = new Channel("channel2", xPaths2, "nothing");
		c1.setUserName("user1");
		c2.setUserName("user2");
		db.addChannel(c1);
		db.addChannel(c2);
		assertEquals(c1.getMatchedURLs().size(), 0);
		assertEquals(c2.getMatchedURLs().size(), 0);

		crawler.startCrawling();

		Channel cReturn1 = db.getChannel("channel1");
		Channel cReturn2 = db.getChannel("channel2");
		assertTrue(cReturn1 != null);
		assertTrue(cReturn2 != null);
		assertEquals(cReturn1.getMatchedURLs().size(), 4);
		assertEquals(cReturn2.getMatchedURLs().size(), 0);
		assertEquals(cReturn1.getMatchedURLs().get(0), "http://crawltest.cis.upenn.edu/bbc/frontpage.xml");

		String URL = "http://crawltest.cis.upenn.edu/bbc/middleeast.xml";
		WebDocument webDoc = db.getDocument(URL);
		assertEquals(webDoc.getURL(), URL);
	}
}
