package edu.upenn.cis455.search;

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
	protected int querySize;
	protected int wordNumberInDoc;
	protected Queue<WordInfo> wordQueue;
	protected double indexScore;
	protected double pagerankScore;
	public double totalScore;

	/**
	 * constructor
	 * 
	 * @param querySize
	 * @param url
	 */
	public DocInfo(int querySize, String url) {
		this.querySize = querySize;
		this.url = url;
		this.wordNumberInDoc = 0;
		this.wordQueue = new PriorityQueue<WordInfo>();
		this.indexScore = 0;
		this.pagerankScore = 0;
		this.totalScore = 0;
	}

	/**
	 * get the total score
	 */
	public void calculateTotalScore() {
		int diffInOrder = getWordsDiffInOrder();
		if (querySize != 1) {
			// TODO: more tunning!!! & add pagerank
			indexScore = indexScore * wordNumberInDoc * 0.5 + indexScore * wordNumberInDoc * 0.5 / diffInOrder;
		}
		totalScore = indexScore + pagerankScore;
	}

	/**
	 * add word and its hits
	 * 
	 * @param word
	 * @param hits
	 */
	public void addWord(String word, int position, String hits) {
		// 1. update query word number occurs in doc
		wordNumberInDoc++;
		// 2. add word info to word queue
		// 2.1 declare variable to help add word info
		WordInfo w;
		// 2.2 create word info for every position and add it to word queue
		String[] temp = hits.split(",");
		for (int i = 0; i < temp.length; i++) {
			w = new WordInfo(word, position, Integer.parseInt(temp[i]));
			wordQueue.offer(w);
		}
	}

	/**
	 * get words hits difference in order
	 * 
	 * @return words hits difference
	 */
	private int getWordsDiffInOrder() {
		// 0. initialize variables to help get word diff in order
		int diff = 0;
		boolean inOrder = false;
		WordInfo prev;
		WordInfo cur;
		
		// 1. if the word queue has more than 3 words
		while ((wordQueue.size() >= 3)) {
			prev = wordQueue.poll();
			cur = wordQueue.poll();
			// calculate closest distinct words difference
			if (cur.position > prev.position) {
				diff += (cur.hit - prev.hit);
				inOrder = true;
			}
			// move to next
			prev = cur;
			cur = wordQueue.poll();
		}
		
		// 2. after setp1 or word queue originally has size equals 2
		if (wordQueue.size() == 2) {
			prev = wordQueue.poll();
			cur = wordQueue.poll();
			// calculate closest distinct words difference
			if (cur.position > prev.position) {
				diff += (cur.hit - prev.hit);
				inOrder = true;
			}
		} 
		
		// 3. word queue originally has size equals 1
		else if (wordQueue.size() == 1) {	
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
			return 0;
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
