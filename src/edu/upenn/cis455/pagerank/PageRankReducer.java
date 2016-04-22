package edu.upenn.cis455.pagerank;

import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.io.Text;
import java.io.IOException;

/**
 * collect all rank score of one url and emit <url, rankScore>; updated rank =
 * sum of (edgeValue) + 0.15 + lossCom
 * 
 * @author weisong
 *
 */
public class PageRankReducer extends Reducer<Text, Text, Text, Text> {
	double damping = 0.85;

	@Override
	protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		double rankScore = 0;
		String url = key.toString();
		Page page = null;
		Rank rank = new Rank();
		for (Text v : values) {
			String eachValue = v.toString();
			if (Page.isPage(eachValue)) { // start with L|
				// recover page
				page = new Page();
				page.unflatten(eachValue);
			} else if (Rank.isRank(eachValue)) { // start with R|
				// add rank
				rank.unflatten(eachValue);
				rankScore += rank.rank;
			}
		}
		// url that are not in database will not commit new score, so next total
		// rank sum will loss. Thus we need to add lossCom for each valid url in
		// next interation
		if (page != null) {
			// newRank = sum(edgeValue) + 0.15 + lossCom
			double constantValue = 1 - damping;
			rankScore += constantValue;
			// loss compensate: guarantee total rank score + total loss = url #
			// loss compensate = totalLoss / count
			rankScore += context.getConfiguration().getDouble("lossCompensate", 0);
			page.rank = rankScore;
			// write such as <A L|0.95|B|C|D>
			context.write(new Text(url), new Text(page.flatten()));
		}
	}
}
