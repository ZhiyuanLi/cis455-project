package edu.upenn.cis455.pagerank;



import java.util.regex.Pattern;

/**
 * include url and list of links, start with ##@@
 * 
 * @author weisong
 *
 */
public class Page {
	static String start = "##@@";
	static String deli = "|";
	String[] outLinks;
	double rank = 0;

	Page() {

	}

	Page(double rank, String[] outLinks) {
		this.rank = rank;
		this.outLinks = outLinks;
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
		for (String link : outLinks) {
			sb.append(deli);
			sb.append(link);
		}
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
			outLinks = new String[0];
		} else if (pair.length == 3) {
			rank = Double.parseDouble(pair[1]);
			outLinks = pair[2].split(Pattern.quote(deli));
		}
	}

	/**
	 * check if string is a page format
	 * 
	 * @param line
	 * @return
	 */
	public static boolean isPage(String line) {
		String[] pair = line.split(Pattern.quote(deli), 2);
		if (pair.length < 2)
			return false;
		return pair[0].equals(start);
	}

}