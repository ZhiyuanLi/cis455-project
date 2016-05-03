package edu.upenn.cis455.search;

import java.util.*;

import edu.upenn.cis455.storage.DynamoDBWrapper;
import edu.upenn.cis455.storage.SingleWordContent;

public class SearchEngineMultiThread {

	private DynamoDBWrapper db;
	protected QueryComputer qComputer;
	private Hashtable<String, QueryWordInfo> queryWordInfoList;
	private Hashtable<String, DocInfo> docList;
	protected ArrayList<DocInfo> results;

	public SearchEngineMultiThread() {
		try {
			db = new DynamoDBWrapper();
			qComputer = new QueryComputer();
			docList = new Hashtable<>();
		} catch (Exception e) {
			System.out.println("@ SearchEngineMultiThread");
		}
	}

	public void setSearchQuery(String queryS) {
		qComputer.setQuery(queryS);
		queryWordInfoList = qComputer.getQueryWordInfoList();
		try {
			issueThreads(true);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		results = new ArrayList<DocInfo>(docList.values());
		Collections.sort(results);
	}

	public void issueThreads(boolean isContent) throws InterruptedException {
		int size = qComputer.getQuerySize();
		Thread[] threadPool = new Thread[size];
		Hashtable<String, ContentSearchWorker> workerList = new Hashtable<String, ContentSearchWorker>();
		ContentSearchWorker c;
		int i = 0;
		// start thread
		for (String word : queryWordInfoList.keySet()) {
			c = new ContentSearchWorker(db, word);
			threadPool[i] = new Thread(c);
			threadPool[i].start();
			workerList.put(word, c);
			i++;
		}
		System.out.println("start");
		// join thread
		for (int j = 0; j < size; j++) {
			threadPool[j].join();
		}
		System.out.println("join end");

		// compute;
		QueryWordInfo qWordInfo;
		List<SingleWordContent> items;
		for (String word : queryWordInfoList.keySet()) {
			qWordInfo = queryWordInfoList.get(word);
			items = workerList.get(word).getContentItems();
			formDoc(qWordInfo, items);
		}
		// sort
		for (DocInfo docInfo : docList.values()) {
			docInfo.calculateTotalScore();
		}
	}

	private void formDoc(QueryWordInfo qWordInfo, List<SingleWordContent> items) {
		String word;
		QueryWordInfo newQueryWordInfo;
		if (!items.isEmpty()) {
			word = qWordInfo.getWord();
			qComputer.setWordWeight(word, items.get(0).getIdf());
			newQueryWordInfo = qComputer.getUpdateWordInfo(word);
			System.out.println(newQueryWordInfo.toString());
			String url, hits;
			for (SingleWordContent item : items) {
				url = item.getUrl();
				hits = item.getHits();
				DocInfo docInfo = docList.get(url);
				if (docInfo == null) {
					docInfo = new DocInfo(qComputer.getQuerySize());
					// TODO: GET PAGE RANK
					docInfo.pagerankScore = db.getPageRankScore(url);
				}
				docInfo.url = url;
				docInfo.addWord(word, newQueryWordInfo.getPosition(), hits);
				docInfo.indexScore += item.getTf_idf() * newQueryWordInfo.getWeight();
				docList.put(url, docInfo);
			}
		}	
	}

	public static void main(String[] args) {
		SearchEngineMultiThread engine = new SearchEngineMultiThread();
		engine.setSearchQuery("home schedule");
		for (DocInfo docInfo : engine.results) {
			System.out.println(docInfo.url + "" + docInfo.totalScore);
		}
	}

}
