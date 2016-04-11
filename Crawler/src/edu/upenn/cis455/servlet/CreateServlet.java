package edu.upenn.cis455.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.upenn.cis455.storage.DatabaseWrapper;
import edu.upenn.cis455.storage.User;

@SuppressWarnings("serial")

/**
 * create account servlet
 * 
 * @author weisong
 *
 */
public class CreateServlet extends HttpServlet {
	/**
	 * display create account page and ask user for user name and password; will
	 * forward to doPut method
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			out.println("<!DOCTYPE html><html>");
			out.println("<h3>Wei Song</h3><body>");
			out.println("<h3>Penn Key: weisong</h3>");
			out.println("<h2>Create Account</h2>");
			out.println("<form method = \"post\">");
			out.println("User Name: <input type = \"text\" name = \"userName\">");
			out.println("<br><br>");
			out.println("Password: <input type = \"password\" name = \"password\">");
			out.println("<br><br>");
			out.println("<button type= \"submit\">Create Account</button>");
			out.println("</form>");

			// admin login
			out.println("<br><br><p>Already have an account?</p>");
			out.println("<form method=\"get\" action=\"login\">");
			out.println("<button type= \"submit\">Go to login</button></form>");
			out.println("<br><br>");
			out.println("</form></body></html>");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * response to create account page; store user information to database
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		try {
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();

			String userName = request.getParameter("userName");
			String password = request.getParameter("password");
			String dbPath = getServletContext().getInitParameter("BDBstore");
			// do work
			boolean illegal = checkIllegal(dbPath, userName, password);
			if (!illegal) {
				createUserAccount(dbPath, userName, password);
				out.println("<!DOCTYPE html><html>");
				out.println("<h3>Wei Song</h3><body>");
				out.println("<h3>Penn Key: weisong</h3>");
				out.println("<h2>Account Created!</h2>");
				// option 1: login
				out.println("<p>Now you can login your account</p>");
				out.println("<form method = \"get\" action = \"login\">");
				out.println("<button type= \"submit\">Login</button>");
				out.println("</form>");
				// option 2: show all channels
				out.println("<br><p>Show all channels</p>");
				out.println("<form method = \"get\" action = \"channel\">");
				out.println("<input type = \"hidden\" name = \"userAction\" value = \"allChannels\">");
				out.println("<button type = \"submit\">Show all channels</button>");
				out.println("</form>");
				out.println("</body></html>");
			} else {
				out.println("<!DOCTYPE html><html>");
				out.println("<h3>Wei Song</h3><body>");
				out.println("<h3>Penn Key: weisong</h3>");
				out.println("<h2>User name or password illegal</h2>");
				out.println("<p>This may be caused by user name already exist or you miss user name or password</p>");
				out.println("<p>Please choose another user name</p>");
				out.println("<form method = \"get\" action = \"login\">");
				out.println("<button type= \"submit\">Login</button>");
				out.println("</form></body></html>");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * check if user name already exist in our database
	 * 
	 * @param dbPath
	 * @param userName
	 * @param password
	 * @return
	 */
	private boolean checkIllegal(String dbPath, String userName, String password) {
		if (userName == null || userName.trim().length() == 0)
			return true;
		if (password == null || password.trim().length() == 0)
			return true;
		// DatabaseWrapper db = DatabaseWrapper.getSingletonDatabase(dbPath);
		DatabaseWrapper db = new DatabaseWrapper(dbPath);
		// System.out.println("User length is "+db.getUserList().size());
		boolean result = db.containsUser(userName);
		db.close();
		return result;

	}

	/**
	 * add user account to database
	 * 
	 * @param dbPath
	 * @param userName
	 * @param password
	 */
	private void createUserAccount(String dbPath, String userName, String password) {
		User user = new User(userName);
		user.setPassword(password);

		// DatabaseWrapper db = DatabaseWrapper.getSingletonDatabase(dbPath);
		DatabaseWrapper db = new DatabaseWrapper(dbPath);
		db.addUser(user);
		db.close();
	}
}

// }                                                                              