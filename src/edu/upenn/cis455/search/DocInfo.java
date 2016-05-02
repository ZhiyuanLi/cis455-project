package edu.upenn.cis455.search;

import java.util.PriorityQueue;
import java.util.Queue;

/**
 * this class stores each word info for one doc
 * 
 * @author zhiyuanli
 *
 */
public class DocInfo implements Comparable<DocInfo> {

	public String url;
	// protected String query;
	protected int querySize;
	protected int wordNumberInDoc;
	protected Queue<WordInfo> wordQueue;
	protected StringBuilder wordsInOrder;
	protected double indexScore;
	protected double pagerankScore;
	public double totalScore;

	public DocInfo(int querySize) {
		// this.query = query;
		this.querySize = querySize;
		this.wordNumberInDoc = 0;
		url = "";
		wordQueue = new PriorityQueue<WordInfo>();
		wordsInOrder = new StringBuilder();
		indexScore = 0;
		pagerankScore = 0;
		totalScore = 0;
	}

	/**
	 * get the total score
	 */
	public void calculateTotalScore() {
		int orderScore = getWordsInOrderDiff();
		if (querySize != 1) {
			indexScore = indexScore * wordNumberInDoc * 0.5 + indexScore * wordNumberInDoc * 0.5 / orderScore;
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
		wordNumberInDoc++;
		String[] temp = hits.split(",");
		WordInfo w;
		for (int i = 0; i < temp.length; i++) {
			w = new WordInfo(word, position, Integer.parseInt(temp[i]));
			wordQueue.offer(w);
		}
	}
	//
	// /**
	// * get words in order as a whole string
	// *
	// * @return
	// */
	// private String getWordsInOrder() {
	// while (!wordQueue.isEmpty()) {
	// wordsInOrder.append(wordQueue.poll().word).append(" ");
	// }
	// return wordsInOrder.toString().trim();
	// }

	private int getWordsInOrderDiff() {
		int diff = 0;
		boolean inOrder = false;
		WordInfo prev;
		WordInfo cur;
		while ((wordQueue.size() >= 3)) {
			prev = wordQueue.poll();
			cur = wordQueue.poll();
			if (cur.position > prev.position) {
				diff += cur.hit - prev.hit;
				inOrder = true;
			}
			prev = cur;
			cur = wordQueue.poll();
		}
		if (wordQueue.size() == 2) {
			prev = wordQueue.poll();
			cur = wordQueue.poll();
			if (cur.position > prev.position) {
				diff += cur.hit = prev.hit;
				inOrder = true;
			}
		} else if (wordQueue.size() == 1) {
			inOrder = false;
		}
		if (inOrder == false) {
			return Integer.MAX_VALUE;
		}
		return diff;
	}

	// /**
	// * check the doc preserve words in order
	// *
	// * @return
	// */
	// private boolean isContaninsQuery() {
	// return getWordsInOrder().contains(query);
	// }

	@Override
	public int compareTo(DocInfo o) {
		// TODO: change to totalscore
		double own = this.totalScore;

		double other = o.totalScore;
		// descending order
		if (own > other) {
			return -1;
		} else if (own < other) {
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * inner class to store word info
	 * 
	 * @author zhiyuanli
	 *
	 */
	private class WordInfo implements Comparable<WordInfo> {

		protected String word;
		protected int position;
		protected int hit;

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
				return hit < o.hit ? -1 : 1;
			}
		}

	}

	// public static void main(String[] args) {
	// DocInfo d = new DocInfo("I love apple pie");
	// d.addWord("apple", "10,27,80");
	// d.addWord("pie", "-1,13,200");
	// System.out.println(d.getWordsInOrder());
	// }
}
