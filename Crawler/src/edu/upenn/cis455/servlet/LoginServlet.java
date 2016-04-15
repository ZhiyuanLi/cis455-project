package edu.upenn.cis455.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.upenn.cis455.storage.DatabaseWrapper;
import edu.upenn.cis455.storage.User;

@SuppressWarnings("serial")
public class LoginServlet extends HttpServlet {

	/**
	 * login page, ask user for user name and password to login
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			out.println("<!DOCTYPE html><html>");
			out.println("<h3>Wei Song</h3>");
			out.println("<h3>Penn Key: weisong</h3>");
			out.println("<h2>Login Page</h2>");
			out.println("<body>");
			// user login
			out.println("<p>User Login</p>");
			out.println("<form method = \"post\" action = \"login\">");
			out.println("User Name: <input type = \"text\" name = \"userName\">");
			out.println("<br><br>"); 
			out.println("Password: <input type = \"password\" name = \"password\">");
			out.println("<br><br>");
			out.println("<button type= \"submit\">Login</button>");
			out.println("</form>");
			out.println("<br><br>");
			// admin login
			out.println("<p>Admin Login</p>");
			out.println("<p>For testing, both user name and password should be \"admin\"</p>");
			out.println("<form method=\"get\" action=\"admin\">");
			out.println("User Name: <input type = \"text\" name = \"userName\">");
			out.println("<br><br>");
			out.println("Password: <input type = \"password\" name = \"password\">");
			out.println("<br><br>");
			out.println("<input type=\"hidden\" name=\"userAction\" value=\"adminOptions\">");
			out.println("<button type= \"submit\">Admin Login</button></form>");
			out.println("<br><br>");
			// create account
			out.print("<p>Create Account Now</p>");
			out.println("<form method = \"get\" action = \"create\">");
			out.println("<button type= \"submit\">Create Account</button>");
			out.println("</form></body></html>");
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * handle user login page and direct to other pages
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		try {
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			String userName = request.getParameter("userName").trim();
			String password = request.getParameter("password").trim();
			boolean result = checkUserLogin(userName, password);
			if (result) {
				// login success
				out.println("<!DOCTYPE html><html>");
				out.println("<h3>Wei Song</h3>");
				out.println("<h3>Penn Key: weisong</h3>");
				out.println("<h2>Login Success</h2>");
				out.println("<body>");
				// option 1: direct to channel show options
				out.println("<p>Manage your channel: </p>");
				out.println("<form method = \"get\" action = \"channel\">");
				out.println("<input type = \"hidden\" name = \"userName\" value = \"" + userName + "\">");
				out.println("<input type = \"hidden\" name = \"userAction\" value = \"showOptions\">");
				out.println("<button type = \"submit\">Manage My Channel</button>");
				out.println("</form>");
				out.println("<br>");

				// option 2: direct to channel show channels
				out.println("<p>Show all channel: </p>");
				out.println("<form method = \"get\" action = \"channel\">");
				out.println("<input type = \"hidden\" name = \"userAction\" value = \"allChannels\">");
				out.println("<button type = \"submit\">All Channels</button>");
				out.println("</form>");
				out.println("<br>");

				// option 3: direct to logout
				out.println("<p>Logout you account: </p>");
				out.println("<form method = \"get\" action = \"logout\">");
				out.println("<button type = \"submit\">Logout</button>");
				out.println("</form>");
				out.println("<br>");
				out.println("</body></html>");
			} else {
				// login failed
				out.println("<!DOCTYPE html><html>");
				out.println("<h2>Wei Song</h2>");
				out.println("<h3>Penn Key: weisong</h3>");
				out.println("<h2>Login Failed</h2>");
				out.println("<p>User name or password incorrect</p>");
				out.println("<body>");

				// option 1: login again
				out.println("<p>Please login again</p>");
				out.println("<form method = \"get\" action = \"login\">");
				out.println("<button type = \"submit\">Login</button>");
				out.println("</form>");

				// option 2: create an account
				out.println("<br><p>Create an account now</p>");
				out.println("<form method = \"get\" action = \"create\">");
				out.println("<button type = \"submit\">Create Account</button>");
				out.println("</form>");

				// option 3: show channels
				out.println("<br><p>Show all channels</p>");
				out.println("<form method = \"get\" action = \"channel\">");
				out.println("<input type = \"hidden\" name = \"userAction\" value = \"allChannels\">");
				out.println("<button type = \"submit\">Show All channels</button>");
				out.println("</form>");

				out.write("</body></html>");
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * check if user exist and if password is correct
	 * 
	 * @param userName
	 * @param password
	 * @return
	 */
	private boolean checkUserLogin(String userName, String password) {
		// String dbPath = getServletContext().getInitParameter("BDBstore");
		// DatabaseWrapper db = DatabaseWrapper.getSingletonDatabase(dbPath);
		DatabaseWrapper db = new DatabaseWrapper(getServletContext().getInitParameter("BDBstore"));
		User user = db.getUser(userName);
		if (user == null) {
			db.close();
			return false;
		}
		if (!user.getPassword().equals(password)) {
			db.close();
			return false;
		}
		db.close();
		return true;
	}
}
