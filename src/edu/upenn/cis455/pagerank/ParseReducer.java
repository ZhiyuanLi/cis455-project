package edu.upenn.cis455.pagerank;



import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.io.Text;
import java.io.IOException;
/**
 * emit <A "source"> and <B list <inlinks>>
 * @author weisong
 *
 */
public class ParseReducer extends Reducer<Text, Text, Text, Text> {
	@Override
	protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		StringBuilder sb = new StringBuilder();
		boolean downloaded = false;
		for (Text v : values) {
			String value = v.toString().trim();
			// if key is source, value is "source"
			if (value.equals("source")) {
				// 1. write <A "source">
				context.write(key, v);
				downloaded = true;
			}
			// if key is destination
			else {
				// combine all in-links to a key!
				sb.append(" " + value.toString());
			}
		}
		String inLinks = sb.toString();
		// only write <A B+C+D+E...> when A is already downloaded
		if (inLinks.length() != 0 && downloaded) {
			context.write(key, new Text(inLinks.substring(1)));
		}
	}
}
