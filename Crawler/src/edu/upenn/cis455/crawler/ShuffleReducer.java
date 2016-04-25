// Reducer for a Mercator-style MapReduce crawler
// Author: Christopher Besser
package edu.upenn.cis455.crawler;
import java.io.IOException;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import java.util.Iterator;
public class ShuffleReducer extends Reducer<Text, Text, Text, Text>
{
	/**
	 * The shuffler hashed assigned URLs to mappers. Just output them to files.
	 * @param url - the read URL from the frontier
	 * @param count - contains 1 element per time url appeared in the frontier
	 * @param context - the MapReduce context
	 * @throws IOException if a read/write error occurs
	 * @throws InterruptedException if a write is interrupted
	 */
	@Override
	public void reduce(Text url, Iterable<Text> count, Context context) throws IOException, InterruptedException
	{
		context.write(new Text(url), new Text(""));
		System.out.println("Writing URL: " + url);
	}
}
