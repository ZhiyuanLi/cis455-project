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
	 * @param docContent
	 */
	public Tokenizer(String docContent) {
		set(docContent);
	}

	/**
	 * Set stream tokenizer, just get word
	 * 
	 * @param word
	 */
	public void set(String docContent) {
		sTokenizer = new StreamTokenizer(new StringReader(docContent));
		sTokenizer.resetSyntax();
		sTokenizer.whitespaceChars('\u0000', '\u0020');
		sTokenizer.wordChars('a', 'z');
		sTokenizer.wordChars('A', 'Z');
		sTokenizer.wordChars('0', '9');
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

//	public static void main(String[] args) throws IOException {
//		Tokenizer t = new Tokenizer(
//				"00008b03 forbes? 是这样吗 you'll \"log in with_your_ social account: or, you can+©time - log in or sign up using forbes. new posts most popular forbes 2016 tax guide lists global game changers video kobe bryant's stats edition: u.s. | europe | asia help | connect | sign up| log in 2 free issues of forbes follow forbes forbes in the news if you missed tax day, here's what to do kelly phillips erb, forbes staff today's top stories yahoo revenue and earnings sink as bids roll in brian solomon, forbes staff herbalife whistle-blower"
//				+ "    sues his lawyers for negligence nathan vardi, forbes staff two 24-year-old women raise $1.7 m for tech to streamline construction projects clare o'connor, forbes staff toyota workers in kentucky elevate their senses to properly build a lexus joann muller, forbes staff windows users, stop using and uninstall quicktime as soon as possible abigail tracy, forbes staff the best business schools for veterans 2016 karsten strauss, forbes staff hamilton backstage: meet the rising stars of broadway most popular sign up for today's top stories thanks for signing up. the 6 dominant action styles: why you need to know yours to be happy and successful active on linkedin conor mcgregor retires: latest news, rumors and analysis on ufc star's shocking tweet +38,296 views ");
//		while (t.hasNext()) {
//			System.out.println(t.nextToken());
//		}
//	}
}
