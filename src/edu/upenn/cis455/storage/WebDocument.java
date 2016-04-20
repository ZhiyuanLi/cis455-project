package edu.upenn.cis455.storage;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

/**
 * WebDocument entity in database
 * 
 * @author weisong
 *
 */
@Entity
public class WebDocument {
	private String content;
	private long lastCrawlTime;

	@PrimaryKey
	private String url;

	public WebDocument() {
	}

	public WebDocument(String url) {
		this.url = url;
	}

	// *********** basic *************

	/**
	 * set url of web page
	 * 
	 * @param url
	 */
	public void setURL(String url) {
		this.url = url;
	}

	/**
	 * get url of web page
	 * 
	 * @return
	 */
	public String getURL() {
		return this.url;
	}

	/**
	 * set last crawl time of web page
	 * 
	 * @param lastCrawlTime
	 */
	public void setLastCrawlTime(long lastCrawlTime) {
		this.lastCrawlTime = lastCrawlTime;
	}

	/**
	 * get last crawl time of web page
	 * 
	 * @return
	 */
	public long getLastCrawlTime() {
		return this.lastCrawlTime;
	}

	/**
	 * set document content
	 * 
	 * @param content
	 */
	public void setDocumentContent(String content) {
		this.content = content;
	}

	/**
	 * get document content
	 * 
	 * @return
	 */
	public String getDocumentContent() {
		return this.content;
	}
}
