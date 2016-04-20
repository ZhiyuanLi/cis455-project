package edu.upenn.cis455.storage;
import java.util.ArrayList;
import java.util.List;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Entity;
/**
 * Channel entity in database, each channel belongs to a user
 * @author weisong
 */
@Entity
public class Channel
{
	@PrimaryKey
	private String cName;
	private List<String> xPaths = new ArrayList<String>();
	// pretty format
	private String xslURL;
	// store XML file urls that match one of the XPath expressions
	private List<String> matchedURLs = new ArrayList<String>();
	private String userName;

	/**
	 * Create a new channel
	 */
	public Channel()
	{
	}

	/**
	 * Create a new channel
	 * @param cName - the name of the new channel
	 * @param xPaths - the list of XPaths to add
	 * @param xslURL - the URL to add
	 */
	public Channel(String cName, List<String> xPaths, String xslURL)
	{
		this.cName = cName;
		this.xPaths = xPaths;
		this.xslURL = xslURL;
	}

	/**
	 * set channel name
	 * @param cName - the name of the channel
	 */
	public void setCName(String cName)
	{
		this.cName = cName;
	}

	/**
	 * get channel name
	 * @return Returns the name of the channel
	 */
	public String getCName()
	{
		return this.cName;
	}

	/**
	 * set channel xpaths
	 * @param xPaths - the xpaths to associate to the channel
	 */
	public void setChannelXpaths(ArrayList<String> xPaths)
	{
		this.xPaths = xPaths;
	}

	/**
	 * get channel xpaths
	 * @return Returns the channel's xpaths
	 */
	public List<String> getChannelXpaths()
	{
		return this.xPaths;
	}

	/**
	 * get xsl url
	 * @return Returns the URL
	 */
	public String getXslUrl()
	{
		return this.xslURL;
	}

	/**
	 * set xsl url
	 * @param url - The new URL
	 */
	public void setXslUrl(String url)
	{
		this.xslURL = url;
	}

	/**
	 * set user name
	 * @param userName - the name of the channel's user
	 */
	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	/**
	 * get user name
	 * @return Returns the channel's user
	 */
	public String getUserName()
	{
		return userName;
	}

	/**
	 * add url of XML that match one of the xpaths expressions to channel
	 * @param url - the URL to add to the channel
	 */
	public void putMatchedURLs(String url)
	{
		this.matchedURLs.add(url);
	}

	/**
	 * get all urls of XML files that match one of the xpaths expressions in this channel
	 * @return Returns all XML files matching an xpath
	 */
	public List<String> getMatchedURLs()
	{
		return this.matchedURLs;
	}
}
