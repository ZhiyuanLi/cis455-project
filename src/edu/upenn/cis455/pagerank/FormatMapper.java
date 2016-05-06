package edu.upenn.cis455.pagerank;



import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import java.io.IOException;

/**
 * emit <url, rank> pair, ignore appending outLinks URLs
 * 
 * @author weisong
 *
 */
public class FormatMapper extends Mapper<LongWritable, Text, Text, Text> {
	@Override
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		String[] pair = value.toString().trim().split("\t", 2);
		String url = pair[0];
		Page page = new Page();
		page.unflatten(pair[1]);
		context.write(new Text(url), new Text(String.valueOf(page.rank)));

	}
}
