package edu.upenn.cis455.pagerank;



import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.io.Text;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/**
 * collect out-links for not empty pages(pages do not have outlinks), all outLinks start and end in
 * downloaded pages; each page give an initial weight of 1
 * 
 * @author weisong
 *
 */
public class ReverseReducer extends Reducer<Text, Text, Text, Text> {
	@Override
	protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		// list will collect all outlinks for a key
		List<String> list = new ArrayList<String>();
		for (Text v : values) {
			// only collect outlinks
			if (!v.toString().equals("source")) {
				list.add(v.toString());
			}
		}
		String[] outLinks = list.toArray(new String[0]);
		
		// give each page initial weight of 1
		Page page = new Page(1, outLinks);
		String flatten = page.flatten();
		context.write(key, new Text(flatten));
		context.getCounter(EnumCount.Counters.PAGECOUNT).increment(1);
	}
}