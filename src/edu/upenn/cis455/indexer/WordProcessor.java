package edu.upenn.cis455.indexer;

/**
 * This class is used to pre-process each word
 * 
 * @author woody
 *
 */
public class WordProcessor {

	/**
	 * Instance of WordProcessor
	 */
	private static StopWords STOPWORDS = new StopWords();
	private static Stemmer STEMMER = new Stemmer();
	private static String pattern = "^[a-zA-Z0-9_']*$";

	/**
	 * Pre-process word, get rid of non-english word, and just get the word
	 * befor "'"
	 * 
	 * @param word
	 * @return a string
	 */
	public static String preProcess(String word) {
		if (!word.matches(pattern)) {
			return "";
		}
		if (word.contains("'")) {
			word = word.substring(0, word.indexOf("'"));
		}
		return word;
		// if (word.isEmpty()) {
		// return word;
		// }
		// if (word.length() == 1) {
		// if (checkLetter(word.charAt(0))) {
		// return word;
		// } else {
		// return "";
		// }
		// }
		// char first = word.charAt(0);
		// char last = word.charAt(word.length() - 1);
		// boolean isFirstValid = checkLetter(first);
		// boolean isLastValid = checkLetter(last);
		// if (isFirstValid && isLastValid) {
		// return word;
		// } else if (isFirstValid && !isLastValid) {
		// return preProcess(word.substring(0, word.length() - 1));
		// } else if (!isFirstValid && isLastValid) {
		// return preProcess(word.substring(1));
		// } else {
		// return preProcess(word.substring(1, word.length() - 1));
		// }
	}

	// private static boolean checkLetter(char c) {
	// return Character.isLetter(c) || Character.isDigit(c);
	// }

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

	public static void main(String[] args) {
		System.out.println(preProcess("whyhello'sdsd"));
	}
}
