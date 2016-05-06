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
		String[] tempLine = line.split("\t", 2);
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
		url = url.replace("http://", "");
		url = url.replace("https://", "");
		url = url.replace(":80", "");
		url = url.replace(":443", "");
		url = url.substring(0, url.indexOf("/"));
		return url;
	}
	
	public static void main(String[] args) {
		String url = "http://www.metmuseum.org/visit/met-fifth-avenue/mailto:mettours@metmuseum.org/";
		System.out.println(getTitle(url));
	}

}
