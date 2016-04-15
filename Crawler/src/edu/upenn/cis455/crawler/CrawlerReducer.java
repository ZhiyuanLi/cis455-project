// Reducer for main crawler MapReduce job
// Author: Christopher Besser
package edu.upenn.cis455.crawler;
import java.io.IOException;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
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
	public void reduce(Text key, Text vals, Context context) throws IOException, InterruptedException
	{
	}
}
