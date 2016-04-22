package edu.upenn.cis455.pagerank;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * edit final output file in right format, which should be list of <URL,
 * finalPageRankScore>
 * 
 * @author weisong
 *
 */
public class FormatDriver extends Configured implements Tool {
	@SuppressWarnings("deprecation")
	@Override
	public int run(String[] args) throws Exception {
		if (args.length != 2) {
			System.out.println("Error in post driver");
			return 1;
		}
		String input = args[0];
		String output = args[1];
		Job job = new Job();
		job.setJarByClass(FormatDriver.class);
		job.setJobName("Step 5: format");

		FileInputFormat.addInputPath(job, new Path(input));
		FileOutputFormat.setOutputPath(job, new Path(output));

		job.setMapperClass(FormatMapper.class);
		job.setReducerClass(FormatReducer.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		boolean success = job.waitForCompletion(true);
		return success ? 0 : 1;
	}
}
