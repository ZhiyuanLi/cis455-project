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
		InterValue iv = null;
		Text docId = null;
		Text hits = null;
		DoubleWritable tf = null;
		for (InterValue interValue : interValues) {
			count++;
			docId = new Text(interValue.getDocId());
			hits = new Text(interValue.getHits());
			tf = new DoubleWritable(Double.parseDouble(interValue.getTf().toString()));
			iv = new InterValue(docId, hits, tf);
			vTemp.add(iv);
		}
		// compute idf
		double idf = Math.log((double) IndexerDriver.N / count);
		// get sort interValues by tf
		Collections.sort(vTemp);
		OutValue outValue;
		Text output = null;
		for (int i = 0; i < IndexerDriver.OUPUTSIZE && i < count; i++) {
			iv = vTemp.get(i);
			outValue = new OutValue(iv.getDocId(), iv.getHits(), idf, iv.getTf().get() * idf);
			output = new Text(outValue.toString());
			context.write(interKey, output);
		}
	}
}
