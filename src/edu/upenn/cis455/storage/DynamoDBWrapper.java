package edu.upenn.cis455.storage;

import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

/**
 * wrapper class to interact with DynamoDB
 * 
 * @author zhiyuanli
 *
 */
public class DynamoDBWrapper {

	private AmazonDynamoDBClient client;
	private DynamoDBMapper mapper;

	/**
	 * Constructor
	 * 
	 * @throws Exception
	 */
	public DynamoDBWrapper() throws Exception {

		// AWSCredentials credentials = new PropertiesCredentials(
		// DynamoDBWrapper.class.getResourceAsStream("AwsCredentials.properties"));

		AWSCredentials credentials = new BasicAWSCredentials("AKIAI5RD7TPQ6HRG5NEQ",
				"FdMAW9DZrDdEKca0VLQx+vVgEnE38U9aMsokhadi");
		client = new AmazonDynamoDBClient(credentials);
		client.setEndpoint("dynamodb.us-west-1.amazonaws.com");

		mapper = new DynamoDBMapper(client);
	}

	/**
	 * query SingleWord table
	 * 
	 * @param word
	 * @return a doc list
	 */
	public List<SingleWord> getSingleWordQuery(String word) {
		SingleWord partitionKey = new SingleWord();

		partitionKey.setWord(word);
		DynamoDBQueryExpression<SingleWord> queryExpression = new DynamoDBQueryExpression<SingleWord>()
				.withHashKeyValues(partitionKey);

		List<SingleWord> itemList = mapper.query(SingleWord.class, queryExpression);

		return itemList;
	}

	/**
	 * add single word to SingleWord table
	 * 
	 * @param word
	 * @param url
	 * @param idf
	 * @param tf_idf
	 */
	public void addSingleWord(String word, String url, Double idf, Double tf_idf) {
		SingleWord item = new SingleWord();
		item.setWord(word);
		item.setUrl(url);
		item.setIdf(idf);
		item.setTf_idf(tf_idf);
		mapper.save(item);
	}

	/**
	 * query SingleWord Content table
	 * 
	 * @param word
	 * @return a doc list
	 */
	public List<SingleWordContent> getSingleWordContentQuery(String word) {
		SingleWordContent partitionKey = new SingleWordContent();

		partitionKey.setWord(word);
		DynamoDBQueryExpression<SingleWordContent> queryExpression = new DynamoDBQueryExpression<SingleWordContent>()
				.withHashKeyValues(partitionKey);

		List<SingleWordContent> itemList = mapper.query(SingleWordContent.class, queryExpression);

		return itemList;
	}

	/**
	 * add single word to SingleWordContent table
	 * 
	 * @param word
	 * @param url
	 * @param position
	 * @param idf
	 * @param tf_idf
	 */
	public void addSingleWordContent(String word, String url, String position, Double idf, Double tf_idf) {
		SingleWordContent item = new SingleWordContent();
		item.setWord(word);
		item.setUrl(url);
		item.setPosition(position);
		item.setIdf(idf);
		item.setTf_idf(tf_idf);
		mapper.save(item);
	}

	/**
	 * query BiWord Content table
	 * 
	 * @param word
	 * @return a doc list
	 */
	public List<BiWordContent> getBiWordContentQuery(String word) {
		BiWordContent partitionKey = new BiWordContent();

		partitionKey.setWord(word);
		DynamoDBQueryExpression<BiWordContent> queryExpression = new DynamoDBQueryExpression<BiWordContent>()
				.withHashKeyValues(partitionKey);

		List<BiWordContent> itemList = mapper.query(BiWordContent.class, queryExpression);

		return itemList;
	}

	/**
	 * add bi word to SingleWordContent table
	 * 
	 * @param word
	 * @param url
	 * @param idf
	 * @param tf_idf
	 */
	public void addBiWordContent(String word, String url, Double idf, Double tf_idf) {
		BiWordContent item = new BiWordContent();
		item.setWord(word);
		item.setUrl(url);
		item.setIdf(idf);
		item.setTf_idf(tf_idf);
		mapper.save(item);
	}

	public static void main(String[] args) throws Exception {
		DynamoDBWrapper w = new DynamoDBWrapper();
		w.addSingleWord("test", "http://www.google.com", 1.0, 2.0);
		// List<SingleWord> items = w.getSingleWordQuery("test");
		// for (SingleWord item : items) {
		// System.out.println(item.getWord() + " " + item.getUrl() + " " +
		// item.getIdf() + " " + item.getTf_idf());
		// }

	}

}
