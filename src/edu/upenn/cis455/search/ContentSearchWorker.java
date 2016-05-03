package edu.upenn.cis455.search;

import java.util.List;

import edu.upenn.cis455.storage.DynamoDBWrapper;
import edu.upenn.cis455.storage.SingleWordContent;

/**
 * ContentSearchWorker is a thread to retrieve tuples from SingleWordContent
 * 
 * @author Di Wu
 *
 */
public class ContentSearchWorker implements Runnable {

	/**
	 * Instance for ContentSearchWorker
	 */
	private DynamoDBWrapper db;
	private String word;
	private List<SingleWordContent> contentItems;

	/**
	 * Constructor for ContentSearchWorker
	 * 
	 * @param db
	 * @param word
	 */
	public ContentSearchWorker(DynamoDBWrapper db, String word) {
		this.db = db;
		this.word = word;
	}

	@Override
	public void run() {
		contentItems = db.getSingleWordContentQuery(word);
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
	 * Get the item list
	 * 
	 * @return the contentItems
	 */
	public List<SingleWordContent> getContentItems() {
		return contentItems;
	}

}
