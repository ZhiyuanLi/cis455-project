package edu.upenn.cis455.search;

import java.util.*;

import edu.upenn.cis455.storage.DynamoDBWrapper;
import edu.upenn.cis455.storage.ImageContent;
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
	private String state, city;
	private ArrayList<String> queryWords;
	private Hashtable<String, DocInfo> docList;
	private ArrayList<DocInfo> results;
	private String query;
	private Weather weather;
	public double queryTime;
	public int numberItemRetrived;
	private Hashtable<String, String> weatherTable;

	/**
	 * Constructor for SearchEngineMultiThread
	 */
	public SearchEngineMultiThread() {
		try {
			db = new DynamoDBWrapper();
			qComputer = new QueryComputer();
			docList = new Hashtable<String, DocInfo>();
			queryTime = 0.0;
			numberItemRetrived = 0;
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
	public void doSearchQuery(String queryS, String searchType, String state, String city) {
		System.gc();
		long startTime = System.currentTimeMillis();

		// 1. set query to query computer
		this.query = queryS;
		qComputer.setQuery(queryS);

		// 2. get query words from query
		queryWords = qComputer.getQueryWords();

		// 3. get query size from query computer
		querySize = qComputer.getQuerySize();

		// 4. set geolocation
		this.state = state;
		this.city = city;

		// 5. issue thread to get every word doc list and compute the score for
		// each doc
		try {
			issueThreads(searchType);
		} catch (InterruptedException e) {
			System.out.println("@ doSearchQuery");
		}

		// 6. sort doc list by its total score
		for (DocInfo docInfo : docList.values()) {
			docInfo.calculateTotalScore();
		}

		// 7. get doc list
		results = new ArrayList<DocInfo>(docList.values());
		numberItemRetrived = results.size();

		// 8. sort doc list by score
		Collections.sort(results);
		long endTime = System.currentTimeMillis();
		queryTime = (double) (endTime - startTime) / 1000;
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
			issueWordTitleThread();
			issueWordContentThread();
			break;

		case "image":
			issueImageThread();
			break;

		case "weather":
			weather = new Weather();
			weatherTable = weather.getWeather(query, "us");
			break;
		}
	}

	/**
	 * @return the weatherTable
	 */
	public Hashtable<String, String> getWeatherTable() {
		return weatherTable;
	}

	/**
	 * @param weatherTable
	 *            the weatherTable to set
	 */
	public void setWeatherTable(Hashtable<String, String> weatherTable) {
		this.weatherTable = weatherTable;
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
		System.out.println("Content retrieved");

		// 4. compute doc list score
		List<SingleWordContent> items;
		for (i = 0; i < querySize; i++) {
			items = contentWorkers[i].getContentItems();
			if (!items.isEmpty()) {
				word = contentWorkers[i].getWord();
				computeContentDoc(word, items);
			}
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
		System.out.println("Title retrived");
		// 4. compute doc list score
		List<SingleWordTitle> items;
		for (i = 0; i < querySize; i++) {
			items = titleWorkers[i].getTitleItems();
			if (!items.isEmpty()) {
				word = titleWorkers[i].getWord();
				computeTitleDoc(word, items);
			}
		}
	}

	private void issueImageThread() throws InterruptedException {
		// 0. declare variables to help to issueThreads
		int i = 0;
		// 1. open a thread pool, open contentWorkers
		Thread[] threadPool = new Thread[querySize];
		ImageSearchWorker[] imageWorkers = new ImageSearchWorker[querySize];

		// 2. start thread
		// 2.1 declare variable to help start thread
		ImageSearchWorker isw;
		String word;
		// 2.2 add thread to pool, start thread
		for (i = 0; i < querySize; i++) {
			word = queryWords.get(i);
			isw = new ImageSearchWorker(db, word);
			imageWorkers[i] = isw;
			threadPool[i] = new Thread(isw);
			threadPool[i].start();
		}

		// 3. join thread, wait all thread to get items back
		for (i = 0; i < querySize; i++) {
			threadPool[i].join();
		}

		// 4. compute doc list score
		List<ImageContent> items;
		for (i = 0; i < querySize; i++) {
			items = imageWorkers[i].getImageItems();
			if (!items.isEmpty()) {
				word = imageWorkers[i].getWord();
				computeImageDoc(word, items);
			}
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
				docInfo = new DocInfo(querySize, queryWords, url);
				docInfo.title = WordTitle.getTitle(url);
				docInfo.pagerankScore = PageRank.getRank(url);
				docInfo.qState = state;
				docInfo.dState = PageRank.getState(url);
				docInfo.qCity = city;
				docInfo.dCity = PageRank.getCity(url);

			}
			docInfo.addWord(word, queryWordInfo.getPosition(), hits, false);
			docInfo.indexDocScore += item.getTf_idf() * queryWordInfo.getWeight();
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
				docInfo = new DocInfo(querySize, queryWords, url);
				docInfo.title = WordTitle.getTitle(url);
				docInfo.queryInTitle = true;
				docInfo.pagerankScore = PageRank.getRank(url);
				docInfo.qState = state;
				docInfo.dState = PageRank.getState(url);
				docInfo.qCity = city;
				docInfo.dCity = PageRank.getCity(url);
			}
			docInfo.addWord(word, queryWordInfo.getPosition(), hits, true);
			docInfo.indexTitleScore += item.getTf_idf() * queryWordInfo.getWeight();
			docList.put(url, docInfo);
		}

	}

	/**
	 * This method is used to compute doc list score for a word
	 * 
	 * @param word
	 * @param items
	 */
	private void computeImageDoc(String word, List<ImageContent> items) {
		// 0. declare variable that help to compute doc info
		String url, hits;
		QueryWordInfo queryWordInfo;

		// 1. compute this word idf
		qComputer.setQueryWordIdf(word, items.get(0).getIdf());

		// 2. get this word query info
		queryWordInfo = qComputer.getQueryWordInfo(word);

		// 3. do computation for each doc
		for (ImageContent item : items) {
			url = item.getUrl();
			hits = item.getHits();
			DocInfo docInfo = docList.get(url);
			if (docInfo == null) {
				docInfo = new DocInfo(querySize, queryWords, url);
				docInfo.title = WordTitle.getTitle(url);
				docInfo.qState = state;
				docInfo.pagerankScore = PageRank.getRank(url);
				docInfo.dState = PageRank.getState(url);
				docInfo.qCity = city;
				docInfo.dCity = PageRank.getCity(url);
			}
			docInfo.addWord(word, queryWordInfo.getPosition(), hits, false);
			docInfo.indexDocScore += item.getTf_idf() * queryWordInfo.getWeight();
			docList.put(url, docInfo);
		}

	}

	/**
	 * 
	 * @return the results
	 */
	public ArrayList<DocInfo> getResults() {
		return results;
	}

	// public static void main(String[] args) {
	// PageRank.loadPageRank("pagerank");
	//
	// WordTitle.loadWordTitle("title");
	// SearchEngineMultiThread engine = new SearchEngineMultiThread();
	// System.gc();
	// long time1 = System.currentTimeMillis();
	// engine.doSearchQuery("Cornell Names Robert ", "word", "pennsylvania",
	// "philadelphia");
	// long time2 = System.currentTimeMillis();
	// engine.queryTime = time2 - time1;
	// System.out.println(Arrays.toString(engine.queryWords.toArray()));
	// int i = 0;
	// for (DocInfo docInfo : engine.results) {
	// if (i < 300) {
	// i++;
	// System.out.println(docInfo.url + ":" + docInfo.hostName + ":" +
	// docInfo.wordNumberInUrlHost + ":"
	// + docInfo.wordNumberInUrl + ":" + docInfo.totalScore);
	// System.out.println();
	// }
	// }
	// }
}
