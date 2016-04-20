package test.edu.upenn.cis455;
import junit.framework.TestCase;
import java.util.ArrayList;
import java.util.List;
import edu.upenn.cis455.crawler.*;
import edu.upenn.cis455.storage.*;
/**
 * Test for Crawler.java
 * @author weisong, cbesser
 */
public class CrawlerTest extends TestCase
{
	Crawler crawler;
	String startURL;
	String dbPath;
	String indexDBPath;
	String urlFile;
	int maxSize;
	int maxNum;
	DatabaseWrapper db;
	IndexWrapper indexDB;
	/**
	 * Setup the test suite
	 * @throws Exception if a database cannot be opened
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
		dbPath = "./testDatabase1";
		indexDBPath = "./testIndexDB1";
		urlFile = "./urls";
		maxSize = 100000;
		maxNum = 1000000;
		db = new DatabaseWrapper(dbPath);
		indexDB = new IndexWrapper(indexDBPath);
	}

	/**
	 * test crawl XML file
	 */
	public void testXML()
	{
		crawler = new Crawler(dbPath, indexDBPath, urlFile, maxSize, maxNum);
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
		assertEquals(cReturn1.getMatchedURLs().size(), 0);
		assertEquals(cReturn2.getMatchedURLs().size(), 0);
	}

	/**
	 * test crawl HTML file
	 */
	public void testHTML()
	{
		crawler = new Crawler(dbPath, indexDBPath, urlFile, maxSize, maxNum);
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
		assertEquals(cReturn1.getMatchedURLs().size(), 0);
		assertEquals(cReturn2.getMatchedURLs().size(), 0);
	}

	/**
	 * Test the content body extraction function
	 */
	public void testExtractContent()
	{
		crawler = new Crawler(dbPath, indexDBPath, urlFile, maxSize, maxNum);
		String content = "<volume>15</volume><number>2</number><articles><article><title>Load balancing in a locally distributed DB system</title><initPage>15</initPage>";
		String noHTML = crawler.extractContent(content);
		System.out.println(noHTML);
		String correct = "15 2 load balancing in a locally distributed db system 15";
		assertEquals(noHTML, correct);
	}
}
