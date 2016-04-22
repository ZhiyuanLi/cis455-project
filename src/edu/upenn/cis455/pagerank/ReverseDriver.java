package edu.upenn.cis455.pagerank;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * Step 2 of page rank, convert inlinks to outlinks; After step 1 and step 2,
 * some of urls are filtered out
 * 
 * @author weisong
 *
 */
public class ReverseDriver extends Configured implements Tool {
	Job job = null;

	@SuppressWarnings("deprecation")
	@Override
	public int run(String[] args) throws Exception {
		String input = args[0];
		String output = args[1];

		job = new Job();
		job.setJarByClass(ReverseDriver.class);
		job.setJobName("Step 2: reverse");

		FileInputFormat.addInputPath(job, new Path(input));
		FileOutputFormat.setOutputPath(job, new Path(output));

		job.setMapperClass(ReverseMapper.class);
		job.setReducerClass(ReverseReducer.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		boolean success = job.waitForCompletion(true);
		return success ? 0 : 1;
	}

	/**
	 * count number in reduce output
	 * 
	 * @return
	 * @throws Exception
	 */
	public long counterReduceOutput() throws Exception {
		return job.getCounters().findCounter(EnumCount.Counters.PAGECOUNT).getValue();
	}

}