package com.cybozu.labs.langdetect;
import java.util.ArrayList;
/**
 * {@link Language} is to store the detected language. {@link Detector#getProbabilities()} returns an {@link ArrayList} of {@link Language}s.
 * @see Detector#getProbabilities()
 * @author Nakatani Shuyo
 */
public class Language
{
    	public String lang;
    	public double prob;
	/**
	 * Create a language
	 * @param lang - the language name
	 * prob - the probability the document is that language
	 */
    	public Language(String lang, double prob)
	{
        	this.lang = lang;
        	this.prob = prob;
    	}

	/**
	 * Convert the language to a string
	 * @return Returns the string representation of the language
	 */
    	public String toString()
	{
        	if (lang == null)
		{
			return "";
		}
        	return lang + ":" + prob;
    	}
}
