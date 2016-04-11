// Driver for a Mercator-style MapReduce crawler
// Author: Christopher Besser
package edu.upenn.cis455.crawler;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
public class CrawlerDriver
{
	/**
	 * Run the driver
	 * @param args - <input/output dir> <num crawlers>
	 * @throws Exception if an error occurs 
	 */
	public static void run(String[] args) throws Exception
	{
		Job job = new Job();
		job.setJarByClass(CrawlerDriver.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		job.setNumReduceTasks(Integer.parseInt(args[1]));
		job.setMapperClass(CrawlerMapper.class);
		job.setReducerClass(CrawlerReducer.class);
		FileOutputFormat.setOutputPath(job, new Path(args[0]));
		job.waitForCompletion(true);
	}
}
