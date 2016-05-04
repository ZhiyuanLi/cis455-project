// Crawler worker for a miltithreaded crawler
// Authors: All
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
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import org.jsoup.Jsoup;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.Thread;
import java.util.concurrent.locks.ReentrantLock;
import com.planetj.math.rabinhash.RabinHashFunction32;
public class CrawlerThreaded
{
	private int count = 0;
	private int numFiles = 1000000;
	private ReentrantLock countLock = new ReentrantLock();
	private Map<String, Long> timeMap = new HashMap<String, Long>();
	private ReentrantLock timeLock = new ReentrantLock();
	private Map<String, Robot> hostRobotMap = new HashMap<String, Robot>();
	private ReentrantLock hostRobotLock = new ReentrantLock();
	private HashSet<Integer> contentHashes = new HashSet<Integer>();
	private ReentrantLock contentHashLock = new ReentrantLock();
	private RabinHashFunction32 hash = RabinHashFunction32.DEFAULT_HASH_FUNCTION;
	private URLFrontier frontier = new URLFrontier();
	private ReentrantLock frontierLock = new ReentrantLock();
	private HashSet<Integer> URLHashes = new HashSet<Integer>();
	private ReentrantLock URLHashLock = new ReentrantLock();
	private Thread[] workers = null;
	private String[] english = {"that", "have", "which", "this", "from", "they", "would", "the", "build", "target", "hi", "that", "me", "my", "him", "her", "we", "us"};
	/**
	 * Create a new crawler master
	 * @param numWorkers - the number of workers to spawn
	 * @param numFiles - the number of files to crawl
	 * @param inFile - the input directory
	 * @param linksFile - the PageRank input directory
	 * @param titleFile - the indexer titles directory
	 * @param indexFile - the indexer bodies directory
	 * @param imageFile - the indexer images directory
	 */
	public CrawlerThreaded(int numWorkers, int numFiles, String inFile, String linksFile, String titleFile, String indexFile, String imageFile)
	{
		try
		{
			this.numFiles = numFiles;
			workers = new Thread[numWorkers];
			BufferedReader reader = new BufferedReader(new FileReader(inFile));
			while (reader.ready())
			{
				String url = reader.readLine();
				frontier.add(url);
			}
			reader.close();
			for (int i = 0; i < numWorkers; i++)
			{
				workers[i] = new CrawlerWorker(linksFile + i + ".txt", titleFile + i + ".txt", indexFile + i + ".txt", imageFile + i + ".txt");
			}
			for (int i = 0; i < numWorkers; i++)
			{
				workers[i].start();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Return the current contents of the fronter
	 * @return Returns the frontier
	 */
	private URLFrontier getFrontier()
	{
		return frontier;
	}

	/**
	 * Run the crawler
	 * @param args - the command line arguments
	 */
	public static void main(String[] args)
	{
		if (args.length != 7)
		{
			System.out.println("Usage: CrawlerThreaded <numWorkers> <numFiles> <inFile> <linksFile> <titleFile> <indexFile> <imageFile>");
			return;
		}
		CrawlerThreaded center = new CrawlerThreaded(Integer.parseInt(args[0]), Integer.parseInt(args[1]), args[2], args[3], args[4], args[5], args[6]);
	}

	private class CrawlerWorker extends Thread
	{
		private String contentType;
		private int contentLen;
		private long lastModified;
		private boolean secure = false;
		private PrintWriter linksWriter;
		private PrintWriter imageWriter;
		private PrintWriter indexWriter;
		private PrintWriter titleWriter;
		private String linksBuffer = "";
		private String imageBuffer = "";
		private String indexBuffer = "";
		private String titleBuffer = "";
		private int linkBufferCount = 0;
		private int indexBufferCount = 0;
		private int imageBufferCount = 0;
		private int titleBufferCount = 0;
		/**
		 * constructor.
		 * @param linksFile - the PageRank input file
		 * @param titleFile - the indexer title file
		 * @param indexFile - the indexer body file
		 * @param imageFile - the indexer image file
	 	 */
		public CrawlerWorker(String linksFile, String titleFile, String indexFile, String imageFile)
		{
			try
			{
				linksWriter = new PrintWriter(new BufferedWriter(new FileWriter(linksFile, true)));
				titleWriter = new PrintWriter(new BufferedWriter(new FileWriter(titleFile, true)));
				indexWriter = new PrintWriter(new BufferedWriter(new FileWriter(indexFile, true)));
				imageWriter = new PrintWriter(new BufferedWriter(new FileWriter(imageFile, true)));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		/**
		 * Write to the links file
		 * @param line - the line to write
		 */
		public void writeLinks(String line)
		{
			if (linkBufferCount < 100)
			{
				linkBufferCount++;
				linksBuffer = linksBuffer + line + "\n";
				return;
			}
			try
			{
				linksWriter.print(linksBuffer);
				linksWriter.flush();
				linksBuffer = "";
				linkBufferCount = 0;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		/**
		 * Write to the image file
		 * @param line - the line to write
		 */
		public void writeImage(String line)
		{
			if (imageBufferCount < 100)
			{
				imageBufferCount++;
				imageBuffer = imageBuffer + line + "\n";
				return;
			}
			try
			{
				imageWriter.print(imageBuffer);
				imageWriter.flush();
				imageBuffer = "";
				imageBufferCount = 0;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		/**
		 * Write to the title file
		 * @param line - the line to write
		 */
		public void writeTitle(String line)
		{
			if (titleBufferCount < 100)
			{
				titleBufferCount++;
				titleBuffer = titleBuffer + line + "\n";
				return;
			}
			try
			{
				titleWriter.print(titleBuffer);
				titleWriter.flush();
				titleBuffer = "";
				titleBufferCount = 0;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		/**
		 * Write to the index file
		 * @param line - the line to write
		 */
		public void writeIndex(String line)
		{
			if (indexBufferCount < 100)
			{
				indexBufferCount++;
				indexBuffer = indexBuffer + line + "\n";
				return;
			}
			try
			{
				indexWriter.print(indexBuffer);
				indexWriter.flush();
				indexBuffer = "";
				indexBufferCount = 0;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		/**
		 * Close the writers and flush buffers
		 */
		public void close()
		{
			if (titleBuffer.length() > 0)
			{
				titleWriter.print(titleBuffer);
				titleWriter.flush();
			}
			titleWriter.close();
			if (indexBuffer.length() > 0)
			{
				indexWriter.print(indexBuffer);
				indexWriter.flush();
			}
			indexWriter.close();
			if (imageBuffer.length() > 0)
			{
				imageWriter.print(imageBuffer);
				imageWriter.flush();
			}
			imageWriter.close();
			if (linksBuffer.length() > 0)
			{
				linksWriter.print(linksBuffer);
				linksWriter.flush();
			}
			linksWriter.close();
		}

		/**
		 * Add http to the beginning of the url
		 * @param url - the url to prepend
		 * @return Returns the prepended url
		 */
		public String prependURL(String url)
		{
			if (url.startsWith("//"))
			{
				return "http" + url;
			}
			else if (!url.startsWith("http"))
			{
				return "http://" + url;
			}
			else
			{
				return url;
			}
		}

		/**
		 * Main process to start crawling
		 */
		public void run()
		{
			// count the number of file crawled
			while (!frontier.isEmpty())
			{
				String currentURL;
				// Step 1. get a URL
				try
				{
					frontierLock.lock();
					currentURL = frontier.poll();
				}
				finally
				{
					frontierLock.unlock();
				}
				// Step 2. send head and get response to check if document is valid
				HttpCrawlerClient client = new HttpCrawlerClient();
				try
				{
					client.parseURL(currentURL);
				}
				catch (Exception e)
				{
					continue;
				}
				secure = client.isSecure();
				client.headRequest();
				// get header response
				lastModified = client.getLastModified();
				contentLen = client.getContentLength();
				contentType = client.getContentType();
				if (contentType.contains(";"))
				{
					contentType = contentType.substring(0, contentType.indexOf(";")).trim();
				}
				// Partly guard against spider traps not flagged in robots.txt by limiting URL length to 200 characters
				if (URLHashes.contains(hash.hash(currentURL)) || (currentURL.length() > 200) || !isValidFile(client, 700))
				{
					continue;
				}
				try
				{
					URLHashLock.lock();
					URLHashes.add(hash.hash(currentURL));
				}
				finally
				{
					URLHashLock.unlock();
				}
				String host = client.getHost();
				// case 2: not crawled yet. check if robot allowed!
				boolean isValidByRobot = isPolite(client, host, client.getPort(), currentURL);
				if (!isValidByRobot)
				{
					continue;
				}
				if (!timeMap.containsKey(currentURL))
				{
					try
					{
						timeLock.lock();
						timeMap.put(currentURL, 0l);
					}
					finally
					{
						timeLock.unlock();
					}
				}
				// Step 3. download and store in database
				downloadAndStore(client, currentURL);
				if (count >= numFiles)
				{
					break;
				}
			}
			close();
		}

		/**
		 * check if current url is allowed by robots.txt from host
		 * @param client - The client used to crawl this page
		 * @param host - the hostname to retrieve robots.txt from
		 * @param currentURL - The currently requested URL
		 * @return Returns true if the host allows robots to access the URL
		 */
		private boolean isPolite(HttpCrawlerClient client, String host, int port, String currentURL)
		{
			Robot robot = hostRobotMap.get(host);
			if (robot == null)
			{
				// download and crawl robot
				robot = downloadRobotRules(currentURL);
				try
				{
					hostRobotLock.lock();
					hostRobotMap.put(host, robot);
				}
				finally
				{
					hostRobotLock.unlock();
				}
				// parse result is null
				if (robot == null)
				{
					return true;
				}
			}
			// already get robot, check if cis455crawler is banned
			return parseRobot(host, port, currentURL, robot, "cis455crawler");
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
		private boolean parseRobot(String host, int port, String currentURL, Robot robot, String agent)
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
						if (checkEquals(currentURL, host + ":" + port + allowed.get(i)))
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
						if (checkEquals(currentURL, host + ":" + port + banned.get(i)))
						{
							return false;
						}
					}
				}
				// 3. check delay time
				if (!checkDelayTime(robot, agent, lastCrawlTime))
				{
					// added to frontier and crawl next time
					try
					{
						frontierLock.lock();
						frontier.add(prependURL(currentURL));
					}
					finally
					{
						frontierLock.unlock();
					}
					return false;
				}
			}
			// not banned and satisfies delay interval requirement
			return true;
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
			try
			{
				timeLock.lock();
				timeMap.put(client.getHost(), System.currentTimeMillis());
			}
			finally
			{
				timeLock.unlock();
			}
			// response content body
			String body = client.getBody();
			if ((body == null) || body.equals(""))
			{
				return;
			}
			int hashValue = hash.hash(body);
			// simple content seen test
			if (contentHashes.contains(hashValue))
			{
				return;
			}
			try
			{
				contentHashLock.lock();
				contentHashes.add(hashValue);
			}
			finally
			{
				contentHashLock.unlock();
			}
			Document doc = null;
			if (contentType.trim().contains("text/html"))
			{
				// html: extract and add to queue
				doc = client.generateHTMLDom(body);
				// for bug fixing
				if ((doc == null) || !detectEnglish(body))
				{
					return;
				}
				String host = client.getHost();
				extractAndEnqueue(currentURL, doc, host);
			}
			if (doc != null)
			{
				String title = client.getHost();
				try
				{
					title = Jsoup.parse(body).select("title").first().text();
				}
				catch (NullPointerException e)
				{
				}
				catch (Exception e)
				{
					return;
				}
				if ((count % 1000) == 0)
				{
					System.out.println(count + " pages crawled.");
				}
				writeTitle(currentURL + "\t" + title);
				long crawlTime = System.currentTimeMillis();
				String noHTML = Jsoup.parse(body).text().toLowerCase().trim();
				writeIndex(currentURL + "\t" + noHTML);
				extractImageURLs(currentURL, doc, client.getHost(), crawlTime, title);
				try
				{
					countLock.lock();
					count++;
				}
				finally
				{
					countLock.unlock();
				}
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
		 * @return Returns true if the file is OK to download (provided the robot does not block the crawler)
	 	*/
		private boolean isValidFile(HttpCrawlerClient client, int maxSize)
		{
			// check code
			if ((client.getCode() == 301) || (client.getCode() == 302))
			{
				String location = client.getRedirectURL().trim();
				if ((location != null) && !location.equals(client.getURL().trim()))
				{
					try
					{
						frontierLock.lock();
						frontier.add(prependURL(location));
					}
					finally
					{
						frontierLock.unlock();
					}
				}
				return false;
			}
			else if (client.getCode() != 200)
			{
				return false;
			}
			// check type
			if (!contentType.trim().toLowerCase().contains("text/html"))
			{
				return false;
			}
			// check length
			if (contentLen > 700000)
			{
				return false;
			}
			return true;
		}

		/**
		 * Extract all links from HTML and add to frontier, only apply on HTML file
		 * @param url - the URL of the HTML file
		 * @param doc - the document tree of the file
		 * @param host - the hostname
		 */
		private void extractAndEnqueue(String url, Document doc, String host)
		{
			NodeList nl = doc.getElementsByTagName("a");
			String line = url + "\t";
			boolean found = false;
			for (int i = 0; i < nl.getLength(); i++)
			{
				Element element = (Element) nl.item(i);
				Node linkNode = element.getAttributeNode("href");
				if (linkNode != null)
				{
					String extractedLink = uniformURL(host, linkNode.getNodeValue().trim() + "", url);
					if ((extractedLink != null) && (extractedLink.length() > 0))
					{
						if (extractedLink.startsWith("/") && !extractedLink.startsWith("//"))
						{
							extractedLink = url + extractedLink;
						}
						if (extractedLink.endsWith("xml"))
						{
							continue;
						}
						if (!extractedLink.endsWith("html") && (extractedLink.charAt(extractedLink.length() - 1) != '/'))
						{
							extractedLink = extractedLink + "/";
						}
						found = true;
						line = line + extractedLink + " ";
						try
						{
							frontierLock.lock();
							frontier.add(prependURL(extractedLink));
						}
						finally
						{
							frontierLock.unlock();
						}
					}
				}
			}
			if (found)
			{
				writeLinks(line.trim());
			}
		}

		/**
		 * Extract image URLs
		 * @param url - the URL of the HTML file
		 * @param doc - the document tree of the file
		 * @param host - the hostname
		 * @param crawlTime - the time the page was crawled
		 * @param title - the title of the page
		 */
		private void extractImageURLs(String url, Document doc, String host, long crawlTime, String title)
		{
			NodeList nl = doc.getElementsByTagName("img");
			String images = url + "\t";
			boolean write = false;
			HashSet<String> seen = new HashSet<String>();
			for (int i = 0; i < nl.getLength(); i++)
			{
				Element element = (Element) nl.item(i);
				Node linkNode = element.getAttributeNode("src");
				if (linkNode != null)
				{
					HttpURL normURL;
					// Only output to DB if there is at least 1 image linked
					try
					{
						normURL = new HttpURL(linkNode.getNodeValue().trim(), linkNode.getNodeValue().trim().startsWith("https"));
					}
					catch (Exception e)
					{
						continue;
					}
					String extractedLink = normURL.getNormalizeURL(linkNode.getNodeValue().trim().startsWith("https"));
					if ((extractedLink.length() > 0) && (extractedLink.endsWith(".png") || extractedLink.endsWith(".gif") || extractedLink.endsWith(".jpg") || extractedLink.endsWith(".jpeg")))
					{
						if (!seen.contains(extractedLink))
						{
							images = images + " " + extractedLink;
							write = true;
						}
						seen.add(extractedLink);
					}
				}
			}
			if (write)
			{
				writeImage(images);
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
			// Tolerate links missing the http: prefix
			else if (relativePath.startsWith("//"))
			{
				String protocol = secure ? "https" : "http";
				return relativePath.endsWith("/") ? protocol + relativePath : protocol + relativePath + "/";
			}
			// remove first slash in relative path
			else if (relativePath.startsWith("/"))
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
				if (url.endsWith("html"))
				{
					firstPartURL = url.substring(0, url.lastIndexOf("/"));
				}
				if (!firstPartURL.endsWith("/"))
				{
					firstPartURL = "/" + firstPartURL;
				}
				// if URL does not end with html, append /
				return firstPartURL + relativePath;
			}
		}

		/**
		 * Check type first, if not an  html file, append / at the end if needed
		 * @param url - the URL to add '/' to the end of
		 * @return Returns the correctly formatted URL
		 */
		private String fixURL(String url)
		{
			if (!url.endsWith(".html"))
			{
				if (!url.endsWith("/"))
				{
					url = url + "/";
				}
			}
			return url;
		}
	}
}
