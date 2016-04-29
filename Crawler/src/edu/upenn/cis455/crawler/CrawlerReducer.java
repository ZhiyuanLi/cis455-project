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
	@Override
	public void reduce(Text key, Iterable<Text> vals, Context context) throws IOException, InterruptedException
	{
		String indexDBDir = context.getConfiguration().get("indexDBDir");
		String frontierPath = context.getConfiguration().get("frontierPath");
		int maxSize = Integer.parseInt(context.getConfiguration().get("maxSize"));
		String linksPath = context.getConfiguration().get("linksPath");
		String imgsDBDir = context.getConfiguration().get("imgsDBDir");
		int files = Integer.parseInt(context.getConfiguration().get("files"));
		LinkedList<String> list = new LinkedList<String>();
		for (Text t: vals)
		{
			list.addLast(t.toString().trim());
		}
		Crawler crawler = new Crawler(indexDBDir, imgsDBDir, list, linksPath, maxSize, files);
		crawler.startCrawling();
		Queue<String> frontier = crawler.getFrontier();
		for (String s: frontier)
		{
			context.write(new Text(s), new Text(""));
		}
	}
}
