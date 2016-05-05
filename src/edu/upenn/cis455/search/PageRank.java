package edu.upenn.cis455.search;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

/**
 * Load pagerank info and geolocation
 * 
 * @author zhiyuanli
 *
 */
public class PageRank {

	private static Hashtable<String, PageRankInfo> pagerank = new Hashtable<String, PageRankInfo>();

	public static void loadPageRank(String path) {
		File inputDir = new File(path);
		for (File f : inputDir.listFiles()) {
			try (BufferedReader bReader = new BufferedReader(new FileReader(f))) {
				String line;
				while ((line = bReader.readLine()) != null && !line.equals("")) {
					parseRatingsLine(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * This method is a helper method to parse line in ratings.dat file
	 * 
	 * @param line
	 *            if line consist with following format
	 *            UserID::MovieID::Rating::Timestamp, then parse the line,
	 *            otherwise print out a message
	 */
	private static void parseRatingsLine(String line) {
		String[] tempLine = line.split("\t");
		// check the format if it consist with 4 element, update corresponding
		// info, otherwise print out a message to user
		PageRankInfo pageRankInfo;
		if (tempLine.length == 4) {
			pageRankInfo = new PageRankInfo(tempLine[0], tempLine[1], tempLine[2], tempLine[3]);
			pagerank.put(tempLine[0], pageRankInfo);
		}
	}

	/**
	 * check the url exsits
	 * 
	 * @param url
	 * @return
	 */
	private static boolean contains(String url) {
		return pagerank.containsKey(url);
	}

	/**
	 * Get rank
	 * 
	 * @param url
	 * @return
	 */
	public static double getRank(String url) {
		if (contains(url)) {
			return pagerank.get(url).rank;
		}
		return 1.0;

	}

	/**
	 * Get Geolocation
	 * 
	 * @param url
	 * @return
	 */
	public static String getGeolocation(String url) {
		if (contains(url)) {
			return pagerank.get(url).state;
		}
		return "N/A";
	}

}
