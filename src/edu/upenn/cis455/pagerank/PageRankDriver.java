package edu.upenn.cis455.pagerank;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.ToolRunner;

/**
 * main class to start page rank algorithm
 * 
 * @author weisong
 *
 */
public class PageRankDriver extends Configured implements Tool {

	Job job = null;

	@Override
	public int run(String[] args) throws Exception {
		if (args.length != 3) {
			System.out.println("Error in page rank driver");
			return 1;
		}
		String input = args[0];
		String output = args[1];
		// !! loss compensate: guarantee total rank score + total loss = url #
		double loss = Double.valueOf(args[2]);

		@SuppressWarnings("deprecation")
		Job job = new Job();
		job.setJarByClass(PageRankDriver.class);
		job.setJobName("Step 4: page rank");
		job.getConfiguration().setDouble("lossCompensate", loss);

		FileInputFormat.addInputPath(job, new Path(input));
		FileOutputFormat.setOutputPath(job, new Path(output));

		job.setMapperClass(PageRankMapper.class);
		job.setReducerClass(PageRankReducer.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		boolean success = job.waitForCompletion(true);
		return success ? 0 : 1;
	}

	/**
	 * input arguments should be {input output iterationNum}
	 */
	public static void main(String[] args) throws Exception {
		String input = args[0];
		String output = args[1];
		int iterationNum = Integer.parseInt(args[2]);
		if (args.length != 3) {
			System.out.println("Wrong Input Argument Number, should be {input output iterationNum}");
			System.exit(1);
		}
		// Step 1: parse
		// input -> output1
		ParseDriver parser = new ParseDriver();
		int result = ToolRunner.run(parser, new String[] { input, "output1" });
		if (result == 1) {
			System.out.println("Error at step 1: parse inputs");
			System.exit(1);
		}
		System.out.println("## Job 1 finished");
		// Step 2: reverse
		// output1 -> output2
		ReverseDriver reverse = new ReverseDriver();
		result = ToolRunner.run(reverse, new String[] { "output1", "output2" });
		if (result == 1) {
			System.out.println("Error at step 2: reverse links");
			System.exit(1);
		}
		System.out.println("## Job 2 finished");
		// Step 2.1: get count
		long count = reverse.counterReduceOutput();

		// To here, dangling links has been filtered out
		String outFinalStep3 = "tmp/iterated";
		String inputStep3 = null;
		String outputStep3 = null;
		String sumStep3 = null;
		double singleLoss = 0;
		for (int i = 0; i < iterationNum; i++) {
			if (i == 0) {
				inputStep3 = "output2";
			} else {
				inputStep3 = outputStep3;
			}
			if (i == iterationNum - 1) {
				outputStep3 = outFinalStep3;
			} else {
				outputStep3 = "tmp/loop_" + String.valueOf(i);
			}
			sumStep3 = outputStep3 + "_sum";

			// Step 3: calculate loss
			SumDriver sum = new SumDriver();
			System.out.println("@@@ sum input is " + inputStep3);
			result = ToolRunner.run(sum, new String[] { inputStep3, sumStep3 });
			System.out.println("@@@ sum output is " + sumStep3);
			if (result == 1) {
				System.out.println("Error at step 3: sum");
				System.exit(1);
			}
			System.out.println("### Job 3 finished when i = " + i);
			Path path = new Path(sumStep3 + "/part-r-00000");
			FileSystem fileSystemReader = path.getFileSystem(new Configuration());
			FileStatus[] fs = fileSystemReader.listStatus(new Path(sumStep3));
			System.out.println("boolean " + fs == null);
			System.out.println("!! " + fs.length);
			String line = null;
			boolean end = false;
			for (int j = 0; j < fs.length; j++) {
				BufferedReader br = new BufferedReader(new InputStreamReader(fileSystemReader.open(fs[j].getPath())));
				line = br.readLine();
				System.out.println("--- each line is" + line);
				while (line != null) {
					end = true;
					break;
				}
				if(end){
					break;
				}
				
			}
			System.out.println("line is "+ line);

			System.out.println("$$$ path is " + path.toString());
			FileSystem fileSystem = path.getFileSystem(new Configuration());
			// read total rank to calculate total loss
			if (fileSystem.exists(path) && fileSystem.isFile(path)) {
				System.out.println("%%% path exists");
				double totalLoss = 0.0;
				// String line = new BufferedReader(new
				// InputStreamReader(fileSystem.open(path))).readLine();
				System.out.println("%%% line is " + line);
				String[] pair = line.split("\t", 2);
				// calculate total loss
				totalLoss = count - Double.parseDouble(pair[1]);
				singleLoss = totalLoss / count;
			} else {
				System.out.println("%%% path does not exists");
				singleLoss = 0;
			}
			fileSystem.close();

			// Step 4: page rank update algorithm
			PageRankDriver rank = new PageRankDriver();
			System.out.println("@@@ pagerank input is " + inputStep3);
			result = ToolRunner.run(rank, new String[] { inputStep3, outputStep3, String.valueOf(singleLoss) });
			System.out.println("@@@ pagerank output is " + outputStep3);
			if (result == 1) {
				System.err.println("Error at step 4: page rank");
				System.exit(1);
			}
			System.out.println("### Job 4 finished when i = " + i);
		}

		// To here, page ranks should have already converged

		// step 5: format driver
		FormatDriver format = new FormatDriver();
		result = ToolRunner.run(format, new String[] { outFinalStep3, output });
		if (result == 1) {
			System.err.println("Error at step 5: format");
			System.exit(1);
		}
		System.out.println("### Job 5 finished");

	}

}