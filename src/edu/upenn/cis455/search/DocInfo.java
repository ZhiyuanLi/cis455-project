package edu.upenn.cis455.search;

/**
 * this class stores each word info for one doc
 * 
 * @author zhiyuanli
 *
 */
public class DocInfo implements Comparable<DocInfo> {
	// public double[] tf_idfArray;
	public String url;
	public double indexScore;
	public double pagerankScore;
	public double totalScore;

	public DocInfo() {
		// TODO Auto-generated constructor stub
		// tf_idfArray = new double[length];
		// for (int i = 0; i < length; i++) {
		// tf_idfArray[i] = 0;
		// }
		url = "";
		indexScore = 0;
		pagerankScore = 0;
		totalScore = 0;
	}

	/**
	 * get the total score
	 */
	public void calculateTotalScore() {

	}

	@Override
	public int compareTo(DocInfo o) {
		//TODO: change to totalscore
		double own = this.indexScore;

		double other = o.indexScore;
		// descending order
		if (own > other) {
			return -1;
		} else if (own < other) {
			return 1;
		} else {
			return 0;
		}
	}

}
