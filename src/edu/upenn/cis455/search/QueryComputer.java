package edu.upenn.cis455.search;

import java.io.IOException;
import java.util.*;

import edu.upenn.cis455.indexer.Tokenizer;
import edu.upenn.cis455.indexer.WordProcessor;

public class QueryComputer {

	private int querySize;
	private Hashtable<String, QueryWordInfo> queryWordInfoList;

	public QueryComputer() {
		querySize = 0;
		queryWordInfoList = new Hashtable<String, QueryWordInfo>();
	}

	public void setQuery(String queryS) {
		try {
			Tokenizer tokenizer = new Tokenizer(queryS);
			int position = 1;
			int maxFreq = 1;
			while (tokenizer.hasNext()) {
				String word = WordProcessor.preProcess(tokenizer.nextToken());
				if (!word.equals("")) {
					String pWord = WordProcessor.process(word.toLowerCase());
					QueryWordInfo queryWordInfo;
					if (pWord != null) {
						queryWordInfo = new QueryWordInfo(pWord, position);
						if (queryWordInfoList.containsKey(pWord)) {
							queryWordInfo = queryWordInfoList.get(pWord);
							queryWordInfo.addFreq();
							if (maxFreq < queryWordInfo.getFreq()) {
								maxFreq = queryWordInfo.getFreq();
							}
						}
						queryWordInfoList.put(pWord, queryWordInfo);
						position++;
					}
				}
			}
			setQueryWordTf(maxFreq);
		} catch (IOException e) {
			System.out.println("@ Set Query");
		}
	}
	
	private void setQueryWordTf(int maxFreq) {
		for (String word : queryWordInfoList.keySet()) {
			querySize++;
			queryWordInfoList.get(word).setTf(maxFreq);
		}
	}
	
	public void setWordWeight(String word, double idf) {
		queryWordInfoList.get(word).setWeight(idf);
	}

	/**
	 * @return the queryWordInfoList
	 */
	public Hashtable<String, QueryWordInfo> getQueryWordInfoList() {
		return queryWordInfoList;
	}

	/**
	 * @return the querySize
	 */
	public int getQuerySize() {
		return querySize;
	}
	
	
	public QueryWordInfo getUpdateWordInfo(String word) {
		return queryWordInfoList.get(word);
	}
	

	
	
	
	
	
	public void print() {
		for (String word: queryWordInfoList.keySet()) {
			System.out.println(queryWordInfoList.get(word));
		}
	}
	
	public static void main(String[] args) {
		QueryComputer q = new QueryComputer();
		q.setQuery("fast food");
//		q.setWordWeight("world", 12.5);
//		q.setWordWeight("good", 12);
		q.print();
	}
}
