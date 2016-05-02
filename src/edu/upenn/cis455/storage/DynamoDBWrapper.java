package edu.upenn.cis455.storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
	private ArrayList<SingleWordContent> contentList;

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
		contentList = new ArrayList<SingleWordContent>();
	}

	/**
	 * query SingleWord table
	 * 
	 * @param word
	 * @return a doc list
	 */
	public List<SingleWordTitle> getSingleWordTitleQuery(String word) {
		SingleWordTitle partitionKey = new SingleWordTitle();

		partitionKey.setWord(word);
		DynamoDBQueryExpression<SingleWordTitle> queryExpression = new DynamoDBQueryExpression<SingleWordTitle>()
				.withHashKeyValues(partitionKey);

		List<SingleWordTitle> itemList = mapper.query(SingleWordTitle.class, queryExpression);

		return itemList;
	}

	/**
	 * add single word to SingleWordTitle table
	 * 
	 * @param word
	 * @param url
	 * @param hits
	 * @param idf
	 * @param tf_idf
	 */
	public void addSingleWordTitle(String word, String url, String hits, Double idf, Double tf_idf) {
		SingleWordTitle item = new SingleWordTitle();
		item.setWord(word);
		item.setUrl(url);
		item.setHits(hits);
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
	 * @param hits
	 * @param idf
	 * @param tf_idf
	 */
	public void addSingleWordContent(String word, String url, String hits, Double idf, Double tf_idf) {
		SingleWordContent item = new SingleWordContent();
		item.setWord(word);
		item.setUrl(url);
		item.setHits(hits);
		item.setIdf(idf);
		item.setTf_idf(tf_idf);
		contentList.add(item);
	}

	/**
	 * get page rank score
	 * 
	 * @param url
	 * @return
	 */
	public int getPageRankScore(String url) {
		// TODO: get pagerank
		return 0;
	}

	public void pushDataToSingWordContent(String path) throws IOException {
		File dir = new File(path);
		BufferedReader br;
		int i = 0;
		if (dir.isDirectory()) {
			for (File file : dir.listFiles()) {
				br = new BufferedReader(new FileReader(file));
				String line;
				line = br.readLine();
				while (line != null && !line.equals("")) {

					String[] s = line.split("\t");
					if (s.length == 5) {
						addSingleWordContent(s[0], s[1], s[2], Double.parseDouble(s[3]), Double.parseDouble(s[4]));
						i++;
					}
					line = br.readLine();

				}
			}

		}

		System.out.println(contentList.size());
		for (int j = 0; j <= contentList.size() / 100; j++) {
			if (j == contentList.size() / 100) {
				// System.out.println(j * 100);
				// System.out.println(contentList.size());
				mapper.batchSave(contentList.subList(j * 100, contentList.size()));
				break;
			}
			mapper.batchSave(contentList.subList(j * 100, j * 100 + 100));
			// System.out.println((j * 100) + " " + (j * 100 + 100));
			System.out.println((j * 100 + 100) + "uplaoded!");
		}

	}

	// /**
	// * query BiWord Content table
	// *
	// * @param word
	// * @return a doc list
	// */
	// public List<BiWordContent> getBiWordContentQuery(String word) {
	// BiWordContent partitionKey = new BiWordContent();
	//
	// partitionKey.setWord(word);
	// DynamoDBQueryExpression<BiWordContent> queryExpression = new
	// DynamoDBQueryExpression<BiWordContent>()
	// .withHashKeyValues(partitionKey);
	//
	// List<BiWordContent> itemList = mapper.query(BiWordContent.class,
	// queryExpression);
	//
	// return itemList;
	// }
	//
	// /**
	// * add bi word to SingleWordContent table
	// *
	// * @param word
	// * @param url
	// * @param idf
	// * @param tf_idf
	// */
	// public void addBiWordContent(String word, String url, Double idf, Double
	// tf_idf) {
	// BiWordContent item = new BiWordContent();
	// item.setWord(word);
	// item.setUrl(url);
	// item.setIdf(idf);
	// item.setTf_idf(tf_idf);
	// mapper.save(item);
	// }

	public static void main(String[] args) throws Exception {
		DynamoDBWrapper w = new DynamoDBWrapper();
		// w.addSingleWordTitle("test", "http://www.google.com", "1,2", 1.0,
		// 2.0);
		w.pushDataToSingWordContent("/Users/zhiyuanli/Downloads/content_test_input");

	}

}
