package edu.upenn.cis455.crawler;
import java.util.LinkedList;
import java.util.Queue;
/**
 * URL Frontier API similar to the one in Mercator Architecture
 * @author weisong
 */
public class URLFrontier
{
	// url queue to store urls that will be evaluated later
	Queue<String> urlQueue = new LinkedList<String>();
	/**
	 * Create a new frontier
	 */
	public URLFrontier()
	{
	}

	/**
	 * add a url to frontier queue
	 * @param urlStart - the new URL to add to the queue
	 */
	public void add(String url)
	{
		urlQueue.offer(url);
	}

	/**
	 * check if frontier queue is empty
	 * @return Returns true if the frontier is empty
	 */
	public boolean isEmpty()
	{
		return urlQueue.isEmpty();
	}

	/**
	 * return the first url in the queue
	 * @return Returns the first item in the queue
	 */
	public String poll()
	{
		return urlQueue.poll();
	}

	/**
	 * Get the URL queue
	 * @return Returns the current queue
	 */
	public Queue<String> getQueue()
	{
		return urlQueue;
	}
}
