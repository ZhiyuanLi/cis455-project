package edu.upenn.cis455.indexer;

import java.io.*;
import java.util.*;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;

public class SingleWordIndexerMapper extends Mapper<LongWritable, Text, Text, InterValue> {
	
	@Override
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		String[] temp = value.toString().split("\t", 2);
		// System.out.println("split complete");
		if (temp.length == 2) {
			// Get input key value pair
			String docId = temp[0];
			String docContent = temp[1];
			// System.out.println("get key value");
			// System.out.println(inputKey);
			// pre-deal with content
			Tokenizer tokenizer = new Tokenizer(docContent);
			// Get word frequency map
			Hashtable<String, Double> wordFrequencyMap = new Hashtable<String, Double>();
//			Hashtable<String, ArrayList<Integer>> wordHitsMap = new Hashtable<String, ArrayList<Integer>>();
			//			int position = 0;
			while (tokenizer.hasNext()) {
				// System.out.println("hasnext=true");
				String k = tokenizer.nextToken();
				// System.out.println("getnext");
				// System.out.println("token: " + k);
				String word = WordProcessor.preProcess(k);
				// System.out.println("preprocess: "+ word);
				if (!word.equals("")) {
//					position++;
					// System.out.println("hello: " + word.toLowerCase());
					String pWord = WordProcessor.process(word.toLowerCase());
					// System.out.println("process: " + pWord);
					if (pWord != null) {
						Double frequecy = 1.0;
//						ArrayList<Integer> hits = new ArrayList<Integer>();
						if (wordFrequencyMap.containsKey(pWord)) {
							frequecy = wordFrequencyMap.get(pWord) + 1;
//							hits = wordHitsMap.get(pWord);
						}
//						hits.add(position);
						wordFrequencyMap.put(pWord, frequecy);
//						wordHitsMap.put(pWord, hits);
					}
					// System.out.println("endofwhile");
				}
			}
			// System.out.println("tokenizer finished");
			// Do emit if word frequency is not empty
			if (!wordFrequencyMap.isEmpty()) {
				// Get module value update word frequency map
				wordFrequencyMap = getModule(wordFrequencyMap);
				// Get max frequency
				Double maxFrequency = Collections.max(wordFrequencyMap.values());
				// Compute the TF score and emit
				for (String word : wordFrequencyMap.keySet()) {
					// get tf score of that word
					double tf = IndexerDriver.TF_FACTOR + (1 - IndexerDriver.TF_FACTOR) * ((double) wordFrequencyMap.get(word) / maxFrequency);
					// System.out.println(word + ":::" + inputKey + "," + tf);
					// get hits position of that word
//					StringBuffer hitsBuffer = new StringBuffer("[");
//					ArrayList<Integer> hits = wordHitsMap.get(word);
//					for (int i = 0; i < hits.size(); i++) {
//						if (i == hits.size() - 1) {
//							hitsBuffer.append(hits.get(i));
//						} else {
//							hitsBuffer.append(hits.get(i)).append(",");
//						}
//					}
//					hitsBuffer.append("]");
					// System.out.println("current url is : " + inputKey + ":: "
					// + word);
					context.write(new Text(word), new InterValue(docId, tf));
				}
			}
		}
	}
	
	
	private Hashtable<String, Double> getModule(Hashtable<String, Double> wordFrequencyMap) {
		double module = 0;
		for (String word : wordFrequencyMap.keySet()) {
			module += Math.pow(wordFrequencyMap.get(word), 2);
		}
		module = Math.sqrt(module);
		for (String word : wordFrequencyMap.keySet()) {
			wordFrequencyMap.put(word, wordFrequencyMap.get(word)/module);
		}
		return wordFrequencyMap;
	}
}
