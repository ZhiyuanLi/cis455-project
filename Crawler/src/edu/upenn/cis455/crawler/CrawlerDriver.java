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
	/**
	 * Launch the shuffle job
	 * @param args - <URL dir> <output dir>
	 * @throws Exception if an error occurs
	 */
	public int run(String[] args) throws Exception
	{
		if (args.length != 2)
		{
			System.out.println("Usage: CrawlerDriver <in directory> <out directory>");
			return -2;
		}
		deleteDirectory(args[1]);
		Job job = new Job();
		job.setJarByClass(CrawlerDriver.class);
		job.setJobName("Shuffle URLs");
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		job.setMapperClass(ShuffleMapper.class);
		job.setReducerClass(ShuffleReducer.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		job.setNumReduceTasks(20);
		return job.waitForCompletion(false) ? 0 : 1;
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
	 * Delete a directory
	 * @param path - directory to delete
	 */
	static void deleteDirectory(String path) throws Exception
	{
		Path toDelete = new Path(path);
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(URI.create(path), conf);
		if (fs.exists(toDelete))
		{
			fs.delete(toDelete, true);
		}
		fs.close();
	}
}
