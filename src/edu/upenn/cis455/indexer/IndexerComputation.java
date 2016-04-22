package edu.upenn.cis455.indexer;

import java.io.IOException;
import java.util.*;

public class IndexerComputation {

	private Hashtable<String, Double> singleWordQueryMap;
	private Hashtable<String, Double> biWordQueryMap;

	public IndexerComputation() {
		// TODO Auto-generated constructor stub
	}

	public void setQuery(String queryS) throws IOException {
		setSinglewordQueryMap(queryS);
		setBiwordQueryMap(queryS);
	}

	private void setSinglewordQueryMap(String queryS) throws IOException {
		singleWordQueryMap = new Hashtable<String, Double>();
		double max = 1.0;
		Tokenizer tokenizer = new Tokenizer(queryS);
		while (tokenizer.hasNext()) {
			String word = WordProcessor.preProcess(tokenizer.nextToken());
			if (!word.equals("")) {
				String pWord = WordProcessor.process(word.toLowerCase());
				if (pWord != null) {
					Double frequecy = 1.0;
					if (singleWordQueryMap.containsKey(pWord)) {
						frequecy = singleWordQueryMap.get(pWord) + 1.0;
						if (frequecy > max) {
							max = frequecy;
						}
					}
					singleWordQueryMap.put(pWord, frequecy);
				}
			}
			System.out.println(singleWordQueryMap);
		}

		for (String word : singleWordQueryMap.keySet()) {
			double tf = IndexerDriver.TF_FACTOR + (1 - IndexerDriver.TF_FACTOR) * (singleWordQueryMap.get(word) / max);
			singleWordQueryMap.put(word, tf);
		}
	}

	private void setBiwordQueryMap(String queryS) throws IOException {
		biWordQueryMap = new Hashtable<String, Double>();
		double max = 1.0;
		Tokenizer tokenizer = new Tokenizer(queryS);
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
					if (biWordQueryMap.containsKey(pWord)) {
						frequecy = biWordQueryMap.get(pWord) + 1.0;
						if (frequecy > max) {
							max = frequecy;
						}
					}
					biWordQueryMap.put(pWord, frequecy);
				}
			}
		}
		for (String word : biWordQueryMap.keySet()) {
			double tf = IndexerDriver.TF_FACTOR + (1 - IndexerDriver.TF_FACTOR) * (biWordQueryMap.get(word) / max);
			biWordQueryMap.put(word, tf);
		}
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

	// private void normalizeQuery() {
	// for (String word : biwordQueryMap.keySet()) {
	// double tf = TF_FACTOR + (1 - TF_FACTOR) *
	// ((double)biwordQueryMap.get(word) / singlewordMaxFrequency);
	// biwordQueryMap.put(word, tf);
	// }
	// System.out.println(biwordQueryMap);
	// }

	// public static void main(String[] args) {
	//
	// IndexerComputation ic = new IndexerComputation();
	// // ic.setQuery("hello word! penn: you are the best, haha-is that true,
	// penn yeas");
	// // ic.normalizeQuery();
	//
	// ic.setQuery("hello");
	// ic.normalizeQuery();
	// }

}
