package edu.upenn.cis455.indexer;

import java.io.*;
import java.util.*;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class IndexerReducer extends Reducer<InterKey, InterValue, Text, Text> {

	private final int N = 610000;
	private final int OUPUTSIZE = 500;

	@Override
	public void reduce(InterKey interKey, Iterable<InterValue> interValues, Context context)
			throws IOException, InterruptedException {
		// variables help to reduce
		int count = 0;
		ArrayList<InterValue> vTemp = new ArrayList<InterValue>();
		// get copy of interValues
		for (InterValue interValue : interValues) {
			count++;
			vTemp.add(new InterValue(new Text(interValue.getDocId()), new Text(interValue.getHitsPosition()),
					new DoubleWritable(Double.parseDouble(interValue.getTf().toString()))));
		}
		// compute idf
		double idf = Math.log((double) N / count);
		// get sort interValues by tf
		Collections.sort(vTemp);
		for (int i = 0; i < OUPUTSIZE && i < count; i++) {
			InterValue v = vTemp.get(i);
			OutValue outValue = new OutValue(v.getDocId(), v.getHitsPosition(), idf, v.getTf().get() * idf);
			context.write(interKey.getWord(), new Text(outValue.toString()));
		}
	}
}
