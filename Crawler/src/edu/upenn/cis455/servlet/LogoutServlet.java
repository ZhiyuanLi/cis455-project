package edu.upenn.cis455.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class LogoutServlet extends HttpServlet {
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			out.println("<!DOCTYPE html><html>");
			out.println("<h2>Wei Song</h2><body>");
			out.println("<h3>Penn Key: weisong</h3>");
			out.println("<h2>Logout Success</h2>");
			out.println("<p>Login your account</p>");
			out.println("<form method = \"get\" action = \"login\">");
			out.println("<button type= \"submit\">Return to login page</button>");
			out.println("</form></body></html>");
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
