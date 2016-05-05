package edu.upenn.cis455.search;

public class PageRankInfo {

	public String url;
	public double rank;
	public String state;
	public String city;

	public PageRankInfo(String url, String rank, String state, String city) {
			this.url = url;
			this.rank = Double.parseDouble(rank);
			this.state = state;
			this.city = city;
		}

}
