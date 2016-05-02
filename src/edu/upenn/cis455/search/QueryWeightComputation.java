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

	private StringBuilder validQuery;
	private ArrayList<String> singleWordList;
	private Hashtable<String, Double> singleWordWeight;

	/**
	 * Constructor
	 */
	public QueryWeightComputation() {
		// TODO Auto-generated constructor stub
		validQuery = new StringBuilder("");
		singleWordList = new ArrayList<String>();
		singleWordWeight = new Hashtable<String, Double>();
	}

	/**
	 * set query
	 * 
	 * @param query
	 */
	public void setQuery(String query) {
		try {
			setSinglewordQueryMap(query);
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
	 * @return the singleWordWeight
	 */
	public Hashtable<String, Double> getSingleWordWeight() {
		return singleWordWeight;
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
			validQuery.append(word + " ");
			singleWordList.add(word);
			singleWordWeight.put(word, tf);
		}

	}
	


	/**
	 * @return the validQuery
	 */
	public String getValidQuery() {
		return validQuery.toString().trim();
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

}
