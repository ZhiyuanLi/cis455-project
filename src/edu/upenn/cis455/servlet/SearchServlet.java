package edu.upenn.cis455.servlet;

import java.io.File;
import java.io.IOException;

import javax.servlet.RequestDispatcher;
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

	private String jspResultPath;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		dispatchReply(req, resp);
	}

	@Override
	public void init() throws ServletException {
		// set worker status for this worker

	}

	public void dispatchReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		RequestDispatcher view = req.getRequestDispatcher(jspResultPath);
		view.forward(req, resp);

	}
}
