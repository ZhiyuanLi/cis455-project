package edu.upenn.cis455.servlet;

//import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.upenn.cis455.storage.Channel;
import edu.upenn.cis455.storage.DatabaseWrapper;
import edu.upenn.cis455.storage.User;
import edu.upenn.cis455.storage.WebDocument;

@SuppressWarnings("serial")

public class ChannelServlet extends HttpServlet {

	/**
	 * display page and manage page
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();

			// String dbPath = getServletContext().getInitParameter("BDBstore");
			// DatabaseWrapper db =
			// DatabaseWrapper.getSingletonDatabase(dbPath);

			DatabaseWrapper db = new DatabaseWrapper(getServletContext().getInitParameter("BDBstore"));
			String userAction = request.getParameter("userAction");
			if (userAction == null) {
				// illegal action, back to login page
				out.println("<!DOCTYPE html><html>");
				out.println("<h2>Wei Song</h2><body>");
				out.println("<h3>Penn Key: weisong</h3>");
				out.println("<p>Action Illegal. Back to login page</p>");
				out.println("<form method = \"get\" action = \"create\">");
				out.println("<button type = \"submit\">Return to login</button>");
				out.println("</form>");
				out.println("</body></html>");
				out.close();
				return;
			}
			// case 1: show all options
			if (userAction.equals("showOptions")) {
				String userName = request.getParameter("userName");
				showOptions(userName, out);
			}
			// case 2: show all channels
			// do not require login
			else if (userAction.equals("allChannels")) {
				showAllChannels(db, out);
			}
			// case 3: show user channels, is also delete channel!
			else if (userAction.equals("userChannels")) {
				String userName = request.getParameter("userName");
				showUserChannels(userName, db, out);
			}
			// case 4: show create channel page
			else if (userAction.equals("showCreateChannel")) {
				String userName = request.getParameter("userName");
				showCreateChannel(userName, out);
			}
			// case 5: show delete channel page
			else if (userAction.equals("deleteChannel")) {
				String userName = request.getParameter("userName");
				showDeleteChannels(userName, db, out);
			}
			db.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * doPost page to handler delete, create and show content
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		try {
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			// String dbPath = getServletContext().getInitParameter("BDBstore");
			// DatabaseWrapper db =
			// DatabaseWrapper.getSingletonDatabase(dbPath);
			DatabaseWrapper db = new DatabaseWrapper(getServletContext().getInitParameter("BDBstore"));

			String userAction = request.getParameter("userAction");

			if (userAction == null) {
				// illegal action, back to login page
				out.println("<!DOCTYPE html><html>");
				out.println("<h2>Wei Song</h2><body>");
				out.println("<h3>Penn Key: weisong</h3>");
				out.println("<p>Action Illegal. Back to login page</p>");
				out.println("<form method = \"get\" action = \"login\">");
				out.println("<button type = \"submit\">Return to login</button>");
				out.println("</form>");
				out.println("</body></html>");
				out.close();
				return;
			}
			if (userAction.equals("deleteChannel")) {
				String userName = request.getParameter("userName");
				deleteChannels(userName, db, out, request);
			} else if (userAction.equals("createChannel")) {
				createChannel(db, request, out);
			} else if (userAction.equals("showXML")) {
				// change to XML response type
				response.setContentType("text/xml");
				response.setStatus(200);
				out = response.getWriter();

				String cName = request.getParameter("cName");
				showXML(cName, db, out);
			}
			db.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// ********** GET ******************
	/**
	 * Important! show four options for user, include create, delete, show user
	 * channels, and show all channels
	 * 
	 * @param userName
	 * @param out
	 */
	private void showOptions(String userName, PrintWriter out) {
		out.println("<!DOCTYPE html><html>");
		out.println("<h3>Wei Song</h3>");
		out.println("<h3>Penn Key: weisong</h3>");
		out.println("<h3>Manage Page</h3>");
		out.println("<body>");
		out.println("<p>Please make you choices:</p>");

		// option 1: create channel GET
		out.print("<p>Create channel</p>");
		out.println("<form method = \"get\" action = \"channel\">");
		out.println("<input type = \"hidden\" name = \"userAction\" value = \"showCreateChannel\">");
		out.println("<input type = \"hidden\" name = \"userName\" value = \"" + userName + "\">");
		out.println("<button type = \"submit\">Create Channel</button>");
		out.println("</form><br>");
		// option 2: delete channel GET
		out.print("<p>Delete channel</p>");
		out.println("<form method = \"get\" action = \"channel\">");
		out.println("<input type = \"hidden\" name = \"userAction\" value = \"deleteChannel\">");
		out.println("<input type = \"hidden\" name = \"userName\" value = \"" + userName + "\">");
		out.println("<button type = \"submit\">Delete Channel</button>");
		out.println("</form><br>");

		// option 3: show user channels GET
		out.print("<p>Show my channels</p>");
		out.println("<form method = \"get\" action = \"channel\">");
		out.println("<input type = \"hidden\" name = \"userAction\" value = \"userChannels\">");
		out.println("<input type = \"hidden\" name = \"userName\" value = \"" + userName + "\">");
		out.println("<button type = \"submit\">Show My Channels</button>");
		out.println("</form><br>");

		// option 4: show all channels GET
		out.print("<p>Show all channels</p>");
		out.println("<form method = \"get\" action = \"channel\">");
		out.println("<input type = \"hidden\" name = \"userAction\" value = \"allChannels\">");
		out.println("<button type = \"submit\">Show All Channels</button>");
		out.println("</form><br>");
		// option 5: log out
		out.print("<p>Logout Account</p>");
		out.println("<form method = \"get\" action = \"logout\">");
		out.println("<button type = \"submit\">Logout</button>");
		out.println("</form>");

		out.println("</body></html>");
	}

	/**
	 * show create page and ask user for input. direct to createChannel in post
	 * method
	 * 
	 * @param userName
	 * @param out
	 */
	private void showCreateChannel(String userName, PrintWriter out) {
		out.println("<!DOCTYPE html><html>");
		out.println("<h2>Wei Song</h2><body>");
		out.println("<h3>Penn Key: weisong</h3>");
		out.println("<h3>Create Channel Page</h3>");

		out.println("<form method = \"post\" action = \"channel\">");
		out.println("New Channel Name: <input type = \"text\" name = \"cName\">");
		out.println("<br><br>");
		out.println("Xpaths: <input type = \"text\" name = \"xPaths\">");
		out.println("<br><br>");
		out.println("URL: <input type = \"text\" name = \"xslURL\">");
		out.println("<br><br>");
		out.println("<input type=\"hidden\" name=\"userAction\" value=\"createChannel\">");
		out.println("<input type = \"hidden\" name = \"userName\" value = \"" + userName + "\">");
		out.println("<button type=\"submit\">Create channel</button></form>");

		// provide option to go to manage page
		out.println("<p>Return to manage page</p>");
		out.println("<form method = \"get\" action = \"channel\">");
		out.println("<input type=\"hidden\" name=\"userName\" value=\"" + userName + "\">");
		out.println("<input type=\"hidden\" name = \"userAction\" value = \"showOptions\">");
		out.println("<button type=\"submit\">Return To Manage Page</button></form>");
	}

	/**
	 * show delete channels and also enable user to delete selected channels;
	 * called by doGet from option page, direct to delete page in doPost
	 * 
	 * @param userName
	 * @param db
	 * @param out
	 */
	private void showDeleteChannels(String userName, DatabaseWrapper db, PrintWriter out) {
		List<Channel> allChannels = db.getChannelList();
		if (allChannels == null || allChannels.size() == 0) {
			// 1. if empty, return to option page
			out.println("<!DOCTYPE html><html>");
			out.println("<h2>Wei Song</h2><body>");
			out.println("<h3>Penn Key: weisong</h3>");
			out.println("<p>You Have 0 Channel Available</p>");
			out.println("<form method = \"get\" action = \"channel\">");
			out.println("<input type= \"hidden\" name = \"userAction\" value = \"showOptions\">");
			out.println("<input type= \"hidden\" name = \"userName\" value = \"" + userName + "\">");
			out.println("<button type = \"submit\">Return to manage page</button>");
			out.println("</form>");
			out.println("</body></html>");
		} else {
			out.println("<!DOCTYPE html><html>");
			out.println("<h2>Wei Song</h2><body>");
			out.println("<h3>Penn Key: weisong</h3>");
			out.println("<p>Your Channels:</p>");

			out.println("<form method = \"post\" action = \"channel\">");
			// direct to delete channel
			out.println("<input type= \"hidden\" name = \"userAction\" value = \"deleteChannel\">");
			out.println("<input type= \"hidden\" name = \"userName\" value = \"" + userName + "\">");
			for (int i = 0; i < allChannels.size(); i++) {
				Channel c = allChannels.get(i);
				String channelUser = c.getUserName();
				String cName = c.getCName();
				// user name must match
				if (userName.equals(channelUser)) {
					// send delete with channel name
					out.println("<input type= \"checkbox\" name= \"" + cName + "\" value=\"Del\">" + cName + "<br>");
				}
			}
			out.println("<br><button type = \"submit\">Delete Selected Channel</button>");
			out.println("</form>");

			out.println("<br><form method = \"get\" action = \"channel\">");
			out.println("<input type= \"hidden\" name = \"userAction\" value = \"showOptions\">");
			out.println("<input type= \"hidden\" name = \"userName\" value = \"" + userName + "\">");
			out.println("<button type = \"submit\">Return to manage page</button>");
			out.println("</form>");

			out.println("</body></html>");
		}
	}

	/**
	 * show user channels and also enable user to delete selected channels;
	 * called by doGet from option page, direct to delete page in doPost
	 * 
	 * @param userName
	 * @param db
	 * @param out
	 */
	private void showUserChannels(String userName, DatabaseWrapper db, PrintWriter out) {
		List<Channel> allChannels = db.getChannelList();
		// 1. no channels available, direct to login
		if (allChannels == null || allChannels.size() == 0) {
			out.println("<!DOCTYPE html><html>");
			out.println("<h2>Wei Song</h2><body>");
			out.println("<h3>Penn Key: weisong</h3>");
			out.println("<p>You Have 0 Channel Available</p>");
			//direct to manage page
			out.println("<form method = \"get\" action = \"channel\">");
			out.println("<input type= \"hidden\" name = \"userAction\" value = \"showOptions\">");
			out.println("<input type= \"hidden\" name = \"userName\" value = \"" + userName + "\">");
			out.println("<button type = \"submit\">Return to manage page</button>");
			out.println("</form></body></html>");
		}
		// 2. list all channels under user
		else {
			out.println("<!DOCTYPE html><html>");
			out.println("<h2>Wei Song</h2><body>");
			out.println("<h3>Penn Key: weisong</h3>");
			out.println("<h3>All Channels In Database:</h3><body>");
			for (Channel eachChannel : allChannels) {
				// only show user channels and provide function to delete
				if (!eachChannel.getUserName().equals(userName)) {
					continue;
				}
				String cName = eachChannel.getCName();
				out.println("<p>Channel: " + cName + "</p>");
				out.println("<form method = \"post\" action = \"channel\">");
				out.println("<input type= \"hidden\" name = \"userAction\" value = \"showXML\">");
				// need to pass channel name for display
				out.println("<input type= \"hidden\" name = \"cName\" value = \"" + cName + "\"><br>");
				out.println("<button type = \"submit\">Display Channel</button>");
				out.println("</form><br>");
			}

			// provide option to go to manage page
			out.println("<br><p>Return to manage page</p>");
			out.println("<form method = \"get\" action = \"channel\">");
			out.println("<input type=\"hidden\" name=\"userName\" value=\"" + userName + "\">");
			out.println("<input type=\"hidden\" name = \"userAction\" value = \"showOptions\">");
			out.println("<button type=\"submit\">Return To Manage Page</button></form>");
			out.println("</body></html>");
		}
	}

	/**
	 * List all channels in database and provide link to each XML file
	 * 
	 * @param db
	 * @param out
	 */
	private void showAllChannels(DatabaseWrapper db, PrintWriter out) {
		List<Channel> allChannels = db.getChannelList();
		// 1. no channels available, direct to login
		if (allChannels == null || allChannels.size() == 0) {
			out.println("<!DOCTYPE html><html>");
			out.println("<h3>Wei Song</h3>");
			out.println("<h3>Penn Key: weisong</h3>");
			out.println("<h2>No Channel In Database</h2>");
			out.println("<body><p>Return to login page</p>");
			out.println("<form method = \"get\" action = \"login\">");
			out.println("<button type = \"submit\">Return to login</button>");
			out.println("</form></body></html>");
		}
		// 2. list all channels in database
		else {
			out.println("<!DOCTYPE html><html>");
			out.println("<h3>Wei Song</h3>");
			out.println("<h3>Penn Key: weisong</h3>");
			out.println("<h2>All Channels In Database:</h2><body>");
			for (Channel eachChannel : allChannels) {
				String cName = eachChannel.getCName();
				out.println("<p>Channel: " + cName + "</p>");
			}
			out.println("<br><form method = \"get\" action = \"login\">");
			out.println("<button type = \"submit\">Return to login</button>");
			out.println("</form></body></html>");
		}
	}

	// *********** POST *******************

	/**
	 * create a channel and add to database; directed from showCreateChannel in
	 * GET
	 * 
	 * @param db
	 * @param request
	 * @param out
	 */
	private void createChannel(DatabaseWrapper db, HttpServletRequest request, PrintWriter out) {
		String cName = request.getParameter("cName");
		String rawXpath = request.getParameter("xPaths");
		String userName = request.getParameter("userName");
		String xslURL = request.getParameter("xslURL");

		List<String> xPaths = new ArrayList<String>();
		String[] xpathsArr = rawXpath.trim().split(";");
		for (String eachPath : xpathsArr) {
			xPaths.add(eachPath.trim());
		}

		// each channel has a user
		Channel channel = new Channel(cName, xPaths, xslURL);

		channel.setUserName(userName);
		db.addChannel(channel, userName);

		afterCreateChannel(userName, out);
	}

	/**
	 * helper function used in createChannel
	 * 
	 * @param userName
	 * @param out
	 */
	private void afterCreateChannel(String userName, PrintWriter out) {
		out.println("<!DOCTYPE html><html>");
		out.println("<h3>Wei Song</h3><body>");
		out.println("<h3>Penn Key: weisong</h3>");
		out.println("<h2>Channel Created</h2>");
		// 1. user channels GET
		out.println("<p>Show your channels</p>");
		out.println("<form method = \"get\" action = \"channel\">");
		out.println("<input type=\"hidden\" name=\"userName\" value=\"" + userName + "\">");
		out.println("<input type=\"hidden\" name = \"userAction\" value = \"userChannels\">");
		out.println("<button type=\"submit\">Show Your Channels</button></form>");

		// 2. all channels GET
		out.println("<p>Show all channels</p>");
		out.println("<form method = \"get\" action = \"channel\">");
		out.println("<input type=\"hidden\" name=\"userName\" value=\"" + userName + "\">");
		out.println("<input type=\"hidden\" name = \"userAction\" value = \"allChannels\">");
		out.println("<button type=\"submit\">Show All Channels</button></form>");

		// 3. return to option page GET
		out.println("<p>Return to manage page</p>");
		out.println("<form method = \"get\" action = \"channel\">");
		out.println("<input type=\"hidden\" name=\"userName\" value=\"" + userName + "\">");
		out.println("<input type=\"hidden\" name = \"userAction\" value = \"showOptions\">");
		out.println("<button type=\"submit\">Return To Manage Page</button></form>");
		out.println("</body></html>");
	}

	/**
	 * delete channel in doPost, called by showUserChannels, directed from
	 * deleteChannel
	 * 
	 * @param userName
	 * @param db
	 * @param out
	 * @param request
	 */
	private void deleteChannels(String userName, DatabaseWrapper db, PrintWriter out, HttpServletRequest request) {
		User user = db.getUser(userName);
		List<String> channelNames = user.getChannelNames();
		for (String eachChannelName : channelNames) {
			// important!
			String action = request.getParameter(eachChannelName);
			if (action != null && action.equals("Del")) {
				db.deleteChannel(userName, eachChannelName);
			}
		}
		// after delete
		out.println("<!DOCTYPE html><html><body>");
		out.println("<h3>Wei Song</h3>");
		out.println("<h3>Penn Key: weisong</h3>");
		out.println("<h2>Delete Success</h2><body>");
		out.println("<p>Your updated channels:</p>");
		user = db.getUser(userName);
		channelNames = user.getChannelNames();
		if (channelNames == null || channelNames.size() == 0) {
			out.println("<p>Currently you have 0 channels</p>");
		} else {
			out.println("<ul>");
			for (int i = 0; i < channelNames.size(); i++) {
				out.print("<li>" + channelNames.get(i) + "</li>");
			}
			out.println("</ul>");
		}
		// provide option to go to manage page
		out.println("<p>Return to manage page</p>");
		out.println("<form method = \"get\" action = \"channel\">");
		out.println("<input type=\"hidden\" name=\"userName\" value=\"" + userName + "\">");
		out.println("<input type=\"hidden\" name = \"userAction\" value = \"showOptions\">");
		out.println("<button type=\"submit\">Return To Manage Page</button></form>");

		out.println("</body></html>");
	}

	/**
	 * Display channel! doPost method to display XML contents; called by all
	 * channels in doGet, directed by showXML
	 * 
	 * @param cName
	 * @param db
	 * @param out
	 */
	private void showXML(String cName, DatabaseWrapper db, PrintWriter out) {
		// //debug use only
		// out.println("<?xml-stylesheet type=\"text/xsl\" href=\"" + xslURL +
		// "\"?>");
		// xslURL should be :media/sf_share/HW2/rss/rss.xsl
		// test
		// System.out.println("@@start@@@@ ");
		// System.out.println("xsl path is: "+xslURL);
		// File f = new File(xslURL);
		// System.out.println("Exist: "+f.exists());
		//
		// System.out.println("@@end@@@@ ");
		Channel channel = db.getChannel(cName);

		String xslURL = channel.getXslUrl();
		List<String> matchedURLs = channel.getMatchedURLs();

		// XSL format begin
		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		// TODO added
		out.println("<?xml-stylesheet type=\"text/xsl\" href=\"" + xslURL + "\"?>");
		// out.println("<xsl:stylesheet type=\"text/xsl\"
		// href=\"http://www.w3.org/1999/XSL/Transform\">");
		out.println("<documentcollection>");

		for (String eachMatchURL : matchedURLs) {
			WebDocument doc = db.getDocument(eachMatchURL);
			if (doc == null) {
				continue;
			}
			String body = doc.getDocumentContent();
			// System.out.println("@@ body is "+body);
			// System.out.println("@@ contains "+body.contains("xml version"));
			long lastCrawlTime = doc.getLastCrawlTime();
			String lastCrawlTimeStr = lastCrawlTimeToStr(lastCrawlTime);
			String location = eachMatchURL;

			out.println("<document crawled=\"" + lastCrawlTimeStr + "\" location=\"" + location + "\">");
			if (body.contains("?>")) {
				// construct head
				int start = body.indexOf("?>") + 2;
				// extract head instruction line
				body = body.substring(start);
				// System.out.println("new body is " + body);
				out.println(body);

			} else {
				// print whole body
				out.println(body);
			}
			out.println("</document>");
		}
		out.println("</documentcollection>");
		// XSL format end
	}

	/**
	 * helper function to convert time from long to string
	 * 
	 * @param lastCrawlTime
	 * @return
	 */
	private String lastCrawlTimeToStr(long lastCrawlTime) {
		SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-DD'T'hh:mm:ss");
		Date date = new Date(lastCrawlTime);
		String result = format.format(date);
		return result;
	}
}
