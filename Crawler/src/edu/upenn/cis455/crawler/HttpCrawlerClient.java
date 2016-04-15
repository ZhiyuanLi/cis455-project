package edu.upenn.cis455.crawler;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;

/**
 * HTTP Crawler Client for user to fetch document using socket and parse content
 * as Document using tidy parser
 * 
 * @author weisong
 *
 */
public class HttpCrawlerClient {
	// socket argument
	private String host = "";
	private String path = "";
	private int portNum = 80;

	// header response
	private String contentType = "";
	private int contentLen = -1;
	private long lastModified = -1;

	private URL urlObject;
	private int code = -1;
	private String body = "";
	private Robot robot = null;

	/**
	 * parse url and seperate host, path and portNum, create urlObject
	 * 
	 * @param url
	 */
	public void parseURL(String url) {
		if (!url.startsWith("http")) {
			url = "http://" + url;
		}
		try {
			urlObject = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		host = urlObject.getHost();
		path = urlObject.getPath();
		portNum = urlObject.getPort() == -1 ? 80 : urlObject.getPort();
	}

	// ***** send GET and HEAD ********
	/**
	 * send HEAD request and parse HEAD response to check if file is valid
	 */
	public void headRequest() {
		try {
//			System.out.print("!!! "+host + "@@@ "+path);
			Socket socket = new Socket(host, portNum);
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			out.write("HEAD " + path + " HTTP/1.1\r\n");
			out.write("Host: " + host + ":" + portNum + "\r\n");
			out.write("User-Agent: cis455crawler\r\n");
			out.write("Connection: close\r\n\r\n");
			out.flush();

			// parse head response
			InputStreamReader inputReader = new InputStreamReader(socket.getInputStream());
			BufferedReader bufferReader = new BufferedReader(inputReader);
			String nextLine = bufferReader.readLine();
			// System.out.println("Head first line is "+ nextLine);
			// check code
			code = Integer.parseInt(nextLine.split(" ")[1]);
			// if connection not success, return
			if (code != 200) {
				socket.close();
				return;
			}

			while (nextLine != null) {
				if (nextLine.toLowerCase().contains("content-length")) {
					contentLen = Integer.parseInt(nextLine.split(":")[1].trim());
				}
				if (nextLine.toLowerCase().contains("content-type")) {
					contentType = nextLine.split(":")[1].trim();
				}
				if (nextLine.toLowerCase().contains("last-modified")) {
					String timeStr = nextLine.split(":")[1].trim();
					// System.out.println(timeStr);
					lastModified = getLastModified(timeStr);
				}
				nextLine = bufferReader.readLine();
			}
			// System.out.println("content len: " + contentLen);
			// System.out.println("content type: " + contentType);
			// System.out.println("last modified: " + lastModified);
			socket.close();
		}
		catch (IOException e) {
			code = 404;
		} catch (NumberFormatException e1) {
			code = 404;
		}
	}

	/**
	 * Send GET request
	 */
	public void sendGetRequest() {
		try {
			Socket socket = new Socket(host, portNum);
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			out.write("GET " + path + " HTTP/1.1\r\n");
			out.write("Host: " + host + ":" + portNum + "\r\n");
			out.write("User-Agent: cis455crawler\r\n");
			out.write("Connection: close\r\n\r\n");
			out.flush();
			// get response body
			body = parseGetResponse(socket.getInputStream());
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// ****** parser ********
	/**
	 * parse GET response and store content in field body
	 * 
	 * @param inputStream
	 * @return
	 */
	private String parseGetResponse(InputStream inputStream) {
		try {
			InputStreamReader inputReader = new InputStreamReader(inputStream);
			BufferedReader bufferReader = new BufferedReader(inputReader);

			int emptyLineNum = 0;
			String nextLine = bufferReader.readLine();
			StringBuilder sb = new StringBuilder();
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
			return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * From MS1: Given string content, generate document using db factory and
	 * tidy
	 * 
	 * @param content
	 * @return
	 */
	public Document generateHTMLDom(String content) {
		// System.out.println("HTML Dom");
		//TODO
		Document d = null;
		try {
			Tidy tidy = new Tidy();
			// HTML true
			tidy.setMakeClean(true);
			tidy.setXHTML(true);
			tidy.setXmlTags(false);
			tidy.setDocType("omit");
			tidy.setShowErrors(0);
			tidy.setEncloseText(true);
			tidy.setShowWarnings(false);
			tidy.setQuiet(true);
//			ByteArrayInputStream in = new ByteArrayInputStream(content.getBytes("UTF-8"));
			ByteArrayInputStream in = new ByteArrayInputStream(content.getBytes());
			ByteArrayOutputStream out = new ByteArrayOutputStream();
//			tidy.parseDOM(in, out);
			d = tidy.parseDOM(in,null);
			return d;


//			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//			DocumentBuilder db = dbf.newDocumentBuilder();
//			d = db.parse(new ByteArrayInputStream(out.toString("UTF-8").getBytes()));
		} catch (Exception e) {
			System.out.println("Parse HTML Fail");
			e.printStackTrace();
		}
		return d;
	}

	/**
	 * From MS1: Generate document from url xml file
	 * 
	 * @param urlString
	 * @return
	 */
	public Document generateXMLDom(String content) {
		Document d = null;
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			d = dBuilder.parse(new ByteArrayInputStream(content.getBytes()));
			return d;
		} catch (Exception e) {
			System.out.println("Parse XML Fail");
			return null;
		}
	}

	/**
	 * parse last modified time, given a string, return a long value
	 * 
	 * @param timeString
	 * @return
	 */
	private long getLastModified(String timeString) {
		SimpleDateFormat simpleDateFormat = null;
		Date d = null;
		if (timeString.charAt(6) == ',') {
			simpleDateFormat = new SimpleDateFormat("EEEEEE, dd-MMM-yy HH:mm:ss z");
		} else if (timeString.charAt(3) == ' ') {
			simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy");
		} else {
			simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
		}
		try {
			d = simpleDateFormat.parse(timeString);
			return d.getTime();
		} catch (ParseException e) {
			return 0;
		}
	}

	// ********** robot ***************
	public Robot downloadRobotRules() {
		headRequest();
		if (code != 200) {
			return null;
		} else {
			sendGETRobotRequest();
			return robot;
		}
	}

	/**
	 * Send robot GET request
	 */
	public void sendGETRobotRequest() {
		try {
			Socket socket = new Socket(host, portNum);
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			out.write("GET " + path + " HTTP/1.1\r\n");
			out.write("Host: " + host + ":" + portNum + "\r\n");
			out.write("User-Agent: cis455crawler\r\n");
			out.write("Connection: close\r\n\r\n");
			out.flush();
			// get response body
			parseGetRobotResponse(socket.getInputStream());
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * parse GET robot response and store content in field body
	 * 
	 * @param inputStream
	 * @return
	 */
	private void parseGetRobotResponse(InputStream inputStream) {
		try {
			InputStreamReader inputReader = new InputStreamReader(inputStream);
			BufferedReader bufferReader = new BufferedReader(inputReader);
			// initial robot here
			robot = new Robot();
			String agent = "*";// default
			String nextLine = bufferReader.readLine();
			while (nextLine != null) {
				// System.out.println("@ " +nextLine);
				if (nextLine.trim().startsWith("User-agent")) {
					agent = nextLine.trim().split(":")[1].trim();
					robot.addAgent(agent);
				} else if (nextLine.trim().startsWith("Disallow")) {
					String banned = nextLine.trim().split(":")[1].trim();
					robot.addBanned(agent, banned);
				} else if (nextLine.trim().startsWith("Allow")) {
					String allow = nextLine.trim().split(":")[1].trim();
					robot.addAllow(agent, allow);
				} else if (nextLine.trim().startsWith("Crawl-delay")) {
					int delay = Integer.valueOf(nextLine.trim().split(":")[1].trim());
					robot.addDelay(agent, delay);
				}
				nextLine = bufferReader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// ********** getters *************
	/**
	 * get host
	 * 
	 * @return
	 */
	public String getHost() {
		return this.host;
	}

	/**
	 * get code status
	 * 
	 * @return
	 */
	public int getCode() {
		return this.code;
	}

	/**
	 * get content type
	 * 
	 * @return
	 */
	public String getContentType() {
		return this.contentType;
	}

	/**
	 * get content length
	 * 
	 * @return
	 */
	public int getContentLength() {
		return this.contentLen;
	}

	/**
	 * get last modified as long type
	 * 
	 * @return
	 */
	public long getLastModified() {
		return this.lastModified;
	}

	/**
	 * get content body of HTML of XML file
	 * 
	 * @return
	 */
	public String getBody() {
		return this.body;
	}

}
