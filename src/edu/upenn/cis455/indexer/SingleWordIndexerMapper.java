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
		if (temp.length == 2) {
			// Get input key value pair
			String docId = temp[0];
			String docContent = temp[1];
			// Get word frequency map， pre-deal with content
			Hashtable<String, Double> wordFrequencyMap = new Hashtable<String, Double>();
			Hashtable<String, ArrayList<Integer>> wordHitsMap = new Hashtable<String, ArrayList<Integer>>();
			Tokenizer tokenizer = new Tokenizer(docContent);
			int position = 0;
			while (tokenizer.hasNext()) {
				String k = tokenizer.nextToken();
				String word = WordProcessor.preProcess(k);
				if (!word.equals("")) {
					String pWord = WordProcessor.process(word.toLowerCase());
					// update word frequency map and word hits map
					if (pWord != null) {	
						position++;
						Double frequecy = 1.0;
						ArrayList<Integer> hits = new ArrayList<Integer>();
						if (wordFrequencyMap.containsKey(pWord)) {
							frequecy = wordFrequencyMap.get(pWord) + 1;
							hits = wordHitsMap.get(pWord);
						}
						hits.add(position);
						wordFrequencyMap.put(pWord, frequecy);
						wordHitsMap.put(pWord, hits);
					}
				}
			}
			// Do emit if word frequency is not empty
			if (!wordFrequencyMap.isEmpty()) {
				// Compute the module
				Double module = 0.0;
				for (String word : wordFrequencyMap.keySet()) {
					module += Math.pow(wordFrequencyMap.get(word), 2);
				}
				module = Math.sqrt(module);
				// Get normalize word frequency map
				Double maxFrequency = 0.0;
				for (String word : wordFrequencyMap.keySet()) {
					Double frequency = wordFrequencyMap.get(word) / module;
					if (frequency > maxFrequency) {
						maxFrequency = frequency;
					}
					wordFrequencyMap.put(word, frequency);
				}
				// Compute the TF score and emit
				for (String word : wordFrequencyMap.keySet()) {
					// get tf score of that word
					double tf = IndexerDriver.TF_FACTOR
							+ (1 - IndexerDriver.TF_FACTOR) * ((double) wordFrequencyMap.get(word) / maxFrequency);
					// get hits position of that word
					StringBuffer hitsBuffer = new StringBuffer("");
					ArrayList<Integer> hits = wordHitsMap.get(word);
					for (int i = 0; i < hits.size(); i++) {
						if (i == hits.size() - 1) {
							hitsBuffer.append(hits.get(i));
						} else {
							hitsBuffer.append(hits.get(i)).append(",");
						}
					}
					context.write(new Text(word), new InterValue(docId, hitsBuffer.toString(), tf));
				}
			}
		}
	}
}
