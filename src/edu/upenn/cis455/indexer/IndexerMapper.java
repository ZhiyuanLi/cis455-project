package edu.upenn.cis455.indexer;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
			while (tokenizer.hasNext()) {
				String word = preProcess(tokenizer.nextToken());
				if (!word.equals("") && !word.matches("\\W+")) {
					String pWord = WordProcessor.process(word.toLowerCase());
					if (pWord != null) {
						Integer frequecy = 1;
						if (wordFrequencyMap.containsKey(pWord)) {
							frequecy = wordFrequencyMap.get(pWord) + 1;
						}
						wordFrequencyMap.put(pWord, frequecy);
					}
				}
			}
			// Do emit if word frequency is not empty
			if (!wordFrequencyMap.isEmpty()) {
				// Get max frequency
				Integer maxFrequency = Collections.max(wordFrequencyMap.values());
				// Compute the TF score and emit
				for (String word : wordFrequencyMap.keySet()) {
					double tf = TF_FACTOR + (1 - TF_FACTOR) * ((double)wordFrequencyMap.get(word) / maxFrequency);
					//					System.out.println(word + ":::" + inputKey + "," + tf);
					context.write(new InterKey(word), new InterValue(inputKey, tf));
				}
			}
		}
	}
	
	/**
	 * Pre-process word, get rid of non-word character at the end of the word, such as '?' '.' ','
	 * @param word
	 * @return a string
	 */
	private String preProcess(String word) {
		Pattern p = Pattern.compile("(\\p{L}+(?:-?\\p{L}+)*)+\\W*");
		Matcher m = p.matcher(word);
		if (m.matches()) { 
			word = m.group(1);
		}
		return word;
	}
}
