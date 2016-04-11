// Reducer for a Mercator-style MapReduce crawler
// Author: Christopher Besser
package edu.upenn.cis455.crawler;
import java.io.IOException;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
public class CrawlerReducer extends Reducer<Text, Text, Text, Text>
{
	/**
	 * The shuffler pseudo-randomly assigned lines to mappers. Just output them.
	 * @param node - the current node
	 * @param urls - the list of lines
	 * @param context - the MapReduce context
	 * @throws IOException if a read/write error occurs
	 * @throws InterruptedException if a write is interrupted
	 */
	public void reduce(Text node, Iterable<Text> urls, Context context) throws IOException, InterruptedException
	{
		for (Text t: urls)
		{
			context.write(new Text(t), new Text(""));
		}
	}
}
