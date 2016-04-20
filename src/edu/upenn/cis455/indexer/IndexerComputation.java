package edu.upenn.cis455.indexer;

import java.util.*;

public class IndexerComputation {
	
	private final static double TF_FACTOR = 0.5;
	private Hashtable<String, Double> queryFrequencyMap;
	private double queryMaxFrequency;
	
	
	public IndexerComputation() {
		// TODO Auto-generated constructor stub
	}
	
	public void setQuery(String queryS) {
		String[] temp = queryS.split(" ");
		queryFrequencyMap = new Hashtable<String, Double>();
		queryMaxFrequency = 1;
		for (int i = 0; i < temp.length; i++) {
			String word = WordProcessor.preProcess(temp[i]);
			if (!word.equals("") && !word.matches("\\W+")) {
				String pWord = WordProcessor.process(word.toLowerCase());
				if (pWord != null) {
					Double frequecy = 1.0;
					if (queryFrequencyMap.containsKey(pWord)) {
						frequecy = queryFrequencyMap.get(pWord) + 1.0;
						if (frequecy > queryMaxFrequency) {
							queryMaxFrequency = frequecy;
						}
					}
					queryFrequencyMap.put(pWord, frequecy);
				}
			}
		}
		System.out.println(queryFrequencyMap);
	}
	
	public Hashtable<String, Double> getDoclist() {
		if (queryFrequencyMap.isEmpty()) {
			// TODO: randomly return 100 pages;
		} else if (queryFrequencyMap.size() == 1) {
			// TODO: get first 100 pages;
		} else {
			
		}
		return null;
	}
	
	private void normalizeQuery() {
		for (String word : queryFrequencyMap.keySet()) {
			double tf = TF_FACTOR + (1 - TF_FACTOR) * ((double)queryFrequencyMap.get(word) / queryMaxFrequency);
			queryFrequencyMap.put(word, tf);
		}
		System.out.println(queryFrequencyMap);
	}
	
	
	
	public static void main(String[] args) {
		
		IndexerComputation ic = new IndexerComputation();
//		ic.setQuery("hello word! penn: you are the best, haha-is that true, penn yeas");
//		ic.normalizeQuery();
		
		ic.setQuery("hello");
		ic.normalizeQuery();
	}

}
