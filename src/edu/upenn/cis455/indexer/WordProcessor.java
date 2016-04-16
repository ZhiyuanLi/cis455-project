package edu.upenn.cis455.indexer;

public class WordProcessor {

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
			System.out.println(STEMMER);
			return STEMMER.toString();
		} else {
			return null;
		}
	}
//	for test
//	public static void main(String[] args) {
//		WordProcessor.process("syzys");
//	}
}
