package edu.upenn.cis455.search;

import java.util.*;

import edu.upenn.cis455.storage.DynamoDBWrapper;
import edu.upenn.cis455.storage.SingleWordTitle;
import edu.upenn.cis455.storage.SingleWordContent;

/**
 * search engine to get top n results
 * 
 * @author zhiyuanli
 *
 */
public class SearchEngine {

	// private String query;
	private DynamoDBWrapper db;
	private QueryWeightComputation queryWeightComputation;
	private ArrayList<QueryWordInfo> singleWordList;
	private Hashtable<QueryWordInfo, Double> singleWordWeight;
	private Hashtable<String, DocInfo> docList;
	private ArrayList<DocInfo> results;

	public SearchEngine() {
		// TODO Auto-generated constructor stub
		try {
			db = new DynamoDBWrapper();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		queryWeightComputation = new QueryWeightComputation();
		docList = new Hashtable<>();

	}

	/**
	 * set query
	 * 
	 * @param query
	 */
	public void setQuery(String query) {
		// pre calculate
		queryWeightComputation.setQuery(query);
		// this.query = queryWeightComputation.getValidQuery();
		// singleWordList = queryWeightComputation.getSingleWordList();
		// singleWordWeight = queryWeightComputation.getSingleWordWeight();
		doSingleWordQuery();
		results = new ArrayList<DocInfo>(docList.values());
		Collections.sort(results);
	}

	/**
	 * helper method to do single word query
	 */
	private void doSingleWordQuery() {
		// formSingleWordTitleDocList();
		formSingleWordContentDocList();
		// formBiWordContentDocList();
	}

	/**
	 * helper method to generate single word doc list
	 */
	private void formSingleWordContentDocList() {

		List<SingleWordContent> items;
		int position = 0;
		for (QueryWordInfo word : singleWordList) {
			position++;
			items = db.getSingleWordContentQuery(word.getWord());

			if (!items.isEmpty()) {
				// take idf into word weight
				queryWeightComputation.setSingleWordIdf(word, items.get(0).getIdf());
				String url, hits;
				updateSingleWordWeight();

				for (SingleWordContent item : items) {
					url = item.getUrl();
					hits = item.getHits();
					// TODO: get pagerank scores
					DocInfo docInfo = docList.get(url);
					if (docInfo == null) {
						docInfo = new DocInfo(singleWordList.size(), url);
						docInfo.pagerankScore = db.getPageRankScore(url);
					}
					docInfo.url = url;
					docInfo.addWord(word.getWord(), position, hits,false);
					docInfo.indexDocScore += item.getTf_idf() * singleWordWeight.get(word);
					docList.put(url, docInfo);
				}

			}
		}

		for (DocInfo docInfo : docList.values()) {
			docInfo.calculateTotalScore();
		}

	}

	private void formSingleWordTitleDocList() {
		int position = 0;
		for (QueryWordInfo word : singleWordList) {
			position++;
			List<SingleWordTitle> items = db.getSingleWordTitleQuery(word.getWord());
			if (!items.isEmpty()) {
				// take idf into word weight
				queryWeightComputation.setSingleWordIdf(word, items.get(0).getIdf());
				String url, hits;
				updateSingleWordWeight();

				for (SingleWordTitle item : items) {
					url = item.getUrl();
					hits = item.getHits();
					// TODO: get pagerank scores
					DocInfo docInfo = docList.get(url);
					if (docInfo == null) {
						docInfo = new DocInfo(singleWordList.size(), url);
						docInfo.pagerankScore = db.getPageRankScore(url);
					}
					docInfo.addWord(word.getWord(), position, hits,true);
					docInfo.indexTitleScore += item.getTf_idf() * singleWordWeight.get(word);
					docList.put(url, docInfo);
				}

			}
		}

		for (DocInfo docInfo : docList.values()) {
			docInfo.calculateTotalScore();
		}

	}

	/**
	 * helper method to get newest SingleWordWeight
	 */
	private void updateSingleWordWeight() {
		singleWordWeight = queryWeightComputation.getSingleWordWeight();
	}

	/**
	 * @return the results
	 */
	public ArrayList<DocInfo> getResults() {
		return results;
	}

	public static void main(String[] args) {
		SearchEngine engine = new SearchEngine();
		engine.setQuery("food");
		for (DocInfo docInfo : engine.results) {
			System.out.println(docInfo.url + "" + docInfo.totalScore);
		}
	}
}
