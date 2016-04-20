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
public class CrawlerDriver
{
	private static int workers = 0;
	private static String dbDir = "";
	private static String indexDBDir = "";
	private static int maxSize = 0;
	private static String linksPath = "";
	/**
	 * Launch the shuffle job
	 * @param args - <URL dir> <output dir>
	 * @return Returns the success state of the job
	 * @throws Exception if an error occurs
	 */
	public static boolean runShuffle(String[] args) throws Exception
	{
		Job job = new Job();
		job.setJarByClass(CrawlerDriver.class);
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		job.setNumReduceTasks(workers);
		job.setMapperClass(ShuffleMapper.class);
		job.setReducerClass(ShuffleReducer.class);
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		return job.waitForCompletion(true);
	}

	/**
	 * Launch the crawler job
	 * @param args - <source dir> <frontier dir>
	 * @return Returns the success state of the job
	 * @throws Exception if an error occurs 
	 */
	public static boolean runCrawler(String[] args) throws Exception
	{
		Configuration conf = new Configuration();
		conf.set("dbDir", dbDir);
		conf.set("indexDBDir", indexDBDir);
		conf.set("frontierPath", args[1]);
		conf.set("URLPath", args[0]);
		conf.set("maxSize", "" + maxSize);
		conf.set("linksPath", linksPath);
		Job job = new Job(conf);
		job.setJarByClass(CrawlerDriver.class);
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		job.setNumReduceTasks(workers);
		job.setMapperClass(CrawlerMapper.class);
		job.setReducerClass(CrawlerReducer.class);
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		return job.waitForCompletion(true);
	}

	/**
	 * Start the crawler MapReduce job
	 * @param args - the command line arguments
	 * @throws Exception if an error occurs
	 */
	public static void main(String[] args) throws Exception
	{
		if ((args.length < 6) || (args.length > 7))
		{
			System.out.println("Usage: CrawlerDriver <seed URLs path> <db root> <index db root> <interm URLs path> <URL frontier path> <max file size> <out links log path> [num of files]");
			return;
		}
		String seedPath = args[0];
		BufferedReader reader = new BufferedReader(new FileReader(seedPath));
		int workers = 0;
		while (reader.ready())
		{
			reader.readLine();
			workers++;
		}
		reader.close();
		dbDir = args[1];
		indexDBDir = args[2];
		String frontierPath = args[4];
		String URLPath = args[3];
		maxSize = Integer.parseInt(args[5]);
		linksPath = args[6];
		int maxFiles = (args.length == 8) ? Integer.parseInt(args[7]) : Integer.MAX_VALUE;
		runCrawler(new String[] {seedPath, frontierPath, "" + workers});
		int iterations = 1;
		// we process workers URLs at a time
		while (iterations < (maxFiles / workers))
		{
			runShuffle(new String[] {frontierPath, URLPath, "" + workers});
			runCrawler(new String[] {URLPath, frontierPath, "" + workers});
			iterations++;
		}
		deleteDirectory(URLPath);
		deleteDirectory(frontierPath);
	}

	/**
	 * Create the specified temporary directory
	 * @param name - the name of the directory
	 * @throws Exception if an error occurs
	 */
	private static void createDirectory(String path) throws Exception
	{
		Path toCreate = new Path(path);
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(URI.create(path), conf);
		if (!fs.exists(toCreate))
		{
			fs.mkdirs(toCreate);
		}
		fs.close();
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
