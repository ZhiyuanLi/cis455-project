// Mapper for main Crawler MapReduce job
// Author: Christopher Besser
package edu.upenn.cis455.crawler;
import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
public class CrawlerMapper extends Mapper<LongWritable, Text, Text, Text>
{
	/**
	 * Read a line and send information to the reducer
	 * @param num - placeholder number
	 * @param line - the line read in from the URL list
	 * @param context - the MapReduce context
	 * @throws IOException if a read error occurs
	 * @throws InterruptedException if the job is interrupted
	 */
	public void map(LongWritable num, Text line, Context context) throws IOException, InterruptedException
	{
		context.write(new Text(line), new Text(""));
	}
}
