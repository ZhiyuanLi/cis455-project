package edu.upenn.cis455.pagerank;

import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import java.io.IOException;
/**
 * for each pair after step 2, write <"finalRank" rankScore>
 * @author weisong
 *
 */
public class SumMapper extends Mapper<LongWritable, Text, Text, Text> {

	@Override
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		String[] pair = value.toString().trim().split("\t", 2);
		Page page = new Page();
		page.unflatten(pair[1]);
		// write rank
		context.write(new Text("finalRank"), new Text(String.valueOf(page.rank)));
	}
}
