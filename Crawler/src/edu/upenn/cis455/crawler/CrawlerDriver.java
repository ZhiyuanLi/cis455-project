// Driver for a Mercator-style MapReduce crawler
// Author: Christopher Besser
package edu.upenn.cis455.crawler;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URI;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
public class CrawlerDriver
{
	private static int workers = 0;
	private static String dbDir = "";
	private static String indexDBDir = "";
	private static int maxSize = 0;
	private static String linksPath = "";
	private static String imagesDBDir = "";
	/**
	 * Launch the shuffle job
	 * @param args - <URL dir> <output dir>
	 * @return Returns the success state of the job
	 * @throws Exception if an error occurs
	 */
	public static boolean runShuffle(String[] args) throws Exception
	{
		deleteDirectory(args[1]);
		Job job = new Job();
		job.setJarByClass(CrawlerDriver.class);
		job.setJobName("Shuffle Step");
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		job.setMapperClass(ShuffleMapper.class);
		job.setReducerClass(ShuffleReducer.class);
		job.setNumReduceTasks(workers);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		boolean success = job.waitForCompletion(true);
		System.out.println(success);
		return success;
	}

	/**
	 * Launch the crawler job
	 * @param args - <source dir> <frontier dir>
	 * @return Returns the success state of the job
	 * @throws Exception if an error occurs
	 */
	public static boolean runCrawler(String[] args) throws Exception
	{
		deleteDirectory(args[1]);
		Configuration conf = new Configuration();
		conf.set("dbDir", dbDir);
		conf.set("indexDBDir", indexDBDir);
		conf.set("imagesDBDir", imagesDBDir);
		conf.set("frontierPath", args[1]);
		conf.set("maxSize", "" + maxSize);
		conf.set("linksPath", linksPath);
		Job job = new Job(conf, "Crawler Job");
//		Job job = new Job();
		job.setJarByClass(CrawlerDriver.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		job.setMapperClass(CrawlerMapper.class);
		job.setReducerClass(CrawlerReducer.class);
		job.setNumReduceTasks(workers);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		boolean success = job.waitForCompletion(true);
		System.out.println(success);
		return success;
	}

	/**
	 * Start the crawler MapReduce job
	 * @param args - the command line arguments
	 * @throws Exception if an error occurs
	 */
	public static void main(String[] args) throws Exception
	{
		if ((args.length < 9) || (args.length > 10))
		{
			System.out.println("Usage: CrawlerDriver <seed urls path> <db root> <index db root> <image db root> <shuffle out path>" +
					   " <crawler out path> <max file size> <out links log path> <numWorkers> [num of files]");
			return;
		}
		String seedPath = args[0];
		int workers = Integer.parseInt(args[8]);
		dbDir = args[1];
		indexDBDir = args[2];
		imagesDBDir = args[3];
		String URLPath = args[4];
		String frontierPath = args[5];
		maxSize = Integer.parseInt(args[6]);
		linksPath = args[7];
		int maxFiles = (args.length == 10) ? Integer.parseInt(args[9]) : Integer.MAX_VALUE;
		boolean success = runCrawler(new String[] {seedPath, frontierPath});
		int iterations = 1;
		// we process (100 * workers) URLs at a time
		while (iterations < (maxFiles / (100 * workers)))
		{
			success &= runShuffle(new String[] {frontierPath, URLPath});
			success &= runCrawler(new String[] {URLPath, frontierPath});
			iterations++;
		}
		deleteDirectory(URLPath);
		deleteDirectory(frontierPath);
	}

	/**
	 * Delete the specified temporary directory
	 * @param name - the name of the file
	 * @throws Exception if an error occurs
	 */
	private static void deleteDirectory(String path) throws Exception
	{
		Path todelete = new Path(path);
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(URI.create(path), conf);
		if (fs.exists(todelete))
		{
			fs.delete(todelete, true);
		}
		fs.close();
	}
}
