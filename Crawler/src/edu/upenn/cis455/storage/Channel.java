package edu.upenn.cis455.storage;

import java.util.ArrayList;
import java.util.List;

import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Entity;

/**
 * Channel entity in database, each channel belongs to a user
 * 
 * @author weisong
 *
 */
@Entity
public class Channel {

	@PrimaryKey
	private String cName;
	private List<String> xPaths = new ArrayList<String>();
	private String xslURL; // pretty format
	// store XML file urls that match one of the XPath expressions
	private List<String> matchedURLs = new ArrayList<String>();
	private String userName;

	public Channel() {
	}

	public Channel(String cName, List<String> xPaths, String xslURL) {
		this.cName = cName;
		this.xPaths = xPaths;
		this.xslURL = xslURL;
	}

	// ************* basic ***************

	/**
	 * set channel name
	 * 
	 * @param cName
	 */
	public void setCName(String cName) {
		this.cName = cName;
	}

	/**
	 * get channel name
	 * 
	 * @return
	 */
	public String getCName() {
		return this.cName;
	}

	/**
	 * set channel xpaths
	 * 
	 * @param xPaths
	 */
	public void setChannelXpaths(ArrayList<String> xPaths) {
		this.xPaths = xPaths;
	}

	/**
	 * get channel xpaths
	 * 
	 * @return
	 */
	public List<String> getChannelXpaths() {
		return this.xPaths;
	}

	/**
	 * get xsl url
	 * 
	 * @return
	 */
	public String getXslUrl() {
		return this.xslURL;
	}

	/**
	 * set xsl url
	 * 
	 * @param url
	 */
	public void setXslUrl(String url) {
		this.xslURL = url;
	}

	// ************* advanced ***************

	/**
	 * set user name
	 * 
	 * @param userName
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * get user name
	 * 
	 * @return
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * add url of XML that match one of the xpaths expressions to channel
	 * 
	 * @param url
	 */
	public void putMatchedURLs(String url) {
		this.matchedURLs.add(url);
	}

	/**
	 * get all urls of XML files that match one of the xpaths expressions in
	 * this channel
	 * 
	 * @return
	 */
	public List<String> getMatchedURLs() {
		return this.matchedURLs;
	}

}
