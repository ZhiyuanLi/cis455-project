package edu.upenn.cis455.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import com.sun.org.apache.bcel.internal.generic.NEWARRAY;

import edu.upenn.cis455.storage.BiWordContent;
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

//	private String query;
	private DynamoDBWrapper db;
	private QueryWeightComputation queryWeightComputation;
	private ArrayList<String> singleWordList;
	private Hashtable<String, Double> singleWordWeight;
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
//		this.query = queryWeightComputation.getValidQuery();
		singleWordList = queryWeightComputation.getSingleWordList();
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
		for (String word : singleWordList) {
			position++;
			items = db.getSingleWordContentQuery(word);

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
						docInfo = new DocInfo(singleWordList.size());
						docInfo.pagerankScore = db.getPageRankScore(url);
					}
					docInfo.url = url;
					docInfo.addWord(word, position, hits);
					docInfo.indexScore += item.getTf_idf() * singleWordWeight.get(word);
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
		for (String word : singleWordList) {
			position++;
			List<SingleWordTitle> items = db.getSingleWordTitleQuery(word);
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
						docInfo = new DocInfo(singleWordList.size());
						docInfo.pagerankScore = db.getPageRankScore(url);
					}
					docInfo.url = url;
					docInfo.addWord(word, position, hits);
					docInfo.indexScore += item.getTf_idf() * singleWordWeight.get(word);
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
