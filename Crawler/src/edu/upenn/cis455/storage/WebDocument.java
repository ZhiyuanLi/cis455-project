// HTML document entity for BerkeleyDB
// Author: Wei Song
package edu.upenn.cis455.storage;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.SecondaryKey;
import com.sleepycat.persist.model.Relationship;
import java.util.HashSet;
/**
 * WebDocument entity in database
 * @author weisong
 */
@Entity
public class WebDocument
{
	@SecondaryKey(relate=Relationship.MANY_TO_ONE)
	private String hashValue;
	private String content;
	private String title = "";
	private HashSet<String> hits;
	private String state = "";
	private String city = "";
	private long lastCrawlTime;
	@PrimaryKey
	private String url;
	/**
	 * Constructor
	 */
	public WebDocument()
	{
		hits = new HashSet<String>();
	}

	/**
	 * Constructor
	 * @param url - the url of the document
	 */
	public WebDocument(String url)
	{
		hits = new HashSet<String>();
		this.url = url;
	}

	public String getCity()
	{
		return city;
	}

	public void setCity(String city)
	{
		this.city = city;
	}

	public void setState(String state)
	{
		this.state = state;
	}

	public String getState()
	{
		return state;
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
	 * Set the hash code
	 * @param hash - the document's hash value
	 */
	public void setHash(String hash)
	{
		this.hashValue = hash;
	}

	/**
	 * Get the document's hash value
	 * @return Returns the document's hash value
	 */
	public String getHash()
	{
		return hashValue;
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

	/**
	 * Add a hit
	 * @param hit - the URL to add to this document
	 */
	public void addHit(String hit)
	{
		hits.add(hit);
		System.out.println("Hits inside addHit = " + hits);
	}

	public HashSet<String> getHits()
	{
		return hits;
	}
}
