package edu.upenn.cis455.indexer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is used to pre-process each word
 * @author woody
 *
 */
public class WordProcessor {

	/**
	 * Instance of WordProcessor
	 */
	private static StopWords STOPWORDS = new StopWords();
	private static Stemmer STEMMER = new Stemmer();

	/**
	 * Pre-process word, get rid of non-word character at the end of the word, such as '?' '.' ','
	 * @param word
	 * @return a string
	 */
	public static String preProcess(String word) {
		Pattern p = Pattern.compile("(\\p{L}+(?:-?\\p{L}+)*)+\\W*");
		Matcher m = p.matcher(word);
		if (m.matches()) { 
			word = m.group(1);
		}
		return word;
	}
	
	/**
	 * word processor, get rid of stopwords, stemmer word
	 * 
	 * @param word
	 *            to be process
	 * @return either a processed word or null
	 */
	public static String process(String word) {
		if (!STOPWORDS.isStopWord(word)) {
			STEMMER.add(word.toCharArray(), word.length());
			STEMMER.stem();
			return STEMMER.toString();
		} else {
			return null;
		}
	}
}
