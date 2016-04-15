package test.edu.upenn.cis455;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import edu.upenn.cis455.xpathengine.XPathEngineImpl;
import junit.framework.TestCase;

public class XPathEngineImplTest extends TestCase {

	/**
	 * test setPaths
	 */
	public void testSetPaths() {
		XPathEngineImpl engine = new XPathEngineImpl();
		String[] xpaths = new String[3];
		xpaths[0] = "crawltest.cis.upenn.edu/nytimes/Africa.xml";
		xpaths[1] = "http://crawltest.cis.upenn.edu/nytimes/Americas.xml";
		xpaths[2] = "http://crawltest.cis.upenn.edu/bbc/frontpage.xml";
		engine.setXPaths(xpaths);
		assertEquals(engine.xPaths.size(), 3);
		assertEquals(engine.xPaths.get(0), "crawltest.cis.upenn.edu/nytimes/Africa.xml");
		assertEquals(engine.xPaths.get(1), "http://crawltest.cis.upenn.edu/nytimes/Americas.xml");
		assertEquals(engine.xPaths.get(2), "http://crawltest.cis.upenn.edu/bbc/frontpage.xml");
	}

	/**
	 * test sisValid
	 */
	public void testIsValid() {
		XPathEngineImpl engine = new XPathEngineImpl();
		String[] xpaths = new String[16];
		xpaths[0] = "/crawltest.cis.upenn.edu/nytimes/Africa.xml";
		xpaths[1] = "http://crawltest.cis.upenn.edu/nytimes/Americas.xml";
		xpaths[2] = "http://crawltest.cis.upenn.edu/bbc/frontpage.xml";
		xpaths[3] = "/foo/bar/xyz";
		xpaths[4] = "/foo/bar[@att=\"123\"]";
		xpaths[5] = "/xyz/abc[contains(text(),\"someSubstring\")]";
		xpaths[6] = "/a/b/c[text()=\"theEntireText\"]";
		xpaths[7] = "/blah[anotherElement]";
		xpaths[8] = "/this/that[something/else]";
		xpaths[9] = "/d/e/f[foo[text()=\"something\"]][bar]";
		xpaths[10] = "/a/b/c[text() =     \"whiteSpacesShouldNotMatter\"]";
		xpaths[11] = "/a/b[foo[text()=\"#$(/][]\"]][bar]/hi[@asdf=\"#$(&[]\"][this][is][crazy]";
		xpaths[12] = "/test[a/b1[c1[p]/d[p]]/n1[a]/n2[c2/d[p]/e[text()=\"/asp[&123(123*/]\"]]]";
		xpaths[13] = "/note/hello4/this[@val=\"text1\"]/that[@val=\"text2\"][something/else]";
		xpaths[14] = "/note/hello1/to[text()=\"text2\"][@vp=\"text1\"]";
		xpaths[15] = "/foo/bar[@abc=\"This is a \"quoted\" test\"]";

		engine.setXPaths(xpaths);

		for (int i = 0; i < engine.xPaths.size(); i++) {
			assertTrue(engine.isValid(i));
		}
	}

	/**
	 * test evaluate easy test and one level nested
	 */
	public void testEvaluateNormal() {
		XPathEngineImpl engine = new XPathEngineImpl();
		String[] xpaths = new String[5];
		String url = "http://crawltest.cis.upenn.edu/nytimes/Africa.xml";
		// three positive tests and two negative tests
		xpaths[0] = "/rss/channel";
		xpaths[1] = "/rss/channel/image[title[text() = \"NYT > Africa\"]]";
		xpaths[2] = "/rss/channel[image/url]";

		xpaths[3] = "/rss/notExist/title";
		xpaths[4] = "/rss/channel/title/notExist/title";
		engine.setXPaths(xpaths);
		Document d = getDom(url);
		boolean[] result = engine.evaluate(d);
		assertTrue(result[0]);
		assertTrue(result[1]);
		assertTrue(result[2]);

		assertFalse(result[3]);
		assertFalse(result[4]);
	}

	/**
	 * test evaluate with text() function
	 */
	public void testEvaluateText() {
		XPathEngineImpl engine = new XPathEngineImpl();
		String[] xpaths = new String[3];
		String url = "http://crawltest.cis.upenn.edu/bbc/middleeast.xml";
		// three positive tests and one negative tests
		xpaths[0] = "/rss";
		xpaths[1] = "/rss/channel/title[text() = \"BBC News | Middle East | World Edition\"]";
		xpaths[2] = "/rss/channel/title[text() = \"Not Exist\"]";
		engine.setXPaths(xpaths);
		Document d = getDom(url);
		boolean[] result = engine.evaluate(d);
		for (int i = 0; i < 2; i++) {
			assertTrue(result[i]);
		}
		assertFalse(result[2]);
	}

	/**
	 * test evaluate with contains() function
	 */
	public void testEvaluateContains() {
		XPathEngineImpl engine = new XPathEngineImpl();
		String[] xpaths = new String[3];
		String url = "http://crawltest.cis.upenn.edu/bbc/middleeast.xml";
		xpaths[0] = "/rss/channel/title[contains(text(), \"BBC News | Middle East | World Edition\")]";
		xpaths[1] = "/rss/channel/title[contains(text(), \"Middle\")]";
		xpaths[2] = "/rss/channel/title[contains(text(), \"not exist\")]";
		engine.setXPaths(xpaths);
		Document d = getDom(url);
		boolean[] result = engine.evaluate(d);
		assertTrue(result[0]);
		assertTrue(result[1]);
		assertFalse(result[2]);
	}

	/**
	 * test evaluate with @attribute function
	 */
	public void testEvaluateAttribute() {
		XPathEngineImpl engine = new XPathEngineImpl();
		String[] xpaths = new String[2];
		String url = "http://crawltest.cis.upenn.edu/bbc/middleeast.xml";
		xpaths[0] = "/rss/channel/item/guid[@isPermaLink =   \"false\"]";
		xpaths[1] = "/rss/channel/item/guid[@isPermaLink = \"notExist\"]";
		engine.setXPaths(xpaths);
		Document d = getDom(url);
		boolean[] result = engine.evaluate(d);
		assertTrue(result[0]);
		assertFalse(result[1]);
	}

	/**
	 * test evaluate, tricky tests with nested condition and multiple tests on
	 * the same level
	 */
	public void testMultipleCondition() {
		XPathEngineImpl engine = new XPathEngineImpl();
		String[] xpaths = new String[5];
		String url = "http://crawltest.cis.upenn.edu/nytimes/Africa.xml";
		xpaths[0] = "/rss/channel[title[text() = \"NYT > Africa\"]][link][description]";
		xpaths[1] = "/rss/channel[title[text() = \"NYT > Africa\"][contains(text(),\"YT\")]][link][description]";
		xpaths[2] = "/rss/channel[title[text() = \"NYT > Africa\"][contains(text(),\"weird\")]][link][description]";
		xpaths[3] = "/rss/channel[title][link][description[contains(text(),\"breaking\")]]";
		xpaths[4] = "/rss/channel[title][link][description[contains(text(),\"not exist\")]]";
		engine.setXPaths(xpaths);
		Document d = getDom(url);
		boolean[] result = engine.evaluate(d);
		assertTrue(result[0]);
		assertTrue(result[1]);
		assertFalse(result[2]);
		assertTrue(result[3]);
		assertFalse(result[4]);
	}

	/**
	 * API used only for test, convert url string to Document
	 * 
	 * @param url
	 * @return
	 */
	public Document getDom(String url) {
		URL URL;
		try {
			URL = new URL(url);
		} catch (MalformedURLException e) {
			return null;
		}
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document dom;
			dom = dBuilder.parse(URL.openStream());
			return dom;
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
