package edu.upenn.cis455.indexer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

/**
 * Implement WritabeComparable, used as InterKey class 
 * @author woody
 *
 */
public class InterKey implements WritableComparable<InterKey>{

	/**
	 * Instance variable for InterKey
	 */
	private Text word;

	/**
	 * Constructor of InterKey
	 * @param word
	 */
	public InterKey(String word) {
		set(new Text(word));
	}

	public InterKey() {
		set(new Text());
	}
	
	public void set(Text word) {
		this.word = word;
	}

	/**
	 * @return the word
	 */
	public Text getWord() {
		return word;
	}

	@Override
	public void readFields(DataInput arg0) throws IOException {
		word.readFields(arg0);		
	}

	@Override
	public void write(DataOutput arg0) throws IOException {
		word.write(arg0);	
	}

	@Override
	public int compareTo(InterKey o) {
		return word.compareTo(o.word);
	}
}
