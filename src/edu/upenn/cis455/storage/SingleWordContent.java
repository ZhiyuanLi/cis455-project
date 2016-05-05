package edu.upenn.cis455.storage;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
 * Single word in content
 * 
 * @author zhiyuanli
 *
 */
@DynamoDBTable(tableName = "Content")
public class SingleWordContent {
	private String word;
	private String url;
	private String hits;
	private Double idf;
	private Double tf_idf;

	/**
	 * @return the word
	 */
	@DynamoDBHashKey(attributeName = "word")
	public String getWord() {
		return word;
	}

	/**
	 * @param word
	 *            the word to set
	 */
	public void setWord(String word) {
		this.word = word;
	}

	/**
	 * @return the url
	 */
	@DynamoDBRangeKey(attributeName = "url")
	public String getUrl() {
		return url;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the hits
	 */
	@DynamoDBAttribute(attributeName = "hits")
	public String getHits() {
		return hits;
	}

	/**
	 * @param hits
	 *            the hits to set
	 */
	public void setHits(String hits) {
		this.hits = hits;
	}

	/**
	 * @return the idf
	 */
	@DynamoDBAttribute(attributeName = "idf")
	public Double getIdf() {
		return idf;
	}

	/**
	 * @param idf
	 *            the idf to set
	 */
	public void setIdf(Double idf) {
		this.idf = idf;
	}

	/**
	 * @return the tf_idf
	 */
	@DynamoDBAttribute(attributeName = "tf_idf")
	public Double getTf_idf() {
		return tf_idf;
	}

	/**
	 * @param tf_idf
	 *            the tf_idf to set
	 */
	public void setTf_idf(Double tf_idf) {
		this.tf_idf = tf_idf;
	}

}
