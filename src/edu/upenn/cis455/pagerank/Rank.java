package edu.upenn.cis455.pagerank;



import java.util.regex.Pattern;

/**
 * rank start with $$%%
 * 
 * @author weisong
 *
 */
public class Rank {
	static String start = "$$%%";
	static String deli = "|";
	double rank = 0;

	Rank() {
	}

	Rank(double rank) {
		this.rank = rank;
	}

	/**
	 * serialize string and return a one line string representation
	 * 
	 * @param line
	 * @return
	 */
	public String flatten() {
		StringBuilder sb = new StringBuilder();
		sb.append(start);
		sb.append(deli);
		sb.append("" + rank);
		return sb.toString();
	}

	/**
	 * deserialize string and convert into object format
	 * 
	 * @param line
	 */
	public void unflatten(String line) {
		String[] pair = line.split(Pattern.quote(deli), 3);
		if (pair.length == 2) {
			rank = Double.parseDouble(pair[1]);
		}
	}

	/**
	 * check if string is a rank format
	 * 
	 * @param line
	 * @return
	 */
	public static boolean isRank(String line) {
		String[] pair = line.split(Pattern.quote(deli), 2);
		if (pair.length != 2)
			return false;
		return pair[0].equals(start);
	}
}
