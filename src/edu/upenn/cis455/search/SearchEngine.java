package edu.upenn.cis455.search;

public class SearchEngine {

	private String query;
	private String[] words;

	public SearchEngine() {
		// TODO Auto-generated constructor stub
		init();
	}

	private void init() {

	}

	public void setQuery(String query) {
		this.query = query;
		words = query.split("\\b");
	}

}
