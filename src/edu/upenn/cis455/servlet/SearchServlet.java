package edu.upenn.cis455.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.upenn.cis455.search.SearchEngine;
import edu.upenn.cis455.storage.SingleWord;

/**
 * Search Servlet
 * 
 * @author zhiyuanli
 *
 */
@SuppressWarnings("serial")
public class SearchServlet extends HttpServlet {

	private SearchEngine searchEngine;

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
		System.out.println(request.getParameter("query"));

			searchEngine = new SearchEngine();
		
		searchEngine.setQuery(request.getParameter("query"));
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String docType = "<!DOCTYPE html>\n";
		String title = "Search Results";
		out.println(docType + "<html>\n" + "<head><title>" + title + "</title></head>\n"
				+ "<body bgcolor=\"#f0f0f0\">\n" + "<h1 align=\"center\">" + title + "</h1>\n");
		// for (SingleWord item : searchEngine.getResults()) {
		// out.println("<p>" + item.getWord() + " " + item.getUrl() + " " +
		// item.getIdf() + " " + item.getTf_idf()
		// + "</p>");
		// }
		out.println("</body></html>");
	}

	@Override
	public void init() throws ServletException {
		// try {
		// searchEngine = new SearchEngine();
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
	}

}
