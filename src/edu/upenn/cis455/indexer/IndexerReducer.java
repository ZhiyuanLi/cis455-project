package edu.upenn.cis455.indexer;

import java.io.*;
import java.util.*;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class IndexerReducer extends Reducer<Text, InterValue, Text, Text> {

	@Override
	public void reduce(Text interKey, Iterable<InterValue> interValues, Context context)
			throws IOException, InterruptedException {
		// variables help to reduce
		int count = 0;
		ArrayList<InterValue> vTemp = new ArrayList<InterValue>();
		// get copy of interValues
		for (InterValue interValue : interValues) {
			count++;
			vTemp.add(new InterValue(new Text(interValue.getDocId()), new Text(interValue.getHits()),
					new DoubleWritable(Double.parseDouble(interValue.getTf().toString()))));
		}
		// compute idf
		double idf = Math.log((double) IndexerDriver.N / count);
		// get sort interValues by tf
		Collections.sort(vTemp);
		for (int i = 0; i < IndexerDriver.OUPUTSIZE && i < count; i++) {
			InterValue v = vTemp.get(i);
			OutValue outValue = new OutValue(v.getDocId(), v.getHits(), idf, v.getTf().get() * idf);
			context.write(interKey, new Text(outValue.toString()));
		}
	}
}
