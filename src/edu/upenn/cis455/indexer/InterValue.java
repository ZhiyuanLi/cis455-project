package edu.upenn.cis455.indexer;


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

/**
 * Implements WritableComparable, used as InterValue class
 * @author woody
 *
 */
public class InterValue implements WritableComparable<InterValue> {

	/**
	 * Instance variables for InterValue
	 */
	private Text docId;
	private Text hits;
	private DoubleWritable tf;
	
	/**
	 * Constructor
	 * @param docId
	 * @param tf
	 */
	public InterValue(String docId, String hits, double tf) {
		set(new Text(docId), new Text(hits), new DoubleWritable(tf));
	}
	
	/**
	 * Constructor
	 */
	public InterValue() {
		set(new Text(), new Text(), new DoubleWritable());
	}
	
	/**
	 * Constructor 
	 * @param docId
	 * @param tf
	 */
	public InterValue(Text docId, Text hits, DoubleWritable tf) {
		set(docId, hits, tf);
	}
	
	/**
	 * Set instance
	 * @param docId
	 * @param tf
	 */
	public void set(Text docId, Text hits, DoubleWritable tf) {
		this.docId = docId;
		this.hits = hits;
		this.tf = tf;
	}
	
	/**
	 * @return the docId
	 */
	public Text getDocId() {
		return docId;
	}

	/**
	 * @return the hits
	 */
	public Text getHits() {
		return hits;
	}

	/**
	 * @return the tf
	 */
	public DoubleWritable getTf() {
		return tf;
	}
	
	@Override
	public void readFields(DataInput arg0) throws IOException {
		docId.readFields(arg0);
		hits.readFields(arg0);
		tf.readFields(arg0);
	}

	@Override
	public void write(DataOutput arg0) throws IOException {
		docId.write(arg0);
		hits.write(arg0);
		tf.write(arg0);
	}

	@Override
	public int compareTo(InterValue o) {
		int cmp = tf.compareTo(o.tf);
		return -cmp;
//		return docId.compareTo(o.docId);
	}

	@Override
	public String toString() {
		return docId.toString() + "," + hits.toString() + "," + tf.toString() + ";";
	}
}
