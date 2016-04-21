package edu.upenn.cis455.storage;

import com.amazonaws.services.dynamodb.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBTable;

@SuppressWarnings("deprecation")
@DynamoDBTable(tableName="SingleWord")
public class SingleWord {
	private String word;
	private String url;
	private Double idf;
	private Double tf_idf;
	/**
	 * @return the word
	 */
	@DynamoDBHashKey(attributeName="word")
	public String getWord() {
		return word;
	}
	/**
	 * @param word the word to set
	 */
	public void setWord(String word) {
		this.word = word;
	}
	/**
	 * @return the url
	 */
	@DynamoDBRangeKey(attributeName="url")
	public String getUrl() {
		return url;
	}
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	/**
	 * @return the idf
	 */
	public Double getIdf() {
		return idf;
	}
	/**
	 * @param idf the idf to set
	 */
	public void setIdf(Double idf) {
		this.idf = idf;
	}
	/**
	 * @return the tf_idf
	 */
	public Double getTf_idf() {
		return tf_idf;
	}
	/**
	 * @param tf_idf the tf_idf to set
	 */
	public void setTf_idf(Double tf_idf) {
		this.tf_idf = tf_idf;
	}
	
	
	
	
}
