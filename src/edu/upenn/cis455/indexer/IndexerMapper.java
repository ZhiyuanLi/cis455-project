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
			// pre-deal with content 
			Tokenizer tokenizer = new Tokenizer(inputValue);
			// Get word frequency map
			Hashtable<String, Integer> wordFrequencyMap = new Hashtable<String, Integer>();
			Hashtable<String, ArrayList<Integer>> wordHitsMap = new Hashtable<String, ArrayList<Integer>>();
			int position = 0;
			while (tokenizer.hasNext()) {
				String word = WordProcessor.preProcess(tokenizer.nextToken());
				if (!word.equals("") && !word.matches("\\W+")) {
					position ++;
					String pWord = WordProcessor.process(word.toLowerCase());
					if (pWord != null) {
						Integer frequecy = 1;
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
				// Get max frequency
				Integer maxFrequency = Collections.max(wordFrequencyMap.values());
				// Compute the TF score and emit
				for (String word : wordFrequencyMap.keySet()) {
					// get tf score of that word
					double tf = TF_FACTOR + (1 - TF_FACTOR) * ((double)wordFrequencyMap.get(word) / maxFrequency);
					//					System.out.println(word + ":::" + inputKey + "," + tf);
					// get hits position of that word
					StringBuffer hitsBuffer = new StringBuffer("[");
					ArrayList<Integer> hits = wordHitsMap.get(word);
					for (int i = 0; i < hits.size(); i++) {
						if (i == hits.size() - 1) {
							hitsBuffer.append(hits.get(i));
						} else {
							hitsBuffer.append(hits.get(i)).append(",");
						}
					}
					hitsBuffer.append("]");
					context.write(new InterKey(word), new InterValue(inputKey, hitsBuffer.toString(), tf));
				}
			}
		}
	}
}
