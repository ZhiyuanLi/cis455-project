package edu.upenn.cis455.servlet;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.nio.file.*;

import javax.servlet.http.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;

import edu.upenn.cis455.xpathengine.*;

@SuppressWarnings("serial")
/**
 * xpath engine servlet and Http Client to establish connect and get documet;
 * include do get method to get user inputs and do post method to show results
 * 
 * @author weisong
 *
 */
public class XPathServlet extends HttpServlet {

	private String localPath = "";
	// global variables
	String path = "";
	String host = "";
	int portNum = 80;

	/**
	 * initial display at root path for user to give xpath and URL
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {
			PrintWriter out = response.getWriter();
			out.println("<!DOCTYPE html><html>");
			out.println("<h2>Wei Song</h2><body>");
			out.println("<h3>Penn Key: weisong</h3>");
			out.println("<form method = \"post\">");
			out.println("XPath: <input type = \"text\" name = \"xpath\">");
			out.println("<br><br>");
			out.println("URL: <input type = \"text\" name = \"url\">");
			out.println("<br><br><br>");
			out.println("<button type= \"submit\">submit</button>");
			out.println("</form></body></html>");
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * using xpaths and URL given by user to connect a socket. Then try to
	 * download URL content and convert into a Document using tidy parser.
	 * Search each xpath using xpath enginer and display each result on web
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			XPathEngine xengine = new XPathEngineImpl();
			String xpath = request.getParameter("xpath");
			String url = request.getParameter("url");
			// IMPORTANT STEP!
			Document d = convertUrlToDom(url);
			if (d == null) {
				out.println(
						"<!DOCTYPE html><html><h2>Wei Song's Xpath Engine</h2><body>Cannot Access Docuemnt from URL: "
								+ url + "</body></html>");
				return;
			}
			String[] xpathSet = xpath.split(";");
			xengine.setXPaths(xpathSet);// enable white spaces
			boolean[] eval = xengine.evaluate(d);
			generateHTMLResponse(out, url, xpathSet, eval);
			out.close();
		} catch (Exception e) {
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			out.println(
					"<!DOCTYPE html><html><h2>Wei Song's Xpath Engine</h2><body>Oops... something went wrong!</body></html>");
			e.printStackTrace();
			out.close();
		}
		
	}

	public Document convertUrlToDom(String urlString) throws Exception {
		Document d = null;
		// make urlString full
		if (!urlString.contains("http://")) {
			urlString = "http://" + urlString;
		}
		boolean isLocal = checkLocal(urlString);
		if (isLocal) {
			// local file
			// System.out.println("@@ Local File @@");
			if (urlString.endsWith(".xml")) {
				// read local xml
				d = generateLocalXMLDom(localPath);
			} else {
				// read local html
				StringBuilder sb = new StringBuilder();
				FileReader fr = new FileReader(localPath);
				BufferedReader br = new BufferedReader(fr);
				String nextLine = "";

				while ((nextLine = br.readLine()) != null) {
					// System.out.println(nextLine);
					sb.append(nextLine);
				}
				br.close();

				d = generateHTMLDom(sb.toString());
			}
		} else {
			// remote file
			// System.out.println("@@ Not Local @@");
			String content = getRemoteContent(urlString);
			if (content == null) {
				return null;
			}
			if (urlString.endsWith(".xml")) {
				d = generateRemoteXMLDom(urlString);
			} else {
				d = generateHTMLDom(content);
			}
		}
		return d;
	}

	/**
	 * establish connect to url host and get document content
	 * 
	 * @param urlString
	 * @return
	 */
	private String getRemoteContent(String urlString) {
		try {
			StringBuilder sb = new StringBuilder();

			// IMPORTANT parse URL
			HttpClientParse(urlString);
			Socket socket1 = new Socket(host, portNum);

			PrintWriter out = new PrintWriter(socket1.getOutputStream(), true);
			out.println("GET " + path + " HTTP/1.1\r\n");
			out.println("Host: " + host + ":" + portNum);
			out.println("Connection: close\r\n\r\n");

			InputStreamReader inputReader = new InputStreamReader(socket1.getInputStream());
			BufferedReader bufferReader = new BufferedReader(inputReader);
			int emptyLineNum = 0;
			String nextLine = bufferReader.readLine();
			while (nextLine != null) {
				// System.out.println("next line is " + nextLine);
				if (emptyLineNum > 0) {
					sb.append(nextLine); // append after first empty line
				}
				if (nextLine.equals("")) {
					emptyLineNum++; // quit after second empty line
				}
				nextLine = bufferReader.readLine();
			}
			socket1.close();
			return sb.toString();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * check if url refer to a local file
	 * 
	 * @param urlString
	 * @return
	 */
	public boolean checkLocal(String urlString) {
		if (!urlString.toLowerCase().contains("local")) {
			return false;
		}
		String exceptHttp = urlString.substring(7);
		localPath = exceptHttp.substring(exceptHttp.indexOf('/'));
		System.out.println(localPath);
		return Files.exists(Paths.get(localPath));
	}

	/**
	 * generate HTML result on web to show if each of xpath matches
	 * 
	 * @param out
	 * @param xpathSet
	 * @param result
	 */
	private void generateHTMLResponse(PrintWriter out, String url, String[] xpathSet, boolean[] result) {
		out.println("<!DOCTYPE html><html>");
		out.println("<h2>Wei Song's XPathEngine</h2><body>");
		out.print("<p>URL: " + url + "<p>");
		for (int i = 0; i < result.length; i++) {
			out.println("<li>" + xpathSet[i] + " : ");
			if (result[i]) {
				out.println("Match</li><br>");
			} else {
				out.println("Not Match</li><br>");
			}
		}
		out.write("</body></html>");
	}

	/**
	 * given string content, generate document using db factory and tidy
	 * 
	 * @param content
	 * @return
	 */
	public Document generateHTMLDom(String content) {
		// System.out.println("HTML Dom");
		Document d = null;
		try {
			Tidy tidy = new Tidy();
			// HTML true
			tidy.setXHTML(true);
			tidy.setXmlTags(false);
			tidy.setDocType("omit");
			tidy.setEncloseText(true);
			ByteArrayInputStream in = new ByteArrayInputStream(content.getBytes("UTF-8"));
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			tidy.parseDOM(in, out);

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			d = db.parse(new ByteArrayInputStream(out.toString("UTF-8").getBytes()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return d;
	}

	/**
	 * generate document from url xml file
	 * 
	 * @param urlString
	 * @return
	 */
	private Document generateRemoteXMLDom(String urlString) {
		try {
			URL urlObject = new URL(urlString);
			Document d = null;
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			d = dBuilder.parse(urlObject.openStream());
			return d;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * generate xml document of a local xml file
	 * 
	 * @param localPath
	 * @return
	 */
	private Document generateLocalXMLDom(String localPath) {
		try {
			Document d = null;
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			d = dBuilder.parse(localPath);
			return d;
		} catch (Exception e) {
			return null;
		}
	}

	// ********* Part 2 Http Client *********

	/**
	 * parse url and get host, portNum, and path
	 * 
	 * @param url
	 * @return
	 */
	private int HttpClientParse(String url) {
		if (url.contains("http://")) {
			url = url.split("//")[1].trim();
			if (url == null || url.equals(""))
				return -1;
		}
		String[] urlSplitBySlash = url.split("/");
		if (urlSplitBySlash.length < 2) {
			return -1; // for URL invalid
		}
		String hostRaw = urlSplitBySlash[0].trim();
		path = ""; // clear first
		for (int i = 1; i < urlSplitBySlash.length; i++) {
			path += "/" + urlSplitBySlash[i].trim();
		}
		if (hostRaw.contains(":")) {
			String[] hostArr = hostSplitByColon(hostRaw);
			if (hostArr.length > 2) {
				return -1; // for URL invalid
			}
			host = hostArr[0].trim();
			portNum = Integer.parseInt(hostArr[1].trim());
		} else {
			host = urlSplitBySlash[0].trim();
			portNum = 80; // default
		}
		return 1; // for OK
	}

	/**
	 * split string by colon
	 *
	 * @param hostRaw
	 * @return
	 */
	private String[] hostSplitByColon(String hostRaw) {
		return hostRaw.split(":");
	}

}
