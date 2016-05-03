package edu.upenn.cis455.search;

import edu.upenn.cis455.indexer.IndexerDriver;

/**
 * @author woody
 *
 */
public class QueryWordInfo {

	/**
	 * Instance for QueryWordInfo
	 */
	private String word;
	private int position;
	private int freq;
	private double tf;
	private double weight;

	/**
	 * Constructor of QueryWordInfo
	 * 
	 * @param word
	 * @param position
	 */
	public QueryWordInfo(String word, int position) {
		this.word = word;
		this.position = position;
		this.freq = 1;
		this.tf = 0.0;
		this.weight = 0.0;
	}

	/**
	 * Get the word
	 * 
	 * @return the word
	 */
	public String getWord() {
		return word;
	}

	/**
	 * Get word in query position
	 * 
	 * @return the position
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * Update the word frequency
	 */
	public void addFreq() {
		this.freq++;
	}

	/**
	 * Get the freq of this word occurs in query
	 * 
	 * @return
	 */
	public int getFreq() {
		return this.freq;
	}

	/**
	 * Set up the word tf
	 * 
	 * @param max
	 */
	public void setTf(int max) {
		this.tf = IndexerDriver.TF_FACTOR + (1 - IndexerDriver.TF_FACTOR) * ((double) freq / max);
	}

	/**
	 * Set up the word idf
	 * 
	 * @param idf
	 */
	public void setWeight(double idf) {
		this.weight = tf * idf;
	}

	/**
	 * Get word weight in query
	 * 
	 * @return
	 */
	public double getWeight() {
		return this.weight;
	}

	@Override
	public String toString() {
		return this.word + " : " + this.tf + " : " + this.position + " : " + this.weight;
	}
}
