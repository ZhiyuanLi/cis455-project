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
import edu.upenn.cis455.storage.SingleWord;
import edu.upenn.cis455.storage.SingleWordContent;

public class SearchEngine {

	private String query;
	private DynamoDBWrapper db;
	private QueryWeightComputation queryWeightComputation;
	private ArrayList<String> singleWordList;
	private ArrayList<String> biWordList;
	private Hashtable<String, Double> singleWordWeight;
	private Hashtable<String, Double> biWordWeight;
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

	public void setQuery(String query) {
		this.query = query;
		queryWeightComputation.setQuery(query);
		doSingleWordQuery();
		results = new ArrayList<DocInfo>(docList.values());
		Collections.sort(results);
	}

	/**
	 * helper method to do single word query
	 */
	private void doSingleWordQuery() {
//		formSingleWordDocList();
//		formSingleWordContentDocList();
		formBiWordContentDocList();
	}

	/**
	 * helper method to generate single word doc list
	 */
	private void formSingleWordContentDocList() {
		singleWordList = queryWeightComputation.getSingleWordList();
		// int length = singleWordList.size();
		// int i = 0;// word order
		for (String word : singleWordList) {
			List<SingleWordContent> items = db.getSingleWordContentQuery(word);
			if (!items.isEmpty()) {
				// take idf into word weight
				queryWeightComputation.setSingleWordIdf(word, items.get(0).getIdf());
				updateSingleWordWeight();
				for (SingleWordContent item : items) {
					String url = item.getUrl();
					// TODO: get pagerank scores
					DocInfo docInfo = docList.get(url);
					if (docInfo == null) {
						docInfo = new DocInfo();
					}
					// docInfo.tf_idfArray[i] = item.getTf_idf();
					docInfo.url = url;
					docInfo.indexScore += item.getTf_idf() * singleWordWeight.get(word);
					// System.out.println(item.getUrl() +
					// Arrays.toString(docInfo.tf_idfArray));
					docList.put(url, docInfo);
				}
			}
			// i++;
		}

	}

	private void formSingleWordDocList() {
		singleWordList = queryWeightComputation.getSingleWordList();
		// int length = singleWordList.size();
		// int i = 0;// word order
		for (String word : singleWordList) {
			List<SingleWord> items = db.getSingleWordQuery(word);
			if (!items.isEmpty()) {
				// take idf into word weight
				queryWeightComputation.setSingleWordIdf(word, items.get(0).getIdf());
				updateSingleWordWeight();
				for (SingleWord item : items) {
					String url = item.getUrl();
					// TODO: get pagerank scores
					DocInfo docInfo = docList.get(url);
					if (docInfo == null) {
						docInfo = new DocInfo();
					}
					// docInfo.tf_idfArray[i] = item.getTf_idf();
					docInfo.url = url;
					docInfo.indexScore += item.getTf_idf() * singleWordWeight.get(word);
					// System.out.println(item.getUrl() +
					// Arrays.toString(docInfo.tf_idfArray));
					docList.put(url, docInfo);
				}
			}
			// i++;
		}

	}
	
	private void formBiWordContentDocList() {
		biWordList = queryWeightComputation.getBiWordList();
		// int length = singleWordList.size();
		// int i = 0;// word order
		for (String word : biWordList) {
			List<BiWordContent> items = db.getBiWordContentQuery(word);
			if (!items.isEmpty()) {
				// take idf into word weight
				queryWeightComputation.setBiWordIdf(word, items.get(0).getIdf());
				updateBiWordWeight();
				for (BiWordContent item : items) {
					String url = item.getUrl();
					// TODO: get pagerank scores
					DocInfo docInfo = docList.get(url);
					if (docInfo == null) {
						docInfo = new DocInfo();
					}
					// docInfo.tf_idfArray[i] = item.getTf_idf();
					docInfo.url = url;
					docInfo.indexScore += item.getTf_idf() * biWordWeight.get(word);
					// System.out.println(item.getUrl() +
					// Arrays.toString(docInfo.tf_idfArray));
					docList.put(url, docInfo);
				}
			}
			// i++;
		}

	}

	/**
	 * helper method to get newest SingleWordWeight
	 */
	private void updateSingleWordWeight() {
		singleWordWeight = queryWeightComputation.getSingleWordWeight();
	}
	/**
	 * helper method to get newest BiWordWeight
	 */
	private void updateBiWordWeight() {
		biWordWeight = queryWeightComputation.getBiWordWeight();
	}

	public static void main(String[] args) {
		SearchEngine engine = new SearchEngine();
		engine.setQuery("inspired bible");
		for (DocInfo docInfo : engine.results) {
			System.out.println(docInfo.url + "" + docInfo.indexScore);
		}
	}
}
