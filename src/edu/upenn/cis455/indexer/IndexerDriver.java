package edu.upenn.cis455.indexer;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class IndexerDriver {

	public static final double TF_FACTOR = 0.5;
	public static final int N = 610000;
	public static final int OUPUTSIZE = 100000;

	public static void main(String[] args) throws Exception {
		Configuration jobConf = new Configuration();
		Job job = new Job(jobConf, "inverted index");
		job.setJarByClass(IndexerDriver.class);

		System.out.println(args[0] + " " + args[1]);
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		job.setMapperClass(SingleWordIndexerMapper.class);

		job.setReducerClass(IndexerReducer.class);

		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(InterValue.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		boolean success = job.waitForCompletion(true);
		System.out.println(success);
	}

}
