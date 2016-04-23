package edu.upenn.cis455.search;

import java.io.IOException;
import java.util.*;

import edu.upenn.cis455.indexer.IndexerDriver;
import edu.upenn.cis455.indexer.Tokenizer;
import edu.upenn.cis455.indexer.WordProcessor;

/**
 * Calculate query weight
 * 
 * @author zhiyuanli
 *
 */
public class QueryWeightComputation {

	private ArrayList<String> singleWordList;
	private Hashtable<String, Double> singleWordWeight;
	private ArrayList<String> biWordList;
	private Hashtable<String, Double> biWordWeight;

	/**
	 * Constructor
	 */
	public QueryWeightComputation() {
		// TODO Auto-generated constructor stub
		singleWordList = new ArrayList<String>();
		singleWordWeight = new Hashtable<String, Double>();
		biWordList = new ArrayList<String>();
		biWordWeight = new Hashtable<String, Double>();
	}

	/**
	 * set query
	 * 
	 * @param query
	 */
	public void setQuery(String query) {
		try {
			setSinglewordQueryMap(query);
			setBiwordQueryMap(query);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @return the singleWordList
	 */
	public ArrayList<String> getSingleWordList() {
		return singleWordList;
	}

	/**
	 * @return the biWordList
	 */
	public ArrayList<String> getBiWordList() {
		return biWordList;
	}

	/**
	 * @return the singleWordWeight
	 */
	public Hashtable<String, Double> getSingleWordWeight() {
		return singleWordWeight;
	}

	/**
	 * @return the biWordWeight
	 */
	public Hashtable<String, Double> getBiWordWeight() {
		return biWordWeight;
	}

	/**
	 * helper method to calculate each single word query weight
	 * 
	 * @param query
	 * @throws IOException
	 */
	private void setSinglewordQueryMap(String query) throws IOException {

		double max = 1.0;
		Tokenizer tokenizer = new Tokenizer(query);
		while (tokenizer.hasNext()) {
			String word = WordProcessor.preProcess(tokenizer.nextToken());
			if (!word.equals("")) {
				String pWord = WordProcessor.process(word.toLowerCase());
				if (pWord != null) {
					Double frequecy = 1.0;
					if (singleWordWeight.containsKey(pWord)) {
						frequecy = singleWordWeight.get(pWord) + 1.0;
						if (frequecy > max) {
							max = frequecy;
						}
					}
					singleWordWeight.put(pWord, frequecy);
				}

			}
		}

		for (String word : singleWordWeight.keySet()) {
			double tf = IndexerDriver.TF_FACTOR + (1 - IndexerDriver.TF_FACTOR) * (singleWordWeight.get(word) / max);
			singleWordList.add(word);
			singleWordWeight.put(word, tf);
		}

		System.out.println(singleWordWeight);
	}

	/**
	 * helper method to calculate each biword query weight
	 * 
	 * @param query
	 * @throws IOException
	 */
	private void setBiwordQueryMap(String query) throws IOException {

		double max = 1.0;
		Tokenizer tokenizer = new Tokenizer(query);
		String preWord = "";
		String concatWord = "";
		while (tokenizer.hasNext()) {
			String word = WordProcessor.preProcess(tokenizer.nextToken());
			if (!word.equals("")) {
				if (preWord.equals("")) {
					preWord = word;
					continue;
				} else {
					concatWord = WordProcessor.concat(preWord, word);
					preWord = word;
				}
				String pWord = WordProcessor.process(concatWord.toLowerCase());
				if (pWord != null) {
					Double frequecy = 1.0;
					if (biWordWeight.containsKey(pWord)) {
						frequecy = biWordWeight.get(pWord) + 1.0;
						if (frequecy > max) {
							max = frequecy;
						}
					}
					biWordWeight.put(pWord, frequecy);
				}
			}
		}
		for (String word : biWordWeight.keySet()) {
			double tf = IndexerDriver.TF_FACTOR + (1 - IndexerDriver.TF_FACTOR) * (biWordWeight.get(word) / max);
			biWordList.add(word);
			biWordWeight.put(word, tf);
		}
	}

	/**
	 * update word weight by idf
	 * 
	 * @param word
	 * @param idf
	 */
	public void setSingleWordIdf(String word, double idf) {
		singleWordWeight.put(word, singleWordWeight.get(word) * idf);
	}
	
	/**
	 * update word weight by idf
	 * 
	 * @param word
	 * @param idf
	 */
	public void setBiWordIdf(String word, double idf) {
		biWordWeight.put(word, biWordWeight.get(word) * idf);
	}

	// public Hashtable<String, Double> getDoclist() {
	// if (biwordQueryMap.isEmpty()) {
	// // TODO: randomly return 100 pages;
	// } else if (biwordQueryMap.size() == 1) {
	// // TODO: get first 100 pages;
	// } else {
	//
	// }
	// return null;
	// }

	// public static void main(String[] args) throws IOException {

	// IndexerComputation ic = new IndexerComputation();
	// ic.setQuery("hello word! penn: you are the best, haha-is that true,
	// penn yeas");
	// ic.normalizeQuery();

	// ic.setQuery("hello");
	// ic.normalizeQuery();
	// }

}
