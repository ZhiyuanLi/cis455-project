package edu.upenn.cis455.search;

import java.util.*;

import edu.upenn.cis455.storage.DynamoDBWrapper;
import edu.upenn.cis455.storage.SingleWordContent;
import edu.upenn.cis455.storage.SingleWordTitle;

/**
 * SearchEngineMultiThread do search using multi-thread
 * 
 * @author Di Wu
 * @author Zhiyuan Li
 *
 */
public class SearchEngineMultiThread {

	/**
	 * Instance for SearchEngineMultiThread
	 */
	private DynamoDBWrapper db;
	protected QueryComputer qComputer;
	private int querySize;
	private ArrayList<String> queryWords;
	private Hashtable<String, DocInfo> docList;
	protected ArrayList<DocInfo> results;

	/**
	 * Constructor for SearchEngineMultiThread
	 */
	public SearchEngineMultiThread() {
		try {
			db = new DynamoDBWrapper();
			qComputer = new QueryComputer();
			docList = new Hashtable<String, DocInfo>();
		} catch (Exception e) {
			System.out.println("@ SearchEngineMultiThread");
		}
	}

	/**
	 * Do search query
	 * 
	 * @param queryS
	 *            query to be search
	 */
	public void doSearchQuery(String queryS, String searchType) {
		// 1. set query to query computer
		qComputer.setQuery(queryS);

		// 2. get query words from query
		queryWords = qComputer.getQueryWords();

		// 3. get query size from query computer
		querySize = qComputer.getQuerySize();

		// 4. issue thread to get every word doc list and compute the score for
		// each doc
		try {
			issueThreads(searchType);
		} catch (InterruptedException e) {
			System.out.println("@ doSearchQuery");
		}

		// 5. get doc list
		results = new ArrayList<DocInfo>(docList.values());

		// 6. sort doc list by score
		Collections.sort(results);
	}

	/**
	 * This method is used to open a thread for each word to get items
	 * 
	 * @param isContent
	 * @throws InterruptedException
	 */
	public void issueThreads(String searchType) throws InterruptedException {
		switch (searchType) {
		case "word":
			issueWordContentThread();
			issueWordTitleThread();
			break;

		case "image":
			issueImageThread();
			break;
		}

	}

	private void issueWordContentThread() throws InterruptedException {
		// 0. declare variables to help to issueThreads
		int i = 0;
		// 1. open a thread pool, open contentWorkers
		Thread[] threadPool = new Thread[querySize];
		ContentSearchWorker[] contentWorkers = new ContentSearchWorker[querySize];

		// 2. start thread
		// 2.1 declare variable to help start thread
		ContentSearchWorker csw;
		String word;
		// 2.2 add thread to pool, start thread
		for (i = 0; i < querySize; i++) {
			word = queryWords.get(i);
			csw = new ContentSearchWorker(db, word);
			contentWorkers[i] = csw;
			threadPool[i] = new Thread(csw);
			threadPool[i].start();
		}

		// 3. join thread, wait all thread to get items back
		for (i = 0; i < querySize; i++) {
			threadPool[i].join();
		}

		// 4. compute doc list score
		List<SingleWordContent> items;
		for (i = 0; i < querySize; i++) {
			items = contentWorkers[i].getContentItems();
			if (!items.isEmpty()) {
				word = contentWorkers[i].getWord();
				computeContentDoc(word, items);
			}
		}

		// 5. sort doc list by its total score
		for (DocInfo docInfo : docList.values()) {
			docInfo.calculateTotalScore();
		}
	}

	private void issueWordTitleThread() throws InterruptedException {
		// 0. declare variables to help to issueThreads
		int i = 0;
		// 1. open a thread pool, open contentWorkers
		Thread[] threadPool = new Thread[querySize];
		TitleSearchWorker[] titleWorkers = new TitleSearchWorker[querySize];

		// 2. start thread
		// 2.1 declare variable to help start thread
		TitleSearchWorker tsw;
		String word;
		// 2.2 add thread to pool, start thread
		for (i = 0; i < querySize; i++) {
			word = queryWords.get(i);
			tsw = new TitleSearchWorker(db, word);
			titleWorkers[i] = tsw;
			threadPool[i] = new Thread(tsw);
			threadPool[i].start();
		}

		// 3. join thread, wait all thread to get items back
		for (i = 0; i < querySize; i++) {
			threadPool[i].join();
		}

		// 4. compute doc list score
		List<SingleWordTitle> items;
		for (i = 0; i < querySize; i++) {
			items = titleWorkers[i].getTitleItems();
			if (!items.isEmpty()) {
				word = titleWorkers[i].getWord();
				computeTitleDoc(word, items);
			}
		}

		// 5. sort doc list by its total score
		for (DocInfo docInfo : docList.values()) {
			docInfo.calculateTotalScore();
		}
	}

	private void issueImageThread() throws InterruptedException {
		// 0. declare variables to help to issueThreads
		int i = 0;
		// 1. open a thread pool, open contentWorkers
		Thread[] threadPool = new Thread[querySize];
		TitleSearchWorker[] titleWorkers = new TitleSearchWorker[querySize];

		// 2. start thread
		// 2.1 declare variable to help start thread
		ContentSearchWorker csw;
		TitleSearchWorker tsw;
		String word;
		// 2.2 add thread to pool, start thread
		for (i = 0; i < querySize; i++) {
			word = queryWords.get(i);
			tsw = new TitleSearchWorker(db, word);
			titleWorkers[i] = tsw;
			threadPool[i] = new Thread(tsw);
			threadPool[i].start();
		}

		// 3. join thread, wait all thread to get items back
		for (i = 0; i < querySize; i++) {
			threadPool[i].join();
		}

		// 4. compute doc list score
		List<SingleWordTitle> items;
		for (i = 0; i < querySize; i++) {
			items = titleWorkers[i].getTitleItems();
			if (!items.isEmpty()) {
				word = titleWorkers[i].getWord();
				computeTitleDoc(word, items);
			}
		}

		// 5. sort doc list by its total score
		for (DocInfo docInfo : docList.values()) {
			docInfo.calculateTotalScore();
		}
	}

	/**
	 * This method is used to compute doc list score for a word
	 * 
	 * @param word
	 * @param items
	 */
	private void computeContentDoc(String word, List<SingleWordContent> items) {
		// 0. declare variable that help to compute doc info
		String url, hits;
		QueryWordInfo queryWordInfo;

		// 1. compute this word idf
		qComputer.setQueryWordIdf(word, items.get(0).getIdf());

		// 2. get this word query info
		queryWordInfo = qComputer.getQueryWordInfo(word);

		// 3. do computation for each doc
		for (SingleWordContent item : items) {
			url = item.getUrl();
			hits = item.getHits();
			DocInfo docInfo = docList.get(url);
			if (docInfo == null) {
				docInfo = new DocInfo(querySize, url);
				docInfo.pagerankScore = db.getPageRankScore(url);
			}
			docInfo.addWord(word, queryWordInfo.getPosition(), hits);
			docInfo.indexScore += item.getTf_idf() * queryWordInfo.getWeight();
			docList.put(url, docInfo);
		}
	}

	/**
	 * This method is used to compute doc list score for a word
	 * 
	 * @param word
	 * @param items
	 */
	private void computeTitleDoc(String word, List<SingleWordTitle> items) {
		// 0. declare variable that help to compute doc info
		String url, hits;
		QueryWordInfo queryWordInfo;

		// 1. compute this word idf
		qComputer.setQueryWordIdf(word, items.get(0).getIdf());

		// 2. get this word query info
		queryWordInfo = qComputer.getQueryWordInfo(word);

		// 3. do computation for each doc
		for (SingleWordTitle item : items) {
			url = item.getUrl();
			hits = item.getHits();
			DocInfo docInfo = docList.get(url);
			if (docInfo == null) {
				docInfo = new DocInfo(querySize, url);
				docInfo.pagerankScore = db.getPageRankScore(url);
			}
			docInfo.addWord(word, queryWordInfo.getPosition(), hits);
			docInfo.indexScore += item.getTf_idf() * queryWordInfo.getWeight();
			docList.put(url, docInfo);
		}
	}

	public static void main(String[] args) {
		SearchEngineMultiThread engine = new SearchEngineMultiThread();
		engine.doSearchQuery("fast food", "word");
		for (DocInfo docInfo : engine.results) {
			System.out.println(docInfo.url + "" + docInfo.totalScore);
		}
	}
}
