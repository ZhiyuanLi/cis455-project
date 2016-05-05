package edu.upenn.cis455.search;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.*;

/**
 * spell checking and corrector, word dictionary is downloaded from
 * http://raelcunha.com/spell-correct/; To start this algorithm, just invoke the
 * constructor
 * 
 * @author weisong
 *
 */
public class SpellCheck {
	// final result
	private String result = "";

	List<String> resultList;
	private static Map<String, Integer> wordFreq = new HashMap<String, Integer>();

	/**
	 * constructor to read dictionary and run algorithm.
	 * 
	 * @param path
	 */
	public SpellCheck(String inputs) {

		// clear memory
		resultList = new ArrayList<String>();
		result = "";

		// step 2: run job
		String[] inputArr = inputs.split(" ");
		if (inputArr == null || inputArr.length == 0) {
			System.out.println("invalid input");
		}
		for (int i = 0; i < inputArr.length; i++) {
			String eachResult = correct(inputArr[i]);
			// add each corrected word to resultList
			if (!eachResult.equals("")) {
				resultList.add(eachResult);
			}
		}
		for (String s : resultList) {
			result += s + " ";
		}
	}

	public static void readDict(String filePath) {
		// step 1: read dictionary
		try {
			Pattern p = Pattern.compile("\\w+");
			FileReader fr;

			fr = new FileReader(filePath);

			BufferedReader in = new BufferedReader(fr);
			String tmp = "";
			tmp = in.readLine();

			while (tmp != null) {
				Matcher matcher = p.matcher(tmp.toLowerCase());
				while (matcher.find()) {
					tmp = matcher.group();
					// System.out.println(tmp);
					if (wordFreq.containsKey(tmp)) {
						wordFreq.put(tmp, wordFreq.get(tmp) + 1);
					} else {
						wordFreq.put(tmp, 1);
					}
				}
				tmp = in.readLine();
			}
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * generate possible word sets of given word, criteria is based on delete,
	 * swap, replace and insert.
	 * 
	 * @param word
	 * @return
	 */
	private List<String> generatePossibleSet(String word) {
		List<String> result = new ArrayList<String>();
		String str = null;
		// 1. swap two neighbor character
		for (int i = 0; i < word.length() - 1; i++) {
			str = word.substring(0, i) + word.substring(i + 1, i + 2) + word.substring(i, i + 1)
					+ word.substring(i + 2);
			// System.out.println(str);
			result.add(str);
		}
		// 2. miss a character

		for (int i = 0; i < word.length(); i++) {
			str = word.substring(0, i) + word.substring(i + 1);
			// System.out.println(str);
			result.add(str);
		}
		for (int i = 0; i < word.length(); ++i) {
			for (int j = 97; j < 123; j++) {
				// 3. replace one character with another character
				str = word.substring(0, i) + String.valueOf((char) j) + word.substring(i + 1);
				result.add(str);
				// 4. insert one character between two characters
				str = word.substring(0, i) + String.valueOf((char) j) + word.substring(i);
				result.add(str);
			}
		}
		return result;
	}

	/**
	 * give corrected word given input, this function is able to handle two edit
	 * distance.
	 * 
	 * @param word
	 * @return
	 */
	public String correct(String word) {
		if (word == null || word.trim().length() == 0) {
			return "";
		}
		if (wordFreq.containsKey(word)) {
			return word;
		}
		List<String> possibleList = generatePossibleSet(word);
		// invert from <int, str> to <str, int>
		Map<Integer, String> candidateList = new HashMap<Integer, String>();
		for (String s : possibleList) {
			// step 1: add if candidate exist within one edit distance
			if (wordFreq.containsKey(s)) {
				candidateList.put(wordFreq.get(s), s);
			}
		}
		if (candidateList.size() > 0) {
			int maxKey = Collections.max(candidateList.keySet());
			String best = candidateList.get(maxKey);
			return best;
		}
		// step 2: if step 1 failed, check within two edit distance
		for (String s : possibleList) {
			for (String s1 : generatePossibleSet(s)) {
				if (wordFreq.containsKey(s1)) {
					candidateList.put(wordFreq.get(s1), s1);
				}
			}
		}
		if (candidateList.size() > 0) {
			int maxKey = Collections.max(candidateList.keySet());
			String best = candidateList.get(maxKey);
			return best;
		} else {
			return "";
		}
	}

	/**
	 * @return the result
	 */
	public String getResult() {
		return result;
	}

	/**
	 * main function to load word dictionary and check single given word and
	 * provide suggestions
	 *
	 * @param args
	 * @throws IOException
	 */
	public static void main(String args[]) throws IOException {
		String filePath = "big.txt";
		String input1 = "unaversiiy of pennsylvani";
		String input2 = "appce store";
		SpellCheck.readDict(filePath);
		SpellCheck spellCheck1 = new SpellCheck(input1);
		System.out.println(spellCheck1.getResult());
		SpellCheck spellCheck2 = new SpellCheck(input2);
		System.out.println(spellCheck2.getResult());
	}

}