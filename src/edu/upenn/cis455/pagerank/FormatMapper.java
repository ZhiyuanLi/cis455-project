package edu.upenn.cis455.pagerank;

import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import java.io.IOException;

/**
 * emit <url, rank> pair, ignore appending outLinks URLs
 * 
 * @author weisong
 *
 */
public class FormatMapper extends Mapper<LongWritable, Text, Text, Text> {
	@Override
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		String[] pair = value.toString().trim().split("\t", 2);
		String url = pair[0];
		Page page = new Page();
		page.unflatten(pair[1]);
		context.write(new Text(url), new Text(String.valueOf(page.rank)));

		// // ## other
		// // Parse input
		// String line = value.toString();
		// String[] pair = line.split("\t", 2);
		// if (pair.length != 2) {
		// System.err.println("line invalid format");
		// return;
		// }
		//
		// // Emit rank
		// String url = pair[0];
		// Page page = new Page();
		// try {
		// page.deserialize(pair[1]);
		// } catch (Exception e) {
		// System.err.println("page invalid format");
		// e.printStackTrace();
		// return;
		// }
		// context.write(new Text(url), new Text(String.valueOf(page.rank)));
	}
}
