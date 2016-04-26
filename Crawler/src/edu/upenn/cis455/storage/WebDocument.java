// HTML document entity for BerkeleyDB
// Author: Wei Song
package edu.upenn.cis455.storage;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.SecondaryKey;
import com.sleepycat.persist.model.Relationship;
/**
 * WebDocument entity in database
 * @author weisong
 */
@Entity
public class WebDocument
{
	@SecondaryKey(relate=Relationship.MANY_TO_ONE)
	private String content;
	private String title = "";
	private long lastCrawlTime;
	@PrimaryKey
	private String url;
	/**
	 * Constructor
	 */
	public WebDocument()
	{
	}

	/**
	 * Constructor
	 * @param url - the url of the document
	 */
	public WebDocument(String url)
	{
		this.url = url;
	}

	/**
	 * set url of web page
	 * @param url - the URL of the page
	 */
	public void setURL(String url)
	{
		this.url = url;
	}

	/**
	 * get url of web page
	 * @return Returns the associated URL
	 */
	public String getURL()
	{
		return this.url;
	}

	/**
	 * set last crawl time of web page
	 * @param lastCrawlTime - the time since the page was last crawled
	 */
	public void setLastCrawlTime(long lastCrawlTime)
	{
		this.lastCrawlTime = lastCrawlTime;
	}

	/**
	 * get last crawl time of web page
	 * @return Returns the last time the document was crawled
	 */
	public long getLastCrawlTime()
	{
		return this.lastCrawlTime;
	}

	/**
	 * set document content
	 * @param content - the content of the document
	 */
	public void setDocumentContent(String content)
	{
		this.content = content;
	}

	/**
	 * get document content
	 * @return - Returns the content of the document
	 */
	public String getDocumentContent()
	{
		return this.content;
	}

	/**
	 * Set document title
	 * @param title - the document title
	 */
	public void setDocumentTitle(String title)
	{
		this.title = title;
	}

	/**
	 * Get the document title
	 * @return returns the document title
	 */
	public String getDocumentTitle()
	{
		return title;
	}
}
