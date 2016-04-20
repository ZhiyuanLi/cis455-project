// Reducer for main crawler MapReduce job
// Author: Christopher Besser
package edu.upenn.cis455.crawler;
import java.io.IOException;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import java.util.LinkedList;
import java.util.Queue;
public class CrawlerReducer extends Reducer<Text, Text, Text, Text>
{
	/**
	 * Process and write to file the mapper outputs
	 * @param key - the value mapped from
	 * @param vals - the values mapped to
	 * @param context - the MapReduce context
	 * @throws IOException if a write error occurs
	 * @throws InterrupedException if the job is interrupted
	 */
	public void reduce(Text key, Iterable<Text> vals, Context context) throws IOException, InterruptedException
	{
		String dbDir = context.getConfiguration().get("dbDir");
		String indexDBDir = context.getConfiguration().get("indexDBDir");
		String frontierPath = context.getConfiguration().get("frontierPath");
		String URLPath = context.getConfiguration().get("URLPath");
		int maxSize = Integer.parseInt(context.getConfiguration().get("maxSize"));
		Crawler crawler = new Crawler(dbDir, indexDBDir, URLPath, maxSize, 1);
		crawler.startCrawling();
		Queue<String> frontier = crawler.getFrontier();
		for (String s: frontier)
		{
			context.write(new Text(s), new Text(""));
		}
	}
}
