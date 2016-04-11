package edu.upenn.cis455.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.upenn.cis455.crawler.XPathCrawler;
import edu.upenn.cis455.storage.Channel;
import edu.upenn.cis455.storage.DatabaseWrapper;
import edu.upenn.cis455.storage.WebDocument;

@SuppressWarnings("serial")
public class CrawlerServlet extends HttpServlet {
	XPathCrawler crawler;
	int htmlNum = 0;

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			String userAction = request.getParameter("userAction").trim();
			if (userAction.equals("start")) {
				startCrawler(out, request);
			} else if (userAction.equals("stop")) {
				stopCrawler(out, request);
			} else if (userAction.equals("display")) {
				displayCrawler(out, request);
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void startCrawler(PrintWriter out, HttpServletRequest request) {
		String url = request.getParameter("urlStart");
		String maxSizeRaw = request.getParameter("size");
		String maxFileNumber = request.getParameter("number");
		// error handling
		int maxSize = Integer.parseInt(maxSizeRaw);
		int maxNumber = Integer.parseInt(maxFileNumber);
		String dbPath = getServletContext().getInitParameter("BDBstore");

		// using write because crawling will take some time but the UI should
		// show immediately
		out.write("<!DOCTYPE html><html>");
		out.write("<h3>Wei Song</h3>");
		out.write("<h3>Penn Key: weisong</h3>");
		out.write("<h2>Start Crawler Page</h2>");
		out.write("<body>");
		out.write("<form method=\"get\" action=\"crawler\">");
		out.write("<input type=\"hidden\" name=\"userAction\" value=\"stop\">");
		out.write("<button type=\"submit\">Stop Crawler</button></form>");
		out.write("</body></html>");
		out.flush();
		// System.out.println("dbPath start is "+dbPath);
		crawler = new XPathCrawler(url, dbPath, maxSize, maxNumber);
		// crawler.isStop = false;
		System.out.println("Crawling Start");
		// while (!crawler.isStop) {
		crawler.startCrawling();
		htmlNum = crawler.count;
		// try {
		// // pause for setting stop
		// Thread.sleep(1000);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// }
		System.out.println("Crawling Stoped");
	}

	private void stopCrawler(PrintWriter out, HttpServletRequest request) {
		// crawler.isStop = true;
		out.println("<!DOCTYPE html><html>");
		out.println("<h3>Wei Song</h3>");
		out.println("<h3>Penn Key: weisong</h3>");
		out.println("<h2>Stop Crawler Page</h2>");
		out.println("<body>");
		out.println("<p>Crawler is stoping... Please wait for a few seconds before update</p>");
		// option back to admin manage page

		out.println("<form method = \"get\" action = \"admin\">");
		out.println("<input type=\"hidden\" name = \"userAction\" value = \"adminOptions\">");
		out.println("<input type=\"hidden\" name=\"userName\" value=\"admin\">");
		out.println("<input type=\"hidden\" name=\"password\" value=\"admin\">");
		out.println("<button type=\"submit\">Return To Admin Page</button></form>");
		out.println("</body></html>");
	}

	/**
	 * 1:the number of HTML pages scanned for links, 2:the number of XML
	 * documents retrieved, 3:the amount of data downloaded, 4:the number of
	 * servers visited, 5:the number of XML documents that match each channel,
	 * and 6:the servers with the most XML documents that match one of the
	 * channels.
	 * 
	 * @param out
	 * @param request
	 */
	private void displayCrawler(PrintWriter out, HttpServletRequest request) {
		String dbPath = getServletContext().getInitParameter("BDBstore");
		// System.out.print("dbPath is "+dbPath);
		DatabaseWrapper db = new DatabaseWrapper(dbPath);
		int DocumentNum = 0;
		long dataAmount = 0;
		Map<String, HashSet<String>> serverToXML = new HashMap<String, HashSet<String>>();
		HashSet<String> servers = new HashSet<String>();

		List<WebDocument> documentList = db.getDocumentList();
		DocumentNum = documentList.size();
		List<Channel> channelList = db.getChannelList();
		out.println("<!DOCTYPE html><html>");
		out.println("<h3>Wei Song</h3>");
		out.println("<h3>Penn Key: weisong</h3>");
		out.println("<h2>Display Database Status Page</h2>");
		out.println("<body>");
		out.println("<p>1: Number of HTML and XML documents retrieved: " + DocumentNum + "</p><br>");

		for (int i = 0; i < documentList.size(); i++) {
			WebDocument doc = documentList.get(i);
			dataAmount += doc.getDocumentContent().length();
			String docURL = doc.getURL();
			String host;
			try {
				host = new URL(docURL).getHost();
				servers.add(host);
			} catch (MalformedURLException e) {
				// e.printStackTrace();
				continue;
			}
		}
		out.println("<p>2: Amount of data downloaded: " + dataAmount + "</p><br>");
		out.println("<p>3: Number of servers visited: " + servers.size() + "</p><br>");
		out.println("<p>4: Number of XML documents that match each channel:<p>");
		for (int i = 0; i < channelList.size(); i++) {
			Channel channel = channelList.get(i);
			out.println("<li>" + channel.getCName() + " has " + channel.getMatchedURLs().size()
					+ " matched XML documents</li>");
			for (String URL : channel.getMatchedURLs()) {
				String host;
				try {
					host = new URL(URL).getHost();
					if (!serverToXML.containsKey(host)) {
						HashSet<String> set = new HashSet<String>();
						set.add(URL);
						serverToXML.put(host, set);
					} else {
						serverToXML.get(host).add(URL);
					}
				} catch (MalformedURLException e) {
					// e.printStackTrace();
					continue;
				}
			}
		}
		out.println("<br>");
		out.println("<p>5: Servers with the most XML documents that match one of the channels</p>");
		for (String server : serverToXML.keySet()) {
			out.println("<li>" + server + " : " + serverToXML.get(server).size() + "</li>");
		}
		out.println("<br>");
		
		// back to admin manage page
		out.println("<form method = \"get\" action = \"admin\">");
		out.println("<input type = \"hidden\" name = \"userAction\" value = \"adminOptions\">");
		out.println("<input type=\"hidden\" name=\"userName\" value=\"admin\">");
		out.println("<input type=\"hidden\" name=\"password\" value=\"admin\">");
		out.println("<button type = \"submit\">Back To Admin Options</button>");
		out.println("</form>");
		
		out.println("</body></html>");
		
		db.close();
	}

}
