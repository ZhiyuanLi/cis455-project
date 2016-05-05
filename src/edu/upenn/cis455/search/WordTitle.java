package edu.upenn.cis455.search;

import java.io.*;
import java.util.Hashtable;

/**
 * Pre-load word title info
 * @author woody
 *
 */
public class WordTitle {

	private static Hashtable<String, String> wordtitle = new Hashtable<String, String>();

	public static void loadWordTitle(String path) {
		File inputDir = new File(path);
		for (File f : inputDir.listFiles()) {
			try (BufferedReader bReader = new BufferedReader(new FileReader(f))) {
				String line;
				while ((line = bReader.readLine()) != null && !line.equals("")) {
					parseWordTitleLine(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * This method is a helper method to parse WordTitleLine
	 * 
	 * @param line
	 */
	private static void parseWordTitleLine(String line) {
		String[] tempLine = line.split("\t");
		if (tempLine.length == 2) {
			wordtitle.put(tempLine[0], tempLine[1]);
		}
	}

	/**
	 * check the url exsits
	 * 
	 * @param url
	 * @return
	 */
	private static boolean contains(String url) {
		return wordtitle.containsKey(url);
	}

	/**
	 * Get rank
	 * 
	 * @param url
	 * @return
	 */
	public static String getTitle(String url) {
		if (contains(url)) {
			return wordtitle.get(url);
		}
		return url.replace("http://", "").replace("https://", "").replace(":80", "").replace(":443", "").substring(0,
				url.indexOf("/"));
	}

}
