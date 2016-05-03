package edu.upenn.cis455.search;

import java.util.List;

import edu.upenn.cis455.storage.DynamoDBWrapper;
import edu.upenn.cis455.storage.SingleWordTitle;

public class TitleSearchWorker implements Runnable {

	private DynamoDBWrapper db;
	private String word;
	private List<SingleWordTitle> titleItems;
	
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
	 * @return the titleItems
	 */
	public List<SingleWordTitle> getTitleItems() {
		return titleItems;
	}

}
