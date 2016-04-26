// Driver for a Mercator-style MapReduce crawler
// Author: Christopher Besser
package edu.upenn.cis455.crawler;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.Job;
import java.net.URI;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
public class CrawlerDriver extends Configured implements Tool
{
	private static int workers = 0;
	private static String dbDir = "";
	private static String indexDBDir = "";
	private static int maxSize = 0;
	private static String linksPath = "";
	private static String imagesDBDir = "";
	private static int numFiles = Integer.MAX_VALUE;
	private static int pagesPerCrawl = 10;
	/**
	 * Launch the shuffle job
	 * @param args - <URL dir> <output dir>
	 * @throws Exception if an error occurs
	 */
	public static boolean runShuffle(String[] args) throws Exception
	{
		deleteDirectory(args[1]);
		Job job = new Job();
		job.setJarByClass(CrawlerDriver.class);
		job.setJobName("Shuffle Step");
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		job.setMapperClass(ShuffleMapper.class);
		job.setReducerClass(ShuffleReducer.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		job.setNumReduceTasks(workers);
		return job.waitForCompletion(false);
	}

	/**
	 * Launch the crawler job
	 * @param args - <source dir> <frontier dir>
	 * @throws Exception if an error occurs
	 */
	public static boolean runCrawler(String[] args) throws Exception
	{
		deleteDirectory(args[1]);
		Configuration conf = new Configuration();
		conf.set("dbDir", dbDir);
		conf.set("indexDBDir", indexDBDir);
		conf.set("imgsDBDir", imagesDBDir);
		conf.set("frontierPath", args[1]);
		conf.set("maxSize", "" + maxSize);
		conf.set("linksPath", linksPath);
		conf.set("files", "" + pagesPerCrawl);
		Job job = new Job(conf);
		job.setJobName("Crawler Step");
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		job.setMapperClass(CrawlerMapper.class);
		job.setReducerClass(CrawlerReducer.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		job.setNumReduceTasks(workers);
		return job.waitForCompletion(false);
	}

	/**
	 * Start the crawler MapReduce job
	 * @param args - the command line arguments
	 * @throws Exception if an error occurs
	 */
	public int run(String[] args) throws Exception
	{
		if ((args.length < 9) || (args.length > 10))
		{
			System.out.println("Usage: CrawlerDriver <seed urls path> <db root> <index db root> <image db root> <shuffle out path>" +
					   " <crawler out path> <max file size> <out links log path> <numWorkers> [num of files]");
			return -2;
		}
		String seedPath = args[0];
		workers = Integer.parseInt(args[8]);
		dbDir = args[1];
		indexDBDir = args[2];
		imagesDBDir = args[3];
		String URLPath = args[4];
		String frontierPath = args[5];
		maxSize = Integer.parseInt(args[6]);
		linksPath = args[7];
		numFiles = (args.length == 10) ? Integer.parseInt(args[9]) : Integer.MAX_VALUE;
		boolean success = runCrawler(new String[] {seedPath, frontierPath});
		int iterations = 1;
		// we process workers URLs at a time
		while (iterations < (numFiles / (workers * pagesPerCrawl)))
		{
			success &= runShuffle(new String[] {frontierPath, URLPath});
			success &= runCrawler(new String[] {URLPath, frontierPath});
			iterations++;
		}
		deleteDirectory(URLPath);
		return success ? 0 : -1;
	}

	/**
	 * Run the job
	 * @param args - command line arguments
	 * @throws Exception if an error occurs
	 */
	public static void main(String[] args) throws Exception
	{
		ToolRunner.run(new CrawlerDriver(), args);
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
