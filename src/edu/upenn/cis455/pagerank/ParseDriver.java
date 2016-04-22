package edu.upenn.cis455.pagerank;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * Step 1 of page rank, parse inputs hadoop run function format reference:
 * https://hadoopi.wordpress.com/2013/06/05/hadoop-implementing-the-tool-
 * interface-for-mapreduce-driver/
 * 
 * @author weisong
 *
 */
public class ParseDriver extends Configured implements Tool {

	@SuppressWarnings("deprecation")
	@Override
	public int run(String[] args) throws Exception {
		if (args.length != 2) {
			System.out.println("####### Error in parse driver");
			return 1;
		}
		String input = args[0];
		String output = args[1];

		Job job = new Job();
		job.setJarByClass(ParseDriver.class);
		job.setJobName("Step 1: parse");

		FileInputFormat.addInputPath(job, new Path(input));
		FileOutputFormat.setOutputPath(job, new Path(output));

		job.setMapperClass(ParseMapper.class);
		job.setReducerClass(ParseReducer.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		boolean success = job.waitForCompletion(true);
		return success ? 0 : 1;
	}

}
