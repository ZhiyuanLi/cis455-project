package edu.upenn.cis455.indexer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

/**
 * Implement WritabeComparable, used as OutValue class 
 * @author woody
 *
 */
public class OutValue implements WritableComparable<OutValue> {

	/**
	 * Instance variable for OutValue
	 */
	private Text docId;
	private Text hits;
	private DoubleWritable idf;
	private DoubleWritable tf_idf;
	
	/**
	 * Constructor of OutValue
	 * @param docId
	 * @param idf
	 * @param tf_idf
	 */
	public OutValue(Text docId, Text hits, double idf, double tf_idf) {
		set(docId, hits, new DoubleWritable(idf), new DoubleWritable(tf_idf));	
	}
	
	/**
	 * Constructor of OutValue
	 */
	public OutValue() {
		set(new Text(), new Text(), new DoubleWritable(), new DoubleWritable());
	}
	
	/**
	 * set instance
	 * @param docId
	 * @param idf
	 * @param tf_idf
	 */
	public void set(Text docId, Text hits, DoubleWritable idf, DoubleWritable tf_idf) {
		this.docId = docId;
		this.hits = hits;
		this.idf = idf;
		this.tf_idf = tf_idf;
	}
	
	@Override
	public void readFields(DataInput arg0) throws IOException {
		docId.readFields(arg0);
		hits.readFields(arg0);
		idf.readFields(arg0);
		tf_idf.readFields(arg0);
		
	}
	@Override
	public void write(DataOutput arg0) throws IOException {
		docId.write(arg0);
		hits.write(arg0);
		idf.write(arg0);
		tf_idf.write(arg0);
		
	}
	@Override
	public int compareTo(OutValue o) {
		int cmp = tf_idf.compareTo(o.tf_idf);
		return -cmp;
	}
	
	@Override
	public String toString() {
		return this.docId.toString() + "\t" + this.hits.toString() + "\t" + this.idf.toString() + "\t" + this.tf_idf.toString();
	}
	
}
