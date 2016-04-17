package edu.upenn.cis455.indexer;

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
