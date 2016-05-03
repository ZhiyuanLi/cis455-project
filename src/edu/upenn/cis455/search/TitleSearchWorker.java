package edu.upenn.cis455.search;

import java.util.List;

import edu.upenn.cis455.storage.DynamoDBWrapper;
import edu.upenn.cis455.storage.SingleWordTitle;

/**
 * TitleSearchWorker implement a thread to get interact with db to retrieve
 * titleItmes from SingleWordTitle
 * 
 * @author Di Wu
 *
 */
public class TitleSearchWorker implements Runnable {

	/**
	 * Instance for TitleSearchWorker
	 */
	private DynamoDBWrapper db;
	private String word;
	private List<SingleWordTitle> titleItems;

	/**
	 * Constructor for TitleSearchWorker
	 * 
	 * @param db
	 * @param word
	 */
	public TitleSearchWorker(DynamoDBWrapper db, String word) {
		this.db = db;
		this.word = word;
		this.titleItems = null;
	}

	@Override
	public void run() {
		titleItems = db.getSingleWordTitleQuery(word);
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
	public List<SingleWordTitle> getTitleItems() {
		return titleItems;
	}

}
