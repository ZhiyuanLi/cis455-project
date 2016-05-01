package edu.upenn.cis455.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.upenn.cis455.search.DocInfo;
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
		out.println(docType + "<html>\n" + "<head><title>" + title + "</title>");
		out.println("<style>");
		out.println(
				"html,body{background: #34addb;color: #fff;padding: 24px;position: relative;z-index: 0;font-family: Helvetica Neue, Helvetica, Arial, sans-serif;text-shadow: 0 1px 1px rgba(0,150,200,.5);line-height: 1.5;}");
		out.println("body {margin: 0 auto;width: 500px;}");
		out.println("h1 {font-size: 1.5em;margin-bottom: .5em;font-weight: 700;}");
		out.println(
				"p {    margin-bottom: 1.5em;}a {    text-decoration: none;    font-weight: 700;    color: #ff9;}#search {    -webkit-appearance: none;    font-family: Helvetica Neue, Helvetica, Arial, sans-serif;    width: 24px;    padding: 0 10px;    height: 24px;    font-size: 14px;    color: #666;    line-height: 24px;    border: 0;    border-radius: 50px;    box-shadow: 0 0 0 1px rgba(0,150,200,.5), inset 0 2px 5px rgba(0,100,150,.3), 0 2px 0 rgba(255,255,255,.6);    position: relative;    z-index: 5;    -webkit-transition: .3s ease;    -moz-transition: .3s ease;}#search:focus {    outline: none;    width: 180px;}p.s {    z-index: 4;    position: relative;    padding: 5px;    line-height: 0;    border-radius: 100px;    background: #b9ecfe;    background-image: -webkit-linear-gradient(#dbf6ff,#b9ecfe);    background-image: -moz-linear-gradient(#dbf6ff,#b9ecfe);    display: inline-block;    box-shadow: inset 0 1px 0 rgba(255,255,255,.6), 0 2px 5px rgba(0,100,150,.4);}p.s:hover {    box-shadow: inset 0 1px 0 rgba(255,255,255,.6), 0 2px 3px 2px rgba(100,200,255,.5);}p.s:after {    content: '';    display: block;    position: absolute;    width: 5px;    height: 20px;    background: #b9ecfe;    bottom: -10px;    right: -3px;    border-radius: 0 0 5px 5px;    -webkit-transform: rotate(-45deg);    -moz-transform: rotate(-45deg);    box-shadow: inset 0 -1px 0 rgbA(255,255,255,.6), -2px 2px 2px rgba(0,100,150,.4);}p.s:hover:after {    box-shadow: inset 0 -1px 0 rgba(255,255,255,.6), -2px 2px 2px 1px rgba(100,200,255,.5);}");
		out.println("</style></head>");
		out.println("<h1>Mini Google</h1>");
		out.println(
				"<form method=\"post\"><p class=\"s\"><input name=\"query\" id=\"search\" type=\"search\" required></p>");
		out.println("<br><input type=\"checkbox\" name=\"weather\" value=\"true\">Search Weather");
		out.println("<br><input type=\"checkbox\" name=\"debug\" value=\"true\">Debug Mode");
		out.println("</form></body></html>");

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println(request.getParameter("query"));
		System.out.println(request.getParameter("weather"));
		System.out.println(request.getParameter("debug"));
		//
		// searchEngine = new SearchEngine();
		//
		// searchEngine.setQuery(request.getParameter("query"));
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		// String docType = "<!DOCTYPE html>\n";
		// String title = "Search Results";
		// out.println(docType + "<html>\n" + "<head><title>" + title +
		// "</title></head>\n"
		// + "<body bgcolor=\"#f0f0f0\">\n" + "<h1 align=\"center\">" + title +
		// "</h1>\n");
		// for (DocInfo docInfo : searchEngine.getResults()) {
		// out.println("<p>" + docInfo.url + "" + docInfo.indexScore + "</p>");
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
