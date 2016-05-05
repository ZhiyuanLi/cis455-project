package edu.upenn.cis455.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.upenn.cis455.search.DocInfo;
import edu.upenn.cis455.search.SearchEngine;
import edu.upenn.cis455.search.SearchEngineMultiThread;
import edu.upenn.cis455.search.SpellCheck;
import edu.upenn.cis455.storage.SingleWordTitle;

/**
 * Search Servlet
 * 
 * @author zhiyuanli
 *
 */
@SuppressWarnings("serial")
public class SearchServlet extends HttpServlet {

	private SearchEngineMultiThread searchEngine;

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
		out.println("h1 {font-size: 3.5em;margin-bottom: .5em;font-weight: 700;}");

		out.println(
				"p {    margin-bottom: 1.5em;}a {    text-decoration: none;    font-weight: 700;    color: #ff9;}#search {    -webkit-appearance: none;    font-family: Helvetica Neue, Helvetica, Arial, sans-serif;    width: 24px;    padding: 0 10px;    height: 24px;    font-size: 14px;    color: #666;    line-height: 24px;    border: 0;    border-radius: 50px;    box-shadow: 0 0 0 1px rgba(0,150,200,.5), inset 0 2px 5px rgba(0,100,150,.3), 0 2px 0 rgba(255,255,255,.6);    position: relative;    z-index: 5;    -webkit-transition: .3s ease;    -moz-transition: .3s ease;}#search:focus {    outline: none;    width: 180px;}p.s {    z-index: 4;    position: relative;    padding: 5px;    line-height: 0;    border-radius: 100px;    background: #b9ecfe;    background-image: -webkit-linear-gradient(#dbf6ff,#b9ecfe);    background-image: -moz-linear-gradient(#dbf6ff,#b9ecfe);    display: inline-block;    box-shadow: inset 0 1px 0 rgba(255,255,255,.6), 0 2px 5px rgba(0,100,150,.4);}p.s:hover {    box-shadow: inset 0 1px 0 rgba(255,255,255,.6), 0 2px 3px 2px rgba(100,200,255,.5);}p.s:after {    content: '';    display: block;    position: absolute;    width: 5px;    height: 30px;    background: #b9ecfe;    bottom: -10px;    right: -3px;    border-radius: 0 0 5px 5px;    -webkit-transform: rotate(-45deg);    -moz-transform: rotate(-45deg);    box-shadow: inset 0 -1px 0 rgbA(255,255,255,.6), -2px 2px 2px rgba(0,100,150,.4);}p.s:hover:after {    box-shadow: inset 0 -1px 0 rgba(255,255,255,.6), -2px 2px 2px 1px rgba(100,200,255,.5);}");
		out.println("</style></head>");
		out.println("<h1>Mini Google</h1>");
		out.println(
				"<form method=\"post\"><side><select name= \"mode\"><option value=\"web\">Web</option><option value=\"image\">Image</option></option><option value=\"weather\">Weather</option></select></side>");

		out.println(
				"<p class=\"s\"><input name=\"query\" id=\"search\" type=\"search\" required></p>");
		// check box
		out.println("<br><input type=\"checkbox\" name=\"spellcheck\" value=\"true\">Spell Check");
		out.println("<br><input type=\"checkbox\" name=\"debug\" value=\"true\">Debug Mode");
		out.println("</form></body></html>");

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		// System.out.println(request.getParameter("query"));
		// System.out.println(request.getParameter("weather"));
		// System.out.println(request.getParameter("debug"));

		/**
		 * get parameters
		 */
		String mode = request.getParameter("mode");
		String spellCheck = request.getParameter("spellcheck");
		String debug = request.getParameter("debug");
		String weather = request.getParameter("weather");
		String query = request.getParameter("query");

		String revisedQuery = null;
		if (spellCheck != null) {
			SpellCheck sp = new SpellCheck(query);
			revisedQuery = sp.getResult();
			if (!revisedQuery.equals("")) {
				query = revisedQuery;
			}
		}

		/**
		 * CSS
		 */
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String docType = "<!DOCTYPE html>\n";
		String title = "Search Engine";
		out.println(docType + "<html>\n" + "<head><title>" + title + "</title>");
		out.println("<style>");
		out.println(
				"html,body{background: #34addb;color: #fff;padding: 24px;position: relative;z-index: 0;font-family: Helvetica Neue, Helvetica, Arial, sans-serif;text-shadow: 0 1px 1px rgba(0,150,200,.5);line-height: 1.5;}");
		out.println("body {margin: 0 auto;width: 500px;}");
		out.println("h1 {font-size: 3.5em;margin-bottom: .5em;font-weight: 700;}");
		out.println("h2 {font-size: 1.5em;margin-bottom: .5em;font-weight: 700;}");

		out.println(
				"p {    margin-bottom: 1.5em;}a {    text-decoration: none;    font-weight: 700;    color: #ff9;}#search {    -webkit-appearance: none;    font-family: Helvetica Neue, Helvetica, Arial, sans-serif;    width: 24px;    padding: 0 10px;    height: 24px;    font-size: 14px;    color: #666;    line-height: 24px;    border: 0;    border-radius: 50px;    box-shadow: 0 0 0 1px rgba(0,150,200,.5), inset 0 2px 5px rgba(0,100,150,.3), 0 2px 0 rgba(255,255,255,.6);    position: relative;    z-index: 5;    -webkit-transition: .3s ease;    -moz-transition: .3s ease;}#search:focus {    outline: none;    width: 180px;}p.s {    z-index: 4;    position: relative;    padding: 5px;    line-height: 0;    border-radius: 100px;    background: #b9ecfe;    background-image: -webkit-linear-gradient(#dbf6ff,#b9ecfe);    background-image: -moz-linear-gradient(#dbf6ff,#b9ecfe);    display: inline-block;    box-shadow: inset 0 1px 0 rgba(255,255,255,.6), 0 2px 5px rgba(0,100,150,.4);}p.s:hover {    box-shadow: inset 0 1px 0 rgba(255,255,255,.6), 0 2px 3px 2px rgba(100,200,255,.5);}p.s:after {    content: '';    display: block;    position: absolute;    width: 5px;    height: 30px;    background: #b9ecfe;    bottom: -10px;    right: -3px;    border-radius: 0 0 5px 5px;    -webkit-transform: rotate(-45deg);    -moz-transform: rotate(-45deg);    box-shadow: inset 0 -1px 0 rgbA(255,255,255,.6), -2px 2px 2px rgba(0,100,150,.4);}p.s:hover:after {    box-shadow: inset 0 -1px 0 rgba(255,255,255,.6), -2px 2px 2px 1px rgba(100,200,255,.5);}");
		out.println("</style></head>");
		out.println("<h1>Search Results</h1>");
		out.println(
				"<form method=\"post\"><side><select name= \"mode\"><option value=\"web\">Web</option><option value=\"image\">Image</option></option><option value=\"weather\">Weather</option></select></side>");

		out.println(
				"<p class=\"s\"><input name=\"query\" id=\"search\" type=\"search\" required></p>");
		// check box
		out.println("<br><input type=\"checkbox\" name=\"spellcheck\" value=\"true\">Spell Check");
		out.println("<br><input type=\"checkbox\" name=\"debug\" value=\"true\">Debug Mode");

		//
		searchEngine = new SearchEngineMultiThread();
		if (revisedQuery != null && !revisedQuery.equals("")) {
			out.println("<p>" + "Do you mean " + revisedQuery + " ?" + "</p>");
		}
		switch (mode) {
		case "web":
			searchEngine.doSearchQuery(query, "word");
			String url;
			for (DocInfo docInfo : searchEngine.getResults()) {
				url = docInfo.url;

				out.println("<div>");
				out.println("<h2>" + docInfo.title + "</h2>");
				out.println("<a href=\"" + url + "\">" + " " + url + " " + "</a>");
				out.println("</div>");
				if (debug != null) {
					out.println("<p>Total Score:" + docInfo.totalScore + "</p>");
				}

			}
			break;
		case "image":
			searchEngine.doSearchQuery(query, "image");
			int i = 0;
			List<DocInfo> resluts = searchEngine.getResults();
			if (resluts.size() > 100) {
				resluts = resluts.subList(0, 101);
			}
			for (DocInfo docInfo : resluts) {
				if (i % 10 == 0) {
					out.println("<div style=\"width:830px; background-color:white; height:320px; overflow:auto;\">");
					out.println("<div style=\"width: 2000px; height: 290px;\">");
				}
				out.println("<img src=\"" + docInfo.url + "\" width=\"160\" height=\"90\" alt=\"bbc news special\" />");
				if (i % 10 == 9 || i == resluts.size() - 1) {
					out.println("</div></div>");
				}
				i++;
			}
			break;
		case "weather":
			searchEngine.doSearchQuery(query, "weather");
			Hashtable<String, String> weatherTable = searchEngine.getWeatherTable();
			if (weatherTable.isEmpty()) {
				out.println("<p>OOPS! No weather information for your search!</p>");
			} else {
				out.println("<p>" + weatherTable.get("weather") + " " + weatherTable.get("temp_f")+" Fahrenheit");
			}

		default:
			break;
		}

		out.println("</body></html>");
	}

	@Override
	public void init() throws ServletException {
		String filePath = "/home/ubuntu/big.txt";
		SpellCheck.readDict(filePath);
	}

}
