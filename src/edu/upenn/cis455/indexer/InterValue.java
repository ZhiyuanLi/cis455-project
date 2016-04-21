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
	private DoubleWritable tf;

	/**
	 * Constructor
	 * @param docId
	 * @param tf
	 */
	public InterValue(String docId, double tf) {
		set(new Text(docId), new DoubleWritable(tf));
	}
	
	/**
	 * Constructor
	 */
	public InterValue() {
		set(new Text(), new DoubleWritable());
	}
	
	/**
	 * Constructor 
	 * @param docId
	 * @param tf
	 */
	public InterValue(Text docId, DoubleWritable tf) {
		set(docId, tf);
	}
	
	/**
	 * Set instance
	 * @param docId
	 * @param tf
	 */
	public void set(Text docId, DoubleWritable tf) {
		this.docId = docId;
		this.tf = tf;
	}
	
	/**
	 * @return the docId
	 */
	public Text getDocId() {
		return docId;
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
		tf.readFields(arg0);
	}

	@Override
	public void write(DataOutput arg0) throws IOException {
		docId.write(arg0);
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
		return docId.toString() + "," + tf.toString() + ";";
	}
}
