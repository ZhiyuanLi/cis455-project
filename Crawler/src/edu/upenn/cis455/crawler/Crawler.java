package edu.upenn.cis455.crawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.upenn.cis455.storage.*;

/**
 * Crawler for CIS 555 Final Project
 * 
 * @author weisong
 *
 */
public class Crawler {
	// four input arguments
	private String urlStart = "";
	private String dbDirectory = "";
	private int maxSize = Integer.MAX_VALUE;
	private int maxNum = Integer.MAX_VALUE;

	// header response
	private String contentType;
	private int contentLen;
	private long lastMofidied;

	public int count;
	private DatabaseWrapper db;
	private WebDocument webDocument;
	// url and host map
	private Map<String, String> urlHostPair = new HashMap<String, String>();
	// time host last crawled
	private Map<String, Long> timeMap = new HashMap<String, Long>();
	// hash host to robot
	private Map<String, Robot> hostRobotMap = new HashMap<String, Robot>();;

	// extra credits to test content seen
	private URLFrontier frontier;
	private HashSet<String> uniqueContents = new HashSet<String>();
	public boolean isStop = false;

	/**
	 * constructor
	 * 
	 * @param urlStart
	 * @param dbDirectory
	 * @param maxSize
	 * @param maxNum
	 */
	public Crawler(String urlStart, String dbDirectory, int maxSize, int maxNum) {
		this.urlStart = urlStart;
		this.dbDirectory = dbDirectory;
		this.maxSize = maxSize;
		this.maxNum = maxNum;
		// db = DatabaseWrapper.getSingletonDatabase(dbDirectory);
		db = new DatabaseWrapper(this.dbDirectory);
		// System.out.println("database document list size is " +
		// db.getDocumentList().size());
		// System.out.println("database channel list size is " +
		// db.getChannelList().size());
		frontier = new URLFrontier();
		frontier.add(this.urlStart);
	}

	/**
	 * Main process to start crawling
	 */
	public void startCrawling() {
		// count the number of file crawled
		count = 0;
		while (!frontier.isEmpty()) {
			// Step 1. get a URL
			String currentURL = frontier.poll();
//			 System.out.println("frontier URLURL is " + currentURL);
			// Step 2. send head and get response to check if document is valid
			HttpCrawlerClient client = new HttpCrawlerClient();
			client.parseURL(currentURL);
			urlHostPair.put(currentURL, client.getHost());
			client.headRequest();
			// get header response
			lastMofidied = client.getLastModified();
			contentLen = client.getContentLength();
			contentType = client.getContentType();
			if (!isValidFile(client, maxSize)) {
				// System.out.println("File Not Valid");
				continue;
			}
			webDocument = db.getDocument(currentURL);
			// case 1: already crawled and last modified earlier than last
			// crawl, should use local copy of the file
			if (webDocument != null && lastMofidied < webDocument.getLastCrawlTime()) {
				System.out.println(currentURL + " : Not Modified");
				String docContent = webDocument.getDocumentContent();
				Document doc = null;
				if (contentType.equals("text/html")) {
					// System.out.println("is html");
					doc = client.generateHTMLDom(docContent);
				}
				// for fixing bug
				if (doc == null) {
					// System.out.println("@@@@@@");
					continue;
				}

				if (contentType.trim().equalsIgnoreCase("text/html")) {
					// html: extract and add to queue
					String host = urlHostPair.get(currentURL);
					extractAndEnqueue(currentURL, doc, host);
				}
				continue;
			}
			// case 2: not crawled yet
			// check if robot allowed!
			String host = client.getHost();
			boolean isValidByRobot = isPolite(client, host, currentURL);
			// System.out.println(isValidByRobot);
			if (!isValidByRobot) {
				continue;
			}
			if (!timeMap.containsKey(currentURL)) {
				timeMap.put(currentURL, 0l);
			}
			// Step 3. download and store in database
			downloadAndStore(client, currentURL);
			if (count >= maxNum)
				break;
		}
		// System.out.println("Count is: " + count);
		db.close();
		// done
	}

	// ******** robot ************

	/**
	 * check if current url is allowed by robots.txt from host
	 * 
	 * @param client
	 * @param host
	 * @param currentURL
	 * @return
	 */
	private boolean isPolite(HttpCrawlerClient client, String host, String currentURL) {
		Robot robot = hostRobotMap.get(host);
		if (robot == null) {
			// download and crawl robot
			robot = downloadRobotRules(currentURL);
			hostRobotMap.put(host, robot);
			// parse result is null
			if (robot == null) {
				return true;
			}
		}
		// already get robot, check if cis455crawler is banned
		// System.out.println(parseRobot(host, currentURL, robot,
		// "cis455crawler"));
		return parseRobot(host, currentURL, robot, "*") && parseRobot(host, currentURL, robot, "cis455crawler");
	}

	/**
	 * download and parse robots.txt given url
	 * 
	 * @param currentURL
	 * @return
	 */
	private Robot downloadRobotRules(String currentURL) {
		// create a new client to parse robots.txt
		HttpCrawlerClient robotClient = new HttpCrawlerClient();
		String hostPart;
		try {
			hostPart = new URL(currentURL).getHost();
			if (!hostPart.endsWith("/"))
				hostPart += "/";
			String robotURL = hostPart + "robots.txt";
			robotClient.parseURL(robotURL);
			return robotClient.downloadRobotRules();
		} catch (MalformedURLException e) {
			return null;
		}
	}

	/**
	 * helper function used in isPolite, check if "cis455crawler" is banned by
	 * robot
	 * 
	 * @param host
	 * @param currentURL
	 * @param robot
	 * @param string
	 * @return
	 */
	private boolean parseRobot(String host, String currentURL, Robot robot, String agent) {
		// only check if robot contains agent which should be "cis455crawler" or
		// "*"
		if (robot.containsAgent(agent)) {
			List<String> allowed = robot.getAllowed(agent);
			List<String> banned = robot.getBanned(agent);
			long lastCrawlTime = robot.getDelay(agent);
			// 1. check allowed
			if (allowed != null) {
				for (int i = 0; i < allowed.size(); i++) {
					if (checkEquals(currentURL, host + allowed.get(i))) {
						return true;
					}
				}
			}
			// 2. check banned
			if (banned != null) {
				for (int i = 0; i < banned.size(); i++) {
					// System.out.println("currentURL: " + currentURL);
					// System.out.println("banned: " + host + banned.get(i));
					if (checkEquals(currentURL, host + banned.get(i))) {
						// System.out.println("Banned");
						return false;
					}
				}
			}
			// 3.check delay time
			if (!checkDelayTime(robot, agent, lastCrawlTime)) {
				// added to frontier and crawl next time
				frontier.add(currentURL);
				return false;
			}
			// not banned and satisfy delay interval requirement
			return true;
		} else {
			return true;
		}
	}

	/**
	 * check if url satisfy minimum crawl delay time interval
	 * 
	 * @param robot
	 * @param agent
	 * @param lastCrawlTime
	 * @return
	 */
	private boolean checkDelayTime(Robot robot, String agent, long lastCrawlTime) {
		long timeInterval = System.currentTimeMillis() - lastCrawlTime;
		long minDelay = 1000 * robot.getDelay(agent);
		if (timeInterval > minDelay)
			return true;
		else
			return false;
	}

	/**
	 * check if url is banned by agent * or agent "cis455crawler"
	 * 
	 * @param currentURL
	 * @param allowedURL
	 * @return
	 */
	private boolean checkEquals(String currentURL, String allowedURL) {
		if (currentURL.startsWith("http")) {
			currentURL = currentURL.split("//")[1].trim();
		}
		if (currentURL.contains(allowedURL)) {
			return true;
		}
		return false;
	}

	// ********** download and store **************
	/**
	 * Send get response, download file and store in database
	 * 
	 * @param client
	 * @param currentURL
	 */
	private void downloadAndStore(HttpCrawlerClient client, String currentURL) {
		// send GET request and crawl the document
		client.sendGetRequest();
		// updated time map
		timeMap.put(client.getHost(), System.currentTimeMillis());
		// response content body
		String body = client.getBody();
		// ! extra credits: check if different URL has same content
		if (contentSeenTest(body)) {
			count++;
			return;
		} else {
			// if not, add content to uniqueContents
			uniqueContents.add(body);
		}
		Document doc = null;
		
		if (contentType.trim().equalsIgnoreCase("text/html")) {
			// html: extract and add to queue
			doc = client.generateHTMLDom(body);
			// for bug fixing
			if (doc == null) {
				return;
			}
			String host = client.getHost();
			extractAndEnqueue(currentURL, doc, host);
		}
		if (doc != null) {
			System.out.println(currentURL + " : Downloading");
			webDocument = new WebDocument(currentURL);
			webDocument.setLastCrawlTime(System.currentTimeMillis());
			webDocument.setDocumentContent(body);
			db.addDocument(webDocument);
			count++;
		} else {
			return;
		}
	}

	/**
	 * check if response status, content type, and content length is valid
	 * 
	 * @param client
	 * @param maxSize
	 * @return
	 */
	private boolean isValidFile(HttpCrawlerClient client, int maxSize) {
		// check code
		if (client.getCode() != 200) {
			return false;
		}
		// check type
		if (!contentType.trim().toLowerCase().equals("text/html")) {
			return false;
		}
		// check length
		if (contentLen > maxSize * 1000000) {
			return false;
		}
		return true;
	}

	/**
	 * Extract all links from HTML and add to frontier, only apply on HTML file,
	 * 
	 * @param url
	 * @param doc
	 * @param host
	 */
	private void extractAndEnqueue(String url, Document doc, String host) {
		NodeList nl = doc.getElementsByTagName("a");
		for (int i = 0; i < nl.getLength(); i++) {
			Element element = (Element) nl.item(i);
			Node linkNode = element.getAttributeNode("href");
			if (linkNode != null) {
				String extractedLink = uniformURL(host, linkNode.getNodeValue().trim() + "", url);
				if (extractedLink != null && extractedLink.length() > 0) {
//					System.out.println("link is " + extractedLink);
					frontier.add(extractedLink);
				}
			}
		}
	}

	/**
	 * uniform URL to good format
	 * 
	 * @param hostName
	 * @param relativePath
	 * @param url
	 * @return
	 */
	private String uniformURL(String hostName, String relativePath, String url) {
		// case 1: relative path already absolute
		if (relativePath.startsWith("www") || relativePath.startsWith("http")) {
			return relativePath;
		}
		// remove first slash in relative path
		if (relativePath.startsWith("/")) {
			relativePath.substring(1);
		}
		// case 2: end with /
		if (relativePath.endsWith("/")) {
			return "http://" + hostName + "/" + relativePath;
		} else {
			// case 3: not end with /
			String firstPartURL = url;
			// System.out.println("url is " + url);
			if (url.endsWith("html")) {
				firstPartURL = url.substring(0, url.lastIndexOf("/"));
			}
			// System.out.println("first part is " + firstPartURL);
			if (!firstPartURL.endsWith("/")) {
				firstPartURL = "/" + firstPartURL;
			}
			String wholeURL = firstPartURL + relativePath;
			// if not end with html, append / if needed
			wholeURL = fixURL(wholeURL);
			return wholeURL;
		}
	}

	/**
	 * Extra credits, check if a different URL has same content, if yes, don't
	 * download again
	 * 
	 * @param content
	 * @return
	 */
	public boolean contentSeenTest(String content) {
		if (uniqueContents.contains(content)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * check type first, if not html file, append / at the end if needed
	 * 
	 * @param url
	 * @return
	 */
	private static String fixURL(String url) {
		if (!url.endsWith(".html")) {
			if (!url.endsWith("/")) {
				url = url + "/";
			}
		}
		return url;
	}

	// ************ main **************
	/**
	 * main method to start
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		if (args.length < 3) {
			System.out.println(
					"Should have at least three arguments: <start URL> <db root> <file max size> [num of files]");
		}
		String urlStart = args[0];
		urlStart = fixURL(urlStart);
		// System.out.println("start: "+urlStart);;
		String dbDirectory = args[1];
		int maxSize = Integer.parseInt(args[2]);
		int maxNum = Integer.MAX_VALUE;
		if (args.length == 4) {
			maxNum = Integer.parseInt(args[3]);
		}
		Crawler crawler = new Crawler(urlStart, dbDirectory, maxSize, maxNum);
		crawler.startCrawling();
	}

}
