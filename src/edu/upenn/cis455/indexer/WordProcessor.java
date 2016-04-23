package edu.upenn.cis455.indexer;

/**
 * This class offers method to process with words
 * 
 * @author woody
 *
 */
public class WordProcessor {

	/**
	 * Instance of WordProcessor
	 */
	private static String pattern = "^[a-zA-Z_]*$";
	private static StopWords STOPWORDS = new StopWords();
	private static Stemmer STEMMER = new Stemmer();

	/**
	 * Pre-process word, get rid of non-english word, get rid of word that
	 * appears in stop list
	 * 
	 * @param word
	 * @return a string
	 */
	public static String preProcess(String word) {
		if (!word.matches(pattern) || STOPWORDS.isStopWord(word)) {
			return "";
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

	/**
	 * concat two words with special connectors
	 * 
	 * @param word1
	 *            to be connect
	 * @param word2
	 *            to be connect
	 * @return a connected word
	 */
	public static String concat(String word1, String word2) {
		return word1.concat("=&=" + word2);
	}

	public static void main(String[] args) {
		System.out.println(preProcess("可不是呢"));
		System.out.println(concat("ny", "times"));
	}
}
