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
		String indexFile = context.getConfiguration().get("indexFile");
		int maxSize = Integer.parseInt(context.getConfiguration().get("maxSize"));
		String linksFile = context.getConfiguration().get("linksFile");
		String imageFile = context.getConfiguration().get("imageFile");
		String titleFile = context.getConfiguration().get("titleFile");
		int files = Integer.parseInt(context.getConfiguration().get("files"));
		LinkedList<String> list = new LinkedList<String>();
		for (Text t: vals)
		{
			list.addLast(t.toString().trim());
		}
		Crawler crawler = new Crawler(linksFile, indexFile, titleFile, imageFile, list, maxSize, files);
		crawler.startCrawling();
		Queue<String> frontier = crawler.getFrontier();
		for (String s: frontier)
		{
			context.write(new Text(s), new Text(""));
		}
	}
}
