package edu.upenn.cis455.indexer;

import org.apache.hadoop.io.Text;

import java.io.IOException;
import java.util.Collections;
import java.util.Hashtable;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;

public class BiwordIndexerMapper extends Mapper<LongWritable, Text, Text, InterValue> {

	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		String[] temp = value.toString().split("\t", 2);
		if (temp.length == 2) {
			String docId = temp[0];
			String docContent = temp[1];
			Tokenizer tokenizer = new Tokenizer(docContent);

			Hashtable<String, Integer> wordFrequencyMap = new Hashtable<String, Integer>();
			String preWord = "";
			String concatWord = "";
			while (tokenizer.hasNext()) {			
				String word = WordProcessor.preProcess(tokenizer.nextToken());
				//				System.out.println(word);
				if (!word.equals("")) {
					if (preWord.equals("")) {
						preWord = word;
						continue;
					} else {
						concatWord = WordProcessor.concat(preWord, word);
						preWord = word;
					}
					//					System.out.println(concatWord);
					String pWord = WordProcessor.process(concatWord.toLowerCase());
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
					// get tf score of that word
					double tf = IndexerDriver.TF_FACTOR + (1 - IndexerDriver.TF_FACTOR) * ((double) wordFrequencyMap.get(word) / maxFrequency);
					context.write(new Text(word), new InterValue(docId, tf));
				}
			}
		}
	}
}
