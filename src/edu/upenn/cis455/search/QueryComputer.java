package edu.upenn.cis455.search;

import java.io.IOException;
import java.util.*;

import edu.upenn.cis455.indexer.Tokenizer;
import edu.upenn.cis455.indexer.WordProcessor;

/**
 * QueryComputer to compute and store query words info
 * 
 * @author Di Wu
 *
 */
public class QueryComputer {

	/**
	 * Instance variable for QueryComputer
	 */
	private int querySize;
	private ArrayList<String> queryWords;
	private Hashtable<String, QueryWordInfo> queryWordInfoList;

	/**
	 * Constructor for QueryComputer
	 */
	public QueryComputer() {
		querySize = 0;
		queryWords = new ArrayList<String>();
		queryWordInfoList = new Hashtable<String, QueryWordInfo>();
	}

	/**
	 * Set query
	 * 
	 * @param queryS
	 */
	public void setQuery(String queryS) {
		try {

			// 1.initialize variables that help to set query
			int position = 1;
			double maxFreq = 1;
			String word = null;
			Tokenizer tokenizer = new Tokenizer(queryS);
			QueryWordInfo queryWordInfo = null;

			// 2.split query using tokenizer
			while (tokenizer.hasNext()) {
				word = WordProcessor.preProcess(tokenizer.nextToken());
				if (!word.equals("")) {
					word = WordProcessor.process(word.toLowerCase());
					if (word != null) {
						// 2.1 if encounter a new word, querySize++, create a
						// new QueryWordInfo for this word, queryWords add this
						// word
						if (!queryWordInfoList.containsKey(word)) {
							querySize++;
							queryWords.add(word);
							queryWordInfo = new QueryWordInfo(word, position);
						}
						// 2.2 if not a new word, update this word
						// QueryWordInfo, update the maxFreq of this query
						else {
							queryWordInfo = queryWordInfoList.get(word);
							queryWordInfo.addFreq();
							if (maxFreq < queryWordInfo.getWeight()) {
								maxFreq = queryWordInfo.getWeight();
							}
						}
						// 2.3 put word and this QueryWordInfo to
						// queryWordInfoList
						queryWordInfoList.put(word, queryWordInfo);
						// 2.4 update word position
						position++;
					}
				}
			}

			// 3. compute every word tf of this query
			setQueryWordTf(maxFreq);
		} catch (IOException e) {
			System.out.println("@ Set Query");
		}
	}

	/**
	 * This is private method set every word tf in this query
	 * 
	 * @param maxFreq
	 */
	private void setQueryWordTf(double maxFreq) {
		for (String word : queryWordInfoList.keySet()) {
			queryWordInfoList.get(word).setTf(maxFreq);
		}
	}

	/**
	 * This is used to set idf for particular word
	 * 
	 * @param word
	 * @param idf
	 */
	public void setQueryWordIdf(String word, double idf) {
		queryWordInfoList.get(word).setIdf(idf);
	}

	/**
	 * This is used to get QueryWordInfo for particular word
	 * 
	 * @param word
	 * @return QueryWordInfo for particular word
	 */
	public QueryWordInfo getQueryWordInfo(String word) {
		return queryWordInfoList.get(word);
	}

	/**
	 * Get the query words in ArrayList
	 * 
	 * @return the query words in ArrayList
	 */
	public ArrayList<String> getQueryWords() {
		return queryWords;
	}

	/**
	 * Get the query size
	 * 
	 * @return the querySize
	 */
	public int getQuerySize() {
		return querySize;
	}
}
