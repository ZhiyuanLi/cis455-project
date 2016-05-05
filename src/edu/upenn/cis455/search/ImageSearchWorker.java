package edu.upenn.cis455.search;

import java.util.List;

import edu.upenn.cis455.storage.DynamoDBWrapper;
import edu.upenn.cis455.storage.ImageContent;

public class ImageSearchWorker implements Runnable{
	
	/**
	 * Instance for TitleSearchWorker
	 */
	private DynamoDBWrapper db;
	private String word;
	private List<ImageContent> imageItems;
	
	/**
	 * Constructor for TitleSearchWorker
	 * 
	 * @param db
	 * @param word
	 */
	public ImageSearchWorker(DynamoDBWrapper db, String word) {
		this.db = db;
		this.word = word;
		this.imageItems = null;
	}

	@Override
	public void run() {
		imageItems = db.getImageContentQuery(word);
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
	 * Get the title items
	 * 
	 * @return the titleItems
	 */
	public List<ImageContent> getImageItems() {
		return imageItems;
	}

}
