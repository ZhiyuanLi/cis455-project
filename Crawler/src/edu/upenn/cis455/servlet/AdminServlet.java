package edu.upenn.cis455.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class AdminServlet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			String userName = request.getParameter("userName").trim();
			String password = request.getParameter("password").trim();
			String userAction = request.getParameter("userAction").trim();

			if (!userName.equals("admin") || !password.equals("admin")) {
				out.println("<!DOCTYPE html><html>");
				out.println("<h3>Wei Song</h3>");
				out.println("<h3>Penn Key: weisong</h3>");
				out.println("<h2>Admin Page</h2>");
				out.println("<body>");
				out.println("<p>Admin user name or password incorrect.</p><br><br>");
				// return to login
				out.println("<form method = \"get\" action = \"login\">");
				out.println("<button type = \"submit\">Return to login</button>");
				out.println("</form>");
				out.println("</body></html>");
			} else {
				// adminOptions
				if (userAction.equals("adminOptions")) {
					showAdminOptionPage(out, request);
				} else if (userAction.equals("startCrawlingPage")) {
					showStartPage(out, request);
				} else if (userAction.equals("stopCrawlingPage")) {
					showStopPage(out, request);
				}
			}
			// close finally
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// ************ get helper function ***************
	/**
	 * called by get method to show all admin options
	 * 
	 * @param out
	 * @param request
	 */
	private void showAdminOptionPage(PrintWriter out, HttpServletRequest request) {
		out.println("<!DOCTYPE html><html>");
		out.println("<h3>Wei Song</h3>");
		out.println("<h3>Penn Key: weisong</h3>");
		out.println("<h2>Admin Page</h2>");
		out.println("<body>");
		// start crawling
		out.println("<br><p>Start Crawling</p>");
		out.println("<form method = \"get\" action = \"admin\">");
		out.println("<input type=\"hidden\" name=\"userName\" value=\"admin\">");
		out.println("<input type=\"hidden\" name=\"password\" value=\"admin\">");
		out.println("<input type = \"hidden\" name = \"userAction\" value = \"startCrawlingPage\">");
		out.println("<button type = \"submit\">Start Crawling</button>");
		out.println("</form>");
		// stop crawling
		out.println("<br><p>Stop Crawling</p>");
		out.println("<form method = \"get\" action = \"admin\">");
		out.println("<input type=\"hidden\" name=\"userName\" value=\"admin\">");
		out.println("<input type=\"hidden\" name=\"password\" value=\"admin\">");
		out.println("<input type = \"hidden\" name = \"userAction\" value = \"stopCrawlingPage\">");
		out.println("<button type = \"submit\">Stop Crawling</button>");
		out.println("</form>");
		// display crawler
		out.println("<br><p>Display Crawler Statistics</p>");
		out.println("<form method = \"get\" action = \"crawler\">");
		out.println("<input type=\"hidden\" name=\"userName\" value=\"admin\">");
		out.println("<input type=\"hidden\" name=\"password\" value=\"admin\">");
		out.println("<input type = \"hidden\" name = \"userAction\" value = \"display\">");
		out.println("<button type = \"submit\">Display Crawler</button>");
		out.println("</form>");
		// back to login
		out.println("<form method = \"get\" action = \"logout\">");
		out.println("<button type = \"submit\">Logout</button>");
		out.println("</form>");

		out.println("</body></html>");
	}

	private void showStartPage(PrintWriter out, HttpServletRequest request) {
		out.println("<!DOCTYPE html><html>");
		out.println("<h3>Wei Song</h3>");
		out.println("<h3>Penn Key: weisong</h3>");
		out.println("<h2>Start Crawling Page</h2>");
		out.println("<body>");

		out.println("<form method = \"get\" action = \"crawler\">");
		out.println("URL to start: <input type = \"text\" name = urlStart>");
		out.println("<br><br>");
		out.println("Max File Size in MB: <input type = \"text\" name = size>");
		out.println("<br><br>");
		out.println("Max File Number: <input type = \"text\" name = number>");
		out.println("<br><br>");
		out.println("<input type = \"hidden\" name = \"userAction\" value = \"start\">");
		out.println("<button type = \"submit\">Crawler Start Now</button>");
		out.println("</form>");
		out.println("</body></html>");
	}

	private void showStopPage(PrintWriter out, HttpServletRequest request) {
		out.println("<!DOCTYPE html><html>");
		out.println("<h3>Wei Song</h3>");
		out.println("<h3>Penn Key: weisong</h3>");
		out.println("<h2>Stop Crawling Page</h2>");
		out.println("<body>");

		out.println("<form method = \"get\" action = \"crawler\">");
		out.println("<input type = \"hidden\" name = \"userAction\" value = \"stop\">");
		out.println("<button type = \"submit\">Crawler Stop</button>");
		out.println("</form>");
		out.println("</body></html>");
	}

	// not used
	// public void doPost(HttpServletRequest request, HttpServletResponse
	// response) {
	// try {
	//
	// }catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
}
