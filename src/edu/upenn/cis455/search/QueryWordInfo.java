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
		this.weight = 1.0;
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
		this.weight++;
	}

	/**
	 * Get the freq of this word occurs in query
	 * 
	 * @return
	 */
	public double getFreq() {
		return this.weight;
	}

	/**
	 * Set up the word tf, update weight
	 * 
	 * @param max
	 */
	public void setTf(double maxFreq) {
		this.weight = IndexerDriver.TF_FACTOR + (1 - IndexerDriver.TF_FACTOR) * (weight/ maxFreq);
	}

	/**
	 * Set up the word idf, update weight
	 * 
	 * @param idf
	 */
	public void setIdf(double idf) {
		this.weight = weight * idf;
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
		return this.word + " : " + this.position + " : " + this.weight;
	}
}
