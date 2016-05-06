package edu.upenn.cis455.search;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * This class stores each query word info for one doc
 * 
 * @author Zhiyuan Li
 * @author Di Wu
 *
 */
public class DocInfo implements Comparable<DocInfo> {

	/**
	 * Instance of DocInfo
	 */
	public String url;
	protected String normalUrl, hostName;
	protected int querySize;
	protected ArrayList<String> queryWords;
	public String title;
	public String queryGeoLocation, docGeoLocation;
	protected boolean queryInTitle;
	public int wordNumberInUrlHost, wordNumberInUrl, wordNumberInTitle, wordNumberInDoc;
	protected Queue<WordInfo> wordTitleQueue, wordDocQueue;
	protected double indexTitleScore, indexDocScore, pagerankScore;
	public double totalScore;

	/**
	 * constructor
	 * 
	 * @param querySize
	 * @param url
	 * @throws MalformedURLException
	 */
	public DocInfo(int querySize, ArrayList<String> queryWords, String url) {
		this.querySize = querySize;
		this.queryWords = queryWords;
		this.url = url;
		this.normalUrl = url.replace(":80", "").replace(":443", "").replace("http://", "").replace("https://", "");
		this.hostName = normalUrl.substring(0, normalUrl.indexOf("/"));
		this.docGeoLocation = "";
		this.wordNumberInUrlHost = 0;
		this.wordNumberInUrl = 0;
		this.wordNumberInTitle = 0;
		this.wordNumberInDoc = 0;
		this.wordTitleQueue = new PriorityQueue<WordInfo>();
		this.wordDocQueue = new PriorityQueue<WordInfo>();
		this.queryInTitle = false;
		this.title = "";
		this.indexTitleScore = 0;
		this.indexDocScore = 0;
		this.pagerankScore = 1;
		this.totalScore = 0;
	}

	/**
	 * get the total score
	 */
	public void calculateTotalScore() {
		for (String word : queryWords) {
			if (hostName.contains(word)) {
				wordNumberInUrlHost++;
			}
			if (url.contains(word)) {
				wordNumberInUrl++;
			}
		}
		int diffTitleInOrder = getWordsDiffInOrder(wordTitleQueue);
		int diffDocInOrder = getWordsDiffInOrder(wordDocQueue);
		double hostScore = 0, urlScore = 0, geoScore = 0;
		if (querySize != 1) {
			indexDocScore = indexDocScore * wordNumberInDoc * 0.05
					+ indexDocScore * wordNumberInDoc * 0.05 / diffDocInOrder;
			indexTitleScore = (indexTitleScore * wordNumberInTitle * 0.05
					+ indexTitleScore * wordNumberInTitle * 0.05 / diffTitleInOrder) * 100;
		}
		if (querySize == wordNumberInUrl) {
			if (wordNumberInUrlHost > 0) {
				hostScore = 100 * (1 + wordNumberInUrlHost);
				if (wordNumberInUrl > 0) {
					urlScore = 1 * (1 + wordNumberInUrl);
				}
			}
		}
		if (docGeoLocation.equalsIgnoreCase(queryGeoLocation)) {
			geoScore = 50;
		}
		totalScore = ((indexDocScore + indexTitleScore + hostScore + urlScore + geoScore)) + pagerankScore;
	}

	/**
	 * add word and its hits
	 * 
	 * @param word
	 * @param hits
	 */
	public void addWord(String word, int position, String hits, boolean isTitle) {
		if (isTitle) {
			// 1. update query word number occurs in doc
			wordNumberInTitle++;
			// 2. add word info to word queue
			// 2.1 declare variable to help add word info
			WordInfo w;
			// 2.2 create word info for every position and add it to word queue
			String[] temp = hits.split(",");
			for (int i = 0; i < temp.length; i++) {
				w = new WordInfo(word, position, Integer.parseInt(temp[i]));
				wordTitleQueue.offer(w);
			}

		} else {
			// 1. update query word number occurs in doc
			wordNumberInDoc++;
			// 2. add word info to word queue
			// 2.1 declare variable to help add word info
			WordInfo w;
			// 2.2 create word info for every position and add it to word queue
			String[] temp = hits.split(",");
			for (int i = 0; i < temp.length; i++) {
				w = new WordInfo(word, position, Integer.parseInt(temp[i]));
				wordDocQueue.offer(w);
			}
		}

	}

	/**
	 * get words hits difference in order
	 * 
	 * @return words hits difference
	 */
	private int getWordsDiffInOrder(Queue<WordInfo> queue) {
		// 0. initialize variables to help get word diff in order
		int diff = 0;
		boolean inOrder = false;
		WordInfo prev;
		WordInfo cur;

		// 1. if the word queue has more than 3 words
		while ((queue.size() >= 3)) {
			prev = queue.poll();
			cur = queue.poll();
			// calculate closest distinct words difference
			if (cur.position > prev.position) {
				diff += (cur.hit - prev.hit);
				inOrder = true;
			}
			// move to next
			prev = cur;
			cur = queue.poll();
		}

		// 2. after setp1 or word queue originally has size equals 2
		if (queue.size() == 2) {
			prev = queue.poll();
			cur = queue.poll();
			// calculate closest distinct words difference
			if (cur.position > prev.position) {
				diff += (cur.hit - prev.hit);
				inOrder = true;
			}
		}

		// 3. word queue originally has size equals 1
		else if (queue.size() == 1) {
			inOrder = false;
		}

		// 4. no words in order ,the order difference is huge
		if (inOrder == false) {
			return Integer.MAX_VALUE;
		}
		return diff;
	}

	@Override
	public int compareTo(DocInfo o) {
		if (totalScore == o.totalScore) {
			// tie breaking cheak length of url
			if (normalUrl.length() == o.normalUrl.length()) {
				return 0;
			}
			return normalUrl.length() > o.normalUrl.length() ? 1 : -1;
		} else {
			// descending order
			return totalScore < o.totalScore ? 1 : -1;
		}
	}

	/**
	 * inner class to store word info
	 * 
	 * @author Zhiyuan Li
	 * @author Di Wu
	 *
	 */
	private class WordInfo implements Comparable<WordInfo> {

		/**
		 * Instance for WordInfo
		 */
		@SuppressWarnings("unused")
		protected String word;

		/**
		 * position in query
		 */
		protected int position;

		/**
		 * hit in doc
		 */
		protected int hit;

		/**
		 * Constructor for WordInfo
		 * 
		 * @param word
		 * @param position
		 * @param hit
		 */
		public WordInfo(String word, int position, int hit) {
			this.word = word;
			this.position = position;
			this.hit = hit;
		}

		@Override
		public int compareTo(WordInfo o) {
			if (hit == o.hit) {
				return 0;
			} else {
				// ascending order
				return hit < o.hit ? -1 : 1;
			}
		}
	}
}
