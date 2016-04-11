package edu.upenn.cis455.crawler;

import java.util.LinkedList;
import java.util.Queue;

/**
 * URL Frontier API similar to the one in Mercator Architecture
 * 
 * @author weisong
 *
 */
public class URLFrontier {
	// url queue to store urls that will be evaluated later
	Queue<String> urlQueue = new LinkedList<String>();

	public URLFrontier() {
	}

	/**
	 * add a url to frontier queue
	 * 
	 * @param urlStart
	 */
	public void add(String url) {
		urlQueue.offer(url);
	}

	/**
	 * check if frontier queue is empty
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		return urlQueue.isEmpty();
	}

	/**
	 * return the first url in the queue
	 * 
	 * @return
	 */
	public String poll() {
		return urlQueue.poll();
	}

}
