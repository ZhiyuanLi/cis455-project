package edu.upenn.cis455.pagerank;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * all keys are the same, sum total rank scores and emit <"finalRank" score>;
 * output2 has only one line
 * 
 * @author weisong
 *
 */
public class SumReducer extends Reducer<Text, Text, Text, Text> {
	@Override
	protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		double finalRank = 0;
		for (Text v : values) {
			finalRank += Double.valueOf(v.toString());
		}
		// only have one key which is "finalRank"
		context.write(key, new Text(String.valueOf(finalRank)));
	}
}
