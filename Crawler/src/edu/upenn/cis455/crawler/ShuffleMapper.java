// Mapper for a Mercator-style MapReduce crawler
// Author: Christopher Besser
package edu.upenn.cis455.crawler;
import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
public class ShuffleMapper extends Mapper<LongWritable, Text, Text, Text>
{
	/**
	 * Shuffle urls to worker nodes
	 * @param num - placeholder number
	 * @param line - the line
	 * @param context - the MapReduce context
	 * @throws IOException if a read/write error occurs
	 * @throws InterruptedException if a write is interrupted
	 */
	public void map(LongWritable num, Text line, Context context) throws IOException, InterruptedException
	{
		// We want to distribute lines roughly evenly, so we let shuffle step cover this
		context.write(new Text("" + System.currentTimeMillis()), new Text(line));
	}
}
