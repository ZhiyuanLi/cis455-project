package edu.upenn.cis455.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Search Servlet
 * 
 * @author zhiyuanli
 *
 */
@SuppressWarnings("serial")
public class SearchServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String docType = "<!DOCTYPE html>\n";
		String title = "Search Engine";
		out.println(docType + "<html>\n" + "<head><title>" + title + "</title></head>\n"
				+ "<body bgcolor=\"#f0f0f0\">\n" + "<h1 align=\"center\">" + title + "</h1>\n"
				+ "<form method=\"post\"><ul>\n" + "<input type=\"text\" name=\"query\" size=\"35\" required>" + "\n"
				+ "<input type=\"submit\" value=\"Search\">" + "</ul></form>\n" + "</body></html>");

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String[] words = request.getParameter("query").split("\\b");
		
	}

	@Override
	public void init() throws ServletException {
		// set worker status for this worker

	}

}
