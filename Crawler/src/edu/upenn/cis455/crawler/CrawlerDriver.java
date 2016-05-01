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
	private static String indexFile = "";
	private static int maxSize = 0;
	private static String linksFile = "";
	private static String imageFile = "";
	private static String titleFile = "";
	private static int numFiles = Integer.MAX_VALUE;
	private static int pagesPerCrawl = 100;
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
		conf.set("indexFile", indexFile);
		conf.set("imageFile", imageFile);
		conf.set("titleFile", titleFile);
		conf.set("maxSize", "" + maxSize);
		conf.set("linksFile", linksFile);
		//conf.set("files", "" + pagesPerCrawl);
		conf.set("files", "" + (numFiles / workers));
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
			System.out.println("Usage: CrawlerDriver <seed urls path> <out links log file> <index file> <title file> <image file> <shuffle out path>" +
					   " <crawler out path> <max file size> <numWorkers> [num of files]");
			return -2;
		}
		long startTime = System.currentTimeMillis();
		String seedPath = args[0];
		workers = Integer.parseInt(args[8]);
		indexFile = args[2];
		imageFile = args[4];
		titleFile = args[3];
		String URLPath = args[5];
		String frontierPath = args[6];
		maxSize = Integer.parseInt(args[7]);
		linksFile = args[1];
		numFiles = (args.length == 10) ? Integer.parseInt(args[9]) : Integer.MAX_VALUE;
		boolean success = runCrawler(new String[] {seedPath, frontierPath});
		int iterations = 1;
		// we process workers URLs at a time
		/*while (iterations < (numFiles / (workers * pagesPerCrawl)))
		{
			success &= runShuffle(new String[] {frontierPath, URLPath});
			success &= runCrawler(new String[] {URLPath, frontierPath});
			System.out.println("********************************************** URLs Processed = " + iterations * workers * pagesPerCrawl + "**********************************");
			iterations++;
		}*/
		deleteDirectory(URLPath);
		System.out.println("Time elapsed: " + (System.currentTimeMillis() - startTime));
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
