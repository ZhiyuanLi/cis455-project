package edu.upenn.cis455.pagerank;



import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import java.io.IOException;

/**
 * input is <A L|1.0|B|C|D>, output includes two parts, one is <A
 * L|rankA|B|C|D>; second is <B R|updatedRankB>, <C R|updatedRankC> etc.
 * 
 * @author weisong
 *
 */
public class PageRankMapper extends Mapper<LongWritable, Text, Text, Text> {
	double damping = 0.85;

	@Override
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		String[] pair = value.toString().trim().split("\t", 2);
		String url = pair[0];
		Page page = new Page();
		page.unflatten(pair[1]); // add outlinks to page
		// 1. write page <A L|1.0|B|C|D>
		context.write(new Text(url), new Text(page.flatten()));
		int outLinksNum = page.outLinks.length;
		double edgeValue = 0.0;
		// ignore page which do not have outlinks
		if (outLinksNum != 0) {
			// evenly spread, edge = 0.85 * previousRank / #outLinks
			edgeValue = damping * page.rank / (outLinksNum);
		}
		Rank rank = new Rank(edgeValue);
		for (String link : page.outLinks) {
			// 2. write <B R|rankScore>
			context.write(new Text(link), new Text(rank.flatten()));
		}
	}
}
