package com.cybozu.labs.langdetect.util;
import static org.junit.Assert.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
/**
 * @author Nakatani Shuyo
 */
public class TagExtractorTest
{
    	/**
    	 * Construct the testing suite
	 * @throws java.lang.Exception - if the suite failed
    	 */
    	@BeforeClass
    	public static void setUpBeforeClass() throws Exception
	{
    	}

    	/**
	 * Tear down the testing suite
     	 * @throws java.lang.Exception - if the testing suite failed
     	 */
    	@AfterClass
    	public static void tearDownAfterClass() throws Exception
	{
    	}

    	/**
	 * Construct the testing suite
    	 * @throws java.lang.Exception - if the testing suite failed
    	 */
    	@Before
    	public void setUp() throws Exception
	{
    	}

    	/**
    	 * Tear down the testing suite
	 * @throws java.lang.Exception - if the testing suite failed
    	 */
    	@After
    	public void tearDown() throws Exception
	{
    	}

    	/**
    	 * Test method for {@link com.cybozu.labs.langdetect.util.TagExtractor#TagExtractor(java.lang.String, int)}.
    	 */
    	@Test
    	public final void testTagExtractor()
	{
        	TagExtractor extractor = new TagExtractor(null, 0);
        	assertEquals(extractor.target_, null);
        	assertEquals(extractor.threshold_, 0);
        	TagExtractor extractor2 = new TagExtractor("abstract", 10);
        	assertEquals(extractor2.target_, "abstract");
        	assertEquals(extractor2.threshold_, 10);
	}

    	/**
    	 * Test method for {@link com.cybozu.labs.langdetect.util.TagExtractor#setTag(java.lang.String)}.
    	 */
    	@Test
    	public final void testSetTag()
	{
        	TagExtractor extractor = new TagExtractor(null, 0);
        	extractor.setTag("");
        	assertEquals(extractor.tag_, "");
        	extractor.setTag(null);
        	assertEquals(extractor.tag_, null);
    	}

    	/**
    	 * Test method for {@link com.cybozu.labs.langdetect.util.TagExtractor#add(java.lang.String)}.
    	 */
    	@Test
    	public final void testAdd()
	{
    		TagExtractor extractor = new TagExtractor(null, 0);
        	extractor.add("");
        	// ignore
		extractor.add(null);
    	}

    	/**
    	 * Test method for {@link com.cybozu.labs.langdetect.util.TagExtractor#closeTag(com.cybozu.labs.langdetect.util.LangProfile)}.
    	 */
    	@Test
    	public final void testCloseTag()
	{
    		TagExtractor extractor = new TagExtractor(null, 0);
        	// ignore
		extractor.closeTag();
    	}

    
    	/**
    	 * Scenario Test of extracting &lt;abstract&gt; tag from Wikipedia database.
    	 */
    	@Test
    	public final void testNormalScenario()
	{
        	TagExtractor extractor = new TagExtractor("abstract", 10);
        	assertEquals(extractor.count(), 0);
        	LangProfile profile = new LangProfile("en");
        	// normal
        	extractor.setTag("abstract");
        	extractor.add("This is a sample text.");
        	profile.update(extractor.closeTag());
        	assertEquals(extractor.count(), 1);
        	// Thisisasampletest
		assertEquals(profile.n_words[0], 17);
		// _T, TH, hi, ...
       		assertEquals(profile.n_words[1], 22);
        	// _Th, Thi, his, ...
		assertEquals(profile.n_words[2], 17);
        	// too short
        	extractor.setTag("abstract");
        	extractor.add("sample");
        	profile.update(extractor.closeTag());
        	assertEquals(extractor.count(), 1);
        	// other tags
        	extractor.setTag("div");
        	extractor.add("This is a sample text which is enough long.");
        	profile.update(extractor.closeTag());
        	assertEquals(extractor.count(), 1);
    	}

    	/**
    	 * Test method for {@link com.cybozu.labs.langdetect.util.TagExtractor#clear()}.
    	 */
    	@Test
    	public final void testClear()
	{
        	TagExtractor extractor = new TagExtractor("abstract", 10);
        	extractor.setTag("abstract");
        	extractor.add("This is a sample text.");
        	assertEquals(extractor.buf_.toString(), "This is a sample text.");
        	assertEquals(extractor.tag_, "abstract");
        	extractor.clear();
        	assertEquals(extractor.buf_.toString(), "");
        	assertEquals(extractor.tag_, null);
    	}
}
