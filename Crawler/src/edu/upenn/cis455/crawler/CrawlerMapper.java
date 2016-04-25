// Mapper for main Crawler MapReduce job
// Author: Christopher Besser
package edu.upenn.cis455.crawler;
import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import java.net.URL;
import java.net.MalformedURLException;
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
		String host = getHost("" + line);
		if (host != null)
		{
			context.write(new Text(host), new Text(line));
		}
	}

	/**
	 * Get the host of the URL
	 * @param url - the requested URL
	 * @return host - Returns the host
	 */
	private String getHost(String url)
	{
		try
		{
			return new URL(url).getHost();
		}
		catch (MalformedURLException e)
		{
			return null;
		}
	}
}
