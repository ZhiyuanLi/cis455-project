package edu.upenn.cis455.indexer;

import java.io.*;
import java.util.*;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class IndexerReducer extends Reducer<InterKey, InterValue, Text, Text> {

	private final int N = 600;
	private final int OUPUTSIZE = 200;

	@Override
	public void reduce(InterKey interKey, Iterable<InterValue> interValues, Context context)
			throws IOException, InterruptedException {
		// variables help to reduce
		int count = 0;
		ArrayList<InterValue> vTemp = new ArrayList<InterValue>();
		// get copy of interValues
		for (InterValue interValue : interValues) {
			count++;
			vTemp.add(new InterValue(new Text(interValue.getDocId()),
					new DoubleWritable(Double.parseDouble(interValue.getTf().toString()))));
		}
		// compute idf
		double idf = Math.log((double) N / count) + 1;
		// get sort interValues by tf
		Collections.sort(vTemp);
		StringBuffer sBuffter = new StringBuffer("");
		for (int i = 0; i < OUPUTSIZE && i < count; i++) {
			InterValue v = vTemp.get(i);
			sBuffter.append(v.toString());
		}
		// emit word: <docid:tf> idf
//		System.out.println(interKey.getWord() + "\t" + sBuffter.toString() + idf);
		context.write(interKey.getWord(), new Text(sBuffter.toString() + "::" + idf));
	}
}
