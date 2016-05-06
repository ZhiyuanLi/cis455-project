package edu.upenn.cis455.pagerank;



import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import java.io.IOException;

/**
 * convert A -> B to <B A> and <A "source">
 * 
 * @author weisong
 *
 */
public class ParseMapper extends Mapper<LongWritable, Text, Text, Text> {
	@Override
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		String[] pair = splitToPair(value.toString(), "\t", 2);
		if (pair.length != 2) {
			System.err.println("wrong input format");
			return;
		}
		// source url
		String url = pair[0].trim();
		Page page = new Page(1, pair[1].trim().split(" "));
		// 1. write <A "source">
		context.write(new Text(url), new Text("source"));
		// 2. write <B A>, <C A>, <D, A> ...
		for (int i = 0; i < page.outLinks.length; i++) {
			String outlink = page.outLinks[i].trim();
			context.write(new Text(outlink), new Text(url));
		}
	}

	/**
	 * helper function used in mapper run, split value string into 2 parts split
	 * by \t
	 * 
	 * @param line
	 * @param delimeter
	 * @param size
	 * @return
	 */
	public String[] splitToPair(String line, String delimeter, int size) {
		if (line.equals("")) {
			return new String[0];
		} else {
			return line.split(delimeter, size);
		}
	}
}