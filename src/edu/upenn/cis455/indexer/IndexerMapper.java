package edu.upenn.cis455.indexer;

import java.io.*;
import java.util.*;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;

public class IndexerMapper extends Mapper<LongWritable, Text, InterKey, InterValue> {

	private final double TF_FACTOR = 0.5;

	@Override
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		String[] temp = value.toString().split("\t", 2);
		if (temp.length == 2) {
			// Get input key value pair
			String inputKey = temp[0];
			String inputValue = temp[1];
			// Get word frequency map
			Hashtable<String, Integer> wordFrequencyMap = getWordFrequencyMap(inputValue);
			// Get max frequency
			Integer maxFrequency = getMaxFrequency(wordFrequencyMap);
			// Compute the TF score and emit
			for (String word : wordFrequencyMap.keySet()) {
				double tf = TF_FACTOR + (1 - TF_FACTOR) * ((double)wordFrequencyMap.get(word) / maxFrequency);
//				System.out.println(word + ":::" + inputKey + "," + tf);
				context.write(new InterKey(word), new InterValue(inputKey, tf));
			}
		}
	}

	private Hashtable<String, Integer> getWordFrequencyMap(String inputValue) {
		Hashtable<String, Integer> wordFrequencyMap = new Hashtable<String, Integer>();
		String[] words = inputValue.split(" ");
		//		int count = 0;
		for (String word : words) {
			String pWord = WordProcessor.process(word.toLowerCase());
			if (pWord != null) {
				Integer frequecy = 1;
				if (wordFrequencyMap.containsKey(pWord)) {
					frequecy = wordFrequencyMap.get(pWord) + 1;
				}
				wordFrequencyMap.put(pWord, frequecy);
			}
			//			count++;
		}
//		print(wordFrequencyMap);
		return wordFrequencyMap;
	}

	private Integer getMaxFrequency(Hashtable<String, Integer> wordFrequencyMap) {
//		print(wordFrequencyMap);
		return Collections.max(wordFrequencyMap.values());
	}
	
	private void print(Hashtable<String, Integer> w) {
		for (String word: w.keySet()) {
			System.out.print(word + ":"+w.get(word)+";;");
			System.out.println();
		}
	}


}
