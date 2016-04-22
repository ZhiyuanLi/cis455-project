package edu.upenn.cis455.pagerank;

import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import java.io.IOException;

/**
 * convert inlinks to outlinks, one by each line, different from initial input
 * in that <A {B,C,D}> has separated to <A B>, <A C>, <A,D>
 * 
 * @author weisong
 *
 */
public class ReverseMapper extends Mapper<LongWritable, Text, Text, Text> {

	@Override
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		String[] pair = value.toString().trim().split("\t", 2);
		// source url
		String url = pair[0];
		// 1. write <source "source">
		if (pair[1].equals("source")) {
			context.write(new Text(url), new Text("source"));
		}
		// 2. write <A B>, <A C> ...
		else {
			// convert inLinks to outLinks
			String[] inLinks = pair[1].split(" ");
			for (String in : inLinks) {
				context.write(new Text(in), new Text(url));
			}
		}
	}
}