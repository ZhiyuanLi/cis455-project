package edu.upenn.cis455.indexer;

import java.io.*;
import java.util.*;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class IndexerReducer extends Reducer<InterKey, InterValue, Text, Text> {

	private final int N = 15;
	private final int OUPUTSIZE = 200;

	@Override
	public void reduce(InterKey interKey, Iterable<InterValue> interValues, Context context)
			throws IOException, InterruptedException {
		int count = 0;
		System.out.println(interKey.getWord());
		ArrayList<InterValue> vTemp = new ArrayList<InterValue>();
		for (InterValue interValue : interValues) {
			System.out.println(interValue.getDocId() + "::" + interValue.getTf());
			count++;

			vTemp.add(new InterValue(new Text(interValue.getDocId()),
					new DoubleWritable(Double.parseDouble(interValue.getTf().toString()))));

		}

		double idf = Math.log((double) N / count) + 1;

		Collections.sort(vTemp);
		StringBuffer sBuffter = new StringBuffer("");
		for (int i = 0; i < OUPUTSIZE && i < count; i++) {
			InterValue v = vTemp.get(i);
			sBuffter.append(v.toString());
		}
		System.out.println(interKey.getWord() + "\t" + sBuffter.toString() + idf);
		context.write(interKey.getWord(), new Text(sBuffter.toString() + "::" + idf));
	}
}
