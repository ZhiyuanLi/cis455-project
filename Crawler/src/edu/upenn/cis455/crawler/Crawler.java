package edu.upenn.cis455.crawler;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.BufferedReader;
import java.io.FileReader;
import edu.upenn.cis455.storage.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import org.jsoup.Jsoup;
import java.io.BufferedReader;
import java.io.FileReader;
/**
 * Crawler for CIS 555 Final Project
 * @author weisong, cbesser
 */
public class Crawler
{
	private String dbDirectory = "";
	private int maxSize = Integer.MAX_VALUE;
	private int maxNum = Integer.MAX_VALUE;
	private String indexerDBDir = "";
	private String URLFile = "";
	// header response
	private String contentType;
	private int contentLen;
	private long lastModified;
	public int count;
	private DatabaseWrapper db;
	private IndexWrapper indexdb;
	private WebDocument webDocument;
	// a small sample of common English words for language detection.
	private String[] english = {"that", "have", "with", "this", "from", "they", "would", "the", "build", "target", "hi", "that", "me", "my", "him", "her", "we", "us"};
	// url and host map
	private Map<String, String> urlHostPair = new HashMap<String, String>();
	// time host last crawled
	private Map<String, Long> timeMap = new HashMap<String, Long>();
	// hash host to robot
	private Map<String, Robot> hostRobotMap = new HashMap<String, Robot>();
	private Map<String, Integer> pagesPerHost = new HashMap<String, Integer>();
	// extra credits to test content seen
	private URLFrontier frontier;
	private HashSet<String> uniqueContents = new HashSet<String>();
	public boolean isStop = false;

	/**
	 * constructor.
	 * @param dbDirectory - the path to the PageRank directory
	 * @param indexerDBDir - the directory of the indexer database directory
	 * @param URLFile - the path to the disk backed URL frontier (or seed file)
	 * @param maxSize  - the maximum file size
	 * @param maxNum - the maximum number of files to crawl
	 */
	public Crawler(String dbDirectory, String indexerDBDir, String URLFile, int maxSize, int maxNum)
	{
		this.dbDirectory = dbDirectory;
		this.maxSize = maxSize;
		this.maxNum = maxNum;
		this.indexerDBDir = indexerDBDir;
		db = new DatabaseWrapper(this.dbDirectory);
		indexdb = new IndexWrapper(this.indexerDBDir);
		this.URLFile = URLFile;
		frontier = new URLFrontier();
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(URLFile));
			while (reader.ready())
			{
				String url = reader.readLine();
				frontier.add(fixURL(url));
			}
			reader.close();
		}
		catch (IOException e)
		{
		}
	}

	/**
	 * Flush contents of URLFrontier to disk
	 * @return Returns the contents of the URLFrontier
	 */
	public Queue<String> getFrontier()
	{
		return frontier.getQueue();
	}

	/**
	 * Main process to start crawling
	 */
	public void startCrawling()
	{
		// count the number of file crawled
		count = 0;
		while (!frontier.isEmpty())
		{
			// Step 1. get a URL
			String currentURL = frontier.poll();
			// Step 2. send head and get response to check if document is valid
			HttpCrawlerClient client = new HttpCrawlerClient();
			client.parseURL(currentURL);
			urlHostPair.put(currentURL, client.getHost());
			client.headRequest();
			// get header response
			lastModified = client.getLastModified();
			contentLen = client.getContentLength();
			contentType = client.getContentType();
			if (contentType.contains(";"))
			{
				contentType = contentType.substring(0, contentType.indexOf(";")).trim();
			}
			if (!isValidFile(client, maxSize))
			{
				//System.out.println(currentURL + " : Invalid File");
				continue;
			}
			webDocument = db.getDocument(currentURL);
			// case 1: already crawled and last modified earlier than last crawl, should use local copy of the file
			if ((webDocument != null) && (lastModified < webDocument.getLastCrawlTime()))
			{
				System.out.println(currentURL + " : Not Modified");
				String docContent = webDocument.getDocumentContent();
				Document doc = null;
				if (contentType.equals("text/html"))
				{
					doc = client.generateHTMLDom(docContent);
				}
				else
				{
					doc = client.generateXMLDom(docContent);
				}
				// for fixing bug
				if (doc == null)
				{
					continue;
				}
				if (contentType.trim().equalsIgnoreCase("text/html"))
				{
					// html: extract and add to queue
					String host = urlHostPair.get(currentURL);
					extractAndEnqueue(currentURL, doc, host);
				}
				else if (contentType.trim().equalsIgnoreCase("text/xml") || contentType.trim().equalsIgnoreCase("application/xml")
					 || contentType.trim().endsWith("+xml"))
				{
					// xml: add to database and update channel
					updateChannel(currentURL, doc);
				}
				continue;
			}
			// case 2: not crawled yet. check if robot allowed!
			String host = client.getHost();
			// this limit can only affect standalone mode
			if ((pagesPerHost.get(host) != null) && (pagesPerHost.get(host) > 100))
			{
				continue;
			}
			boolean isValidByRobot = isPolite(client, host, currentURL);
			if (!isValidByRobot)
			{
				System.out.println(currentURL + " : Blocked by robots.txt");
				continue;
			}
			if (!timeMap.containsKey(currentURL))
			{
				timeMap.put(currentURL, 0l);
			}
			pagesPerHost.put(host, pagesPerHost.containsKey(host) ? pagesPerHost.get(host) + 1 : 1);
			// Step 3. download and store in database
			downloadAndStore(client, currentURL);
			if (count >= maxNum)
			{
				break;
			}
		}
		System.out.println("Final count: " + count + " pages crawled.");
		// close();
		// done
	}

	/**
	 * check if current url is allowed by robots.txt from host
	 * @param client - The client used to crawl this page
	 * @param host - the hostname to retrieve robots.txt from
	 * @param currentURL - The currently requested URL
	 * @return Returns true if the host allows robots to access the URL
	 */
	private boolean isPolite(HttpCrawlerClient client, String host, String currentURL)
	{
		Robot robot = hostRobotMap.get(host);
		if (robot == null)
		{
			// download and crawl robot
			robot = downloadRobotRules(currentURL);
			hostRobotMap.put(host, robot);
			// parse result is null
			if (robot == null)
			{
				return true;
			}
		}
		// already get robot, check if cis455crawler is banned
		return parseRobot(host, currentURL, robot, "cis455crawler");
	}

	/**
	 * download and parse robots.txt given url
	 * @param currentURL - the URL whose robots.txt we need
	 * @return Returns the robot specification for the page
	 */
	private Robot downloadRobotRules(String currentURL)
	{
		// create a new client to parse robots.txt
		HttpCrawlerClient robotClient = new HttpCrawlerClient();
		String hostPart;
		try
		{
			hostPart = new URL(currentURL).getHost();
			if (!hostPart.endsWith("/"))
			{
				hostPart += "/";
			}
			String robotURL = hostPart + "robots.txt";
			robotClient.parseURL(robotURL);
			return robotClient.downloadRobotRules();
		}
		catch (MalformedURLException e)
		{
			return null;
		}
	}

	/**
	 * helper function used in isPolite, check if "cis455crawler" is banned by robot
	 * @param host - the hostname whose robots.txt we need
	 * @param currentURL - the URL of the page within the host to access
	 * @param robot - the robots.txt specification
	 * @param agent - the agent described in robots.txt
	 * @return Returns true if the robots.txt allows this crawler
	 */
	private boolean parseRobot(String host, String currentURL, Robot robot, String agent)
	{
		// only check if robot contains agent which should be "cis455crawler" or "*"
		if (robot.containsAgent(agent))
		{
			List<String> allowed = robot.getAllowed(agent);
			List<String> banned = robot.getBanned(agent);
			long lastCrawlTime = robot.getDelay(agent);
			// 1. check allowed
			if (allowed != null)
			{
				for (int i = 0; i < allowed.size(); i++)
				{
					if (checkEquals(currentURL, host + allowed.get(i)))
					{
						return true;
					}
				}
			}
			// 2. check banned
			if (banned != null)
			{
				for (int i = 0; i < banned.size(); i++)
				{
					if (checkEquals(currentURL, host + banned.get(i)))
					{
						return false;
					}
				}
			}
			// 3.check delay time
			if (!checkDelayTime(robot, agent, lastCrawlTime))
			{
				// added to frontier and crawl next time
				frontier.add(currentURL);
				return false;
			}
			// not banned and satisfy delay interval requirement
			return true;
		}
		else
		{
			return true;
		}
	}

	/**
	 * check if url satisfy minimum crawl delay time interval
	 * @param robot - the robots.txt specification
	 * @param agent - the agent described in robots.txt
	 * @param lastCrawlTime - the last time we crawled this host
	 * @return Returns true if robots.txt allows the crawler to access the page based on cooldowns
	 */
	private boolean checkDelayTime(Robot robot, String agent, long lastCrawlTime)
	{
		long timeInterval = System.currentTimeMillis() - lastCrawlTime;
		long minDelay = 1000 * robot.getDelay(agent);
		if (timeInterval > minDelay)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * check if url is banned by agent * or agent "cis455crawler"
	 * @param currentURL - the URL we wish to access
	 * @param allowedURL - the specification of allowed URLs
	 * @return Returns true if the crawler is even allowed access to the page
	 */
	private boolean checkEquals(String currentURL, String allowedURL)
		{
		if (currentURL.startsWith("http"))
		{
			currentURL = currentURL.split("//")[1].trim();
		}
		if (currentURL.contains(allowedURL))
		{
			return true;
		}
		return false;
	}

	/**
	 * Send get response, download file and store in database
	 * @param client - the crawler client
	 * @param currentURL - the URL we wish to access
	 */
	private void downloadAndStore(HttpCrawlerClient client, String currentURL)
	{
		// send GET request and crawl the document
		client.sendGetRequest();
		// updated time map
		timeMap.put(client.getHost(), System.currentTimeMillis());
		// response content body
		String body = client.getBody();
		// ! extra credits: check if different URL has same content
		if (contentSeenTest(body))
		{
			return;
		}
		else
		{
			// if not, add content to uniqueContents
			uniqueContents.add(body);
		}
		Document doc = null;
		if (contentType.trim().contains("text/html"))
		{
			// html: extract and add to queue
			doc = client.generateHTMLDom(body);
			// for bug fixing
			if (doc == null)
			{
				return;
			}
			String host = client.getHost();
			// xml may not contain much english, so we assume it always is
			if (!detectEnglish(body))
			{
				return;
			}
			extractAndEnqueue(currentURL, doc, host);
		}
		else if (contentType.trim().equalsIgnoreCase("text/xml") || contentType.trim().equalsIgnoreCase("application/xml")
			 || contentType.trim().endsWith("+xml"))
		{
			// xml: add to database and update channel
			doc = client.generateXMLDom(body);
			updateChannel(currentURL, doc);
		}
		if (doc != null)
		{
			System.out.println(currentURL + ": Downloading");
			webDocument = new WebDocument(currentURL);
			long crawlTime = System.currentTimeMillis();
			webDocument.setLastCrawlTime(crawlTime);
			webDocument.setDocumentContent(body);
			String noHTML = Jsoup.parse(body).text().toLowerCase().trim();
			WebDocument contents = new WebDocument(currentURL);
			contents.setDocumentContent(noHTML);
			contents.setLastCrawlTime(crawlTime);
			indexdb.addDocument(contents);
			db.addDocument(webDocument);
			count++;
		}
		else
		{
			return;
		}
	}

	/**
	 * Simple guess if the page is English. This is by no means foolproof.
	 * @param text - text to test
	 * @return Returns true if the page is maybe English
	 */
	private boolean detectEnglish(String text)
	{
		for (String s: english)
		{
			if (text.contains(s))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * check if response status, content type, and content length is valid
	 * @param client - the Crawler client
	 * @param maxSize - the maximum size of file to crawl
	 * @return Returns true if the file is OK to download (barring robot specifications)
	 */
	private boolean isValidFile(HttpCrawlerClient client, int maxSize)
	{
		// check code
		if (client.getCode() != 200)
		{
			return false;
		}
		// check type
		if (!contentType.trim().toLowerCase().contains("text/html") && !contentType.trim().toLowerCase().equals("text/xml")
		    && !contentType.trim().toLowerCase().equals("application/xml") && !contentType.trim().toLowerCase().endsWith("+xml"))
		{
			return false;
		}
		// check length
		if (contentLen > (maxSize * 1000000))
		{
			return false;
		}
		return true;
	}

	/**
	 * Update channel, used by XML file only
	 * @param currentURL - the URL of the XML file
	 * @param doc - the document from the URL
	 */
	private void updateChannel(String currentURL, Document doc)
	{
		List<Channel> channelList = db.getChannelList();
		for (int i = 0; i < channelList.size(); i++)
		{
			Channel eachChannel = channelList.get(i);
			if (eachChannel.getMatchedURLs().contains(currentURL))
			{
				continue;
			}
		}
	}

	/**
	 * Extract all links from HTML and add to frontier, only apply on HTML file, XML file do not extract, directly download
	 * @param url - the URL of the HTML/XML file
	 * @param doc - the document tree of the file
	 * @param host - the hostname
	 */
	private void extractAndEnqueue(String url, Document doc, String host)
	{
		NodeList nl = doc.getElementsByTagName("a");
		for (int i = 0; i < nl.getLength(); i++)
		{
			Element element = (Element) nl.item(i);
			Node linkNode = element.getAttributeNode("href");
			if (linkNode != null)
			{
				String extractedLink = uniformURL(host, linkNode.getNodeValue().trim() + "", url);
				if ((extractedLink != null) && (extractedLink.length() > 0))
				{
					if (extractedLink.charAt(extractedLink.length() - 1) != '/')
					{
						extractedLink = extractedLink + "/";
					}
					frontier.add(extractedLink);
				}
			}
		}
	}

	/**
	 * uniform URL to good format
	 * @param hostName - the host of the URL
	 * @param relativePath - the path from the host we wish to access
	 * @param url - the URL we wish to access
	 * @return Returns the corrected URL for crawling
	 */
	private String uniformURL(String hostName, String relativePath, String url)
	{
		// case 1: relative path already absolute
		if (relativePath.startsWith("www") || relativePath.startsWith("http"))
		{
			return relativePath;
		}
		// remove first slash in relative path
		if (relativePath.startsWith("/"))
		{
			relativePath = relativePath.substring(1);
		}
		// case 2: end with /
		if (relativePath.endsWith("/"))
		{
			return "http://" + hostName + "/" + relativePath;
		}
		else
		{
			// case 3: not end with /
			String firstPartURL = url;
			if (url.endsWith("xml") || url.endsWith("html"))
			{
				firstPartURL = url.substring(0, url.lastIndexOf("/"));
			}
			if (!firstPartURL.endsWith("/"))
			{
				firstPartURL = "/" + firstPartURL;
			}
			String wholeURL = firstPartURL + relativePath;
			// if not end with xml or html, append / if needed
			return wholeURL;
		}
	}

	/**
	 * Extra credits, check if a different URL has same content, if yes, don't download again
	 * @param content - the String file contents
	 * @return Returns true if we have already seen this content
	 */
	public boolean contentSeenTest(String content)
	{
		if (uniqueContents.contains(content))
		{
			count++;
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * check type first, if not xml or html file, append / at the end if needed
	 * @param url - the URL to add '/' to the end of
	 * @return Returns the correctly formatted URL
	 */
	private static String fixURL(String url)
	{
		if (!url.endsWith(".xml") && !url.endsWith(".html"))
		{
			if (!url.endsWith("/"))
			{
				url = url + "/";
			}
		}
		return url;
	}

	/**
	 * Close the databases
	 */
	public void close()
	{
		db.close();
		indexdb.close();
	}

	/**
	 * main method to start
	 * @param args
	 */
	public static void main(String args[])
	{
		if (args.length < 4)
		{
			System.out.println("Should have at least five arguments: <db root> <index db root> <url frontier path>" +
					   " <file max size> [num of files]");
			return;
		}
		String dbDirectory = args[0];
		String indexDBDir = args[1];
		String URLPath = args[2];
		int maxSize = Integer.parseInt(args[3]);
		int maxNum = Integer.MAX_VALUE;
		if (args.length == 5)
		{
			maxNum = Integer.parseInt(args[4]);
		}
		Crawler crawler = new Crawler(dbDirectory, indexDBDir, URLPath, maxSize, maxNum);
		crawler.startCrawling();
	}
}
