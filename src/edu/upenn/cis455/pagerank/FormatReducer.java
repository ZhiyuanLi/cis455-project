package edu.upenn.cis455.pagerank;

import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.io.Text;
import java.io.IOException;

/**
 * emit key value pair to final output file
 * 
 * @author weisong
 *
 */
public class FormatReducer extends Reducer<Text, Text, Text, Text> {
	@Override
	protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		for (Text v : values) {
			context.write(key, v);
		}
		// // ## other
		// int i = 0;
		// for (Text value : values) {
		// i++;
		// if (i > 1) {
		// System.err.println("redundant line");
		// return;
		// } else {
		// context.write(key, value);
		// }
		// }
	}
}
