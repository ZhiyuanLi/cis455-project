package com.cybozu.labs.langdetect;
/**
 * @author Nakatani Shuyo
 */
enum ErrorCode
{
	NoTextError, FormatError, FileLoadError, DuplicateLangError, NeedLoadProfileError, CantDetectError, CantOpenTrainData, TrainDataFormatError, InitParamError
}
/**
 * @author Nakatani Shuyo
 */
public class LangDetectException extends Exception
{
    	private static final long serialVersionUID = 1L;
    	private ErrorCode code;
    	/**
	 * Create a new LangDetectException
    	 * @param code - the error code
    	 * @param message - the message of what went wrong
    	 */
    	public LangDetectException(ErrorCode code, String message)
	{
        	super(message);
        	this.code = code;
    	}

    	/**
    	 * Retrieve the error code
	 * @return the error code
    	 */
    	public ErrorCode getCode()
	{
        	return code;
    	}
}
