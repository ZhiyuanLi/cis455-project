package edu.upenn.cis455.storage;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.dynamodb.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMapper;

@SuppressWarnings("deprecation")
public class DynamoDBWrapper {

	static AmazonDynamoDBClient dynamoDB;

	private static void init() throws Exception {
		AWSCredentials credentials = new PropertiesCredentials(
				DynamoDBWrapper.class.getResourceAsStream("AwsCredentials.properties"));

		dynamoDB = new AmazonDynamoDBClient(credentials);
		dynamoDB.setEndpoint("dynamodb.us-west-2.amazonaws.com");
	}

	public static void main(String[] args) throws Exception {
		init();

		DynamoDBMapper mapper = new DynamoDBMapper(dynamoDB);

		SingleWord word = new SingleWord();
		word.setWord("test");
		word.setUrl("http://www.google.com");
		word.setIdf(8.79);
		word.setTf_idf(4.57);
		mapper.save(word);
	}

}
