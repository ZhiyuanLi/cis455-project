package edu.upenn.cis455.search;

import java.util.List;

import edu.upenn.cis455.storage.DynamoDBWrapper;
import edu.upenn.cis455.storage.SingleWordContent;

public class ContentSearchWorker implements Runnable {

	private DynamoDBWrapper db;
	private String word;
	private List<SingleWordContent> contentItems;

	public ContentSearchWorker(DynamoDBWrapper db, String word) {
		this.db = db;
		this.word = word;
	}

	@Override
	public void run() {
		contentItems = db.getSingleWordContentQuery(word);
		System.out.println(word + " = end");
	}
	
	/**
	 * @return the word
	 */
	public String getWord() {
		return word;
	}

	/**
	 * @return the contentItems
	 */
	public List<SingleWordContent> getContentItems() {
		return contentItems;
	}

	
	
	

}
