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
	 * @param args - <URL dir> <output dir> <num crawlers>
	 * @throws Exception if an error occurs 
	 */
	public static void main(String[] args) throws Exception
	{
		Job job = new Job();
		job.setJarByClass(CrawlerDriver.class);
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		job.setNumReduceTasks(Integer.parseInt(args[2]));
		job.setMapperClass(CrawlerMapper.class);
		job.setReducerClass(CrawlerReducer.class);
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		job.submit();
		while (!job.isComplete())
		{
			// Run the shuffle job every 2 minutes
			Thread.sleep(120000);
			Job job2 = new Job();
			job.setJarByClass(CrawlerDriver.class);
			FileInputFormat.addInputPath(job, new Path(args[0]));
			job.setMapOutputKeyClass(Text.class);
			job.setMapOutputValueClass(Text.class);
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);
			job.setNumReduceTasks(Integer.parseInt(args[2]));
			job.setMapperClass(ShuffleMapper.class);
			job.setReducerClass(ShuffleReducer.class);
			FileOutputFormat.setOutputPath(job, new Path(args[1]));
			job.waitForCompletion(true);
		}
	}
}
