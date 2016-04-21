package edu.upenn.cis455.indexer;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;

/**
 * This class is used to pre-process file content
 * 
 * @author woody
 *
 */
public class Tokenizer {

	/**
	 * Instance of Tokenizer
	 */
	private StreamTokenizer sTokenizer;

	/**
	 * Constructor of Tokenizer
	 * 
	 * @param fileContent
	 */
	public Tokenizer(String fileContent) {
		set(fileContent);
	}

	/**
	 * Set stream tokenizer
	 * 
	 * @param word
	 */
	public void set(String fileContent) {
		sTokenizer = new StreamTokenizer(new StringReader(fileContent));
		sTokenizer.resetSyntax();
		sTokenizer.whitespaceChars('\u0000', '\u0020');
		sTokenizer.wordChars('a', 'z');
		sTokenizer.wordChars('A', 'Z');
		sTokenizer.wordChars('0', '9');
		sTokenizer.wordChars('_', '_');
	}

	/**
	 * @return a string of next token
	 * @throws IOException
	 */
	public String nextToken() throws IOException {
		if (sTokenizer.ttype == StreamTokenizer.TT_WORD) {
			return sTokenizer.sval;
		} else if (sTokenizer.ttype == StreamTokenizer.TT_NUMBER) {
			return String.valueOf(sTokenizer.nval);
		} else {
			return "";
		}
	}

	/**
	 * @return a boolean, is has next token
	 * @throws IOException
	 */
	public boolean hasNext() throws IOException {
		sTokenizer.nextToken();
		return (sTokenizer.ttype != StreamTokenizer.TT_EOF && sTokenizer.ttype != StreamTokenizer.TT_EOL);
	}
	
	public static void main(String[] args) throws IOException {
		
		Tokenizer t = new Tokenizer("z�e	iA��[ۄ'�");
		while (t.hasNext()) {
			System.out.println(t.nextToken());
		}
	}
}
