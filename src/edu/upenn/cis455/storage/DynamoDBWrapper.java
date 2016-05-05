package edu.upenn.cis455.storage;

import java.io.*;
import java.util.*;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.planetj.math.rabinhash.RabinHashFunction32;
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
	// TODO:
	private ArrayList<SingleWordTitle> titleList;
	private ArrayList<SingleWordContent> contentList;
	private ArrayList<ImageContent> imageList;
	private RabinHashFunction32 hash = RabinHashFunction32.DEFAULT_HASH_FUNCTION;
	private HashSet<Integer> URLhashes = new HashSet<Integer>();

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
		client.setEndpoint("dynamodb.us-east-1.amazonaws.com");

		mapper = new DynamoDBMapper(client);
		titleList = new ArrayList<SingleWordTitle>();
		contentList = new ArrayList<SingleWordContent>();
		imageList = new ArrayList<ImageContent>();
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
	 * add tuple to SingleWordTitle table
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
		titleList.add(item);
	}

	/**
	 * query SingleWordContent table
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
	 * add tuple to SingleWordContent table
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
	 * query ImageContent table
	 * 
	 * @param word
	 * @return a doc list
	 */
	public List<ImageContent> getImageContentQuery(String word) {
		ImageContent partitionKey = new ImageContent();

		partitionKey.setWord(word);
		DynamoDBQueryExpression<ImageContent> queryExpression = new DynamoDBQueryExpression<ImageContent>()
				.withHashKeyValues(partitionKey);

		List<ImageContent> itemList = mapper.query(ImageContent.class, queryExpression);

		return itemList;
	}

	/**
	 * add tuple to ImageContent table
	 * 
	 * @param word
	 * @param url
	 * @param hits
	 * @param idf
	 * @param tf_idf
	 */
	public void addImageContent(String word, String url, String hits, Double idf, Double tf_idf) {
		ImageContent item = new ImageContent();
		item.setWord(word);
		item.setUrl(url);
		item.setHits(hits);
		item.setIdf(idf);
		item.setTf_idf(tf_idf);
		imageList.add(item);
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

	/**
	 * upload data to SingleWordTitle table
	 * 
	 * @param path
	 * @throws IOException
	 */
	public void pushDataToSingleWordTitle(String path) throws IOException {
		File dir = new File(path);
		BufferedReader br;
		if (dir.isDirectory()) {
			for (File file : dir.listFiles()) {
				br = new BufferedReader(new FileReader(file));
				String line;
				line = br.readLine();
				while (line != null && !line.equals("")) {
					String[] s = line.split("\t");
					String key = null;
					int checksum = 0;
					if (s.length == 5) {
						key = s[0] + " " + s[1];
						checksum = hash.hash(key);
						if (!URLhashes.contains(checksum)) {
							addSingleWordTitle(s[0], s[1], s[2], Double.parseDouble(s[3]), Double.parseDouble(s[4]));
							URLhashes.add(checksum);
						}
					}
					line = br.readLine();
				}

				System.out.println(titleList.size());
				for (int j = 0; j <= titleList.size() / 100; j++) {
					if (j == titleList.size() / 100) {
						mapper.batchSave(titleList.subList(j * 100, titleList.size()));
						break;
					}
					mapper.batchSave(titleList.subList(j * 100, j * 100 + 100));
					System.out.println((j * 100 + 100) + "uplaoded!");
				}
				System.out.println(file.getName() + " done!");
				URLhashes.clear();
				titleList.clear();
			}
		}
	}

	/**
	 * upload data to SingleWordContent table
	 * 
	 * @param path
	 * @throws IOException
	 */
	public void pushDataToSingleWordContent(String path) throws IOException {
		File dir = new File(path);
		BufferedReader br;
		if (dir.isDirectory()) {
			for (File file : dir.listFiles()) {
				br = new BufferedReader(new FileReader(file));
				String line;
				line = br.readLine();
				while (line != null && !line.equals("")) {
					String[] s = line.split("\t");
					String key = null;
					int checksum = 0;
					if (s.length == 5) {
						key = s[0] + " " + s[1];
						checksum = hash.hash(key);
						if (!URLhashes.contains(checksum)) {
							addSingleWordContent(s[0], s[1], s[2], Double.parseDouble(s[3]), Double.parseDouble(s[4]));
							URLhashes.add(checksum);
						}
					}
					line = br.readLine();
				}
				System.out.println(contentList.size());
				for (int j = 0; j <= contentList.size() / 100; j++) {
					if (j == contentList.size() / 100) {
						mapper.batchSave(contentList.subList(j * 100, contentList.size()));
						break;
					}
					mapper.batchSave(contentList.subList(j * 100, j * 100 + 100));
					System.out.println((j * 100 + 100) + " uploaded!");
				}
				System.out.println(file.getName() + " done");
				URLhashes.clear();
				contentList.clear();
			}
		}
	}

	/**
	 * upload data to ImageContent table
	 * 
	 * @param path
	 * @throws IOException
	 */
	public void pushDataToImageContent(String path) throws IOException {
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
					String key = null;
					int checksum = 0;
					if (s.length == 5) {
						key = s[0] + " " + s[1];
						checksum = hash.hash(key);
						i++;
						if (!URLhashes.contains(checksum)) {
							addImageContent(s[0], s[1], s[2], Double.parseDouble(s[3]), Double.parseDouble(s[4]));
							URLhashes.add(checksum);
						}
					}
					line = br.readLine();
				}
				System.out.println("i : " + i);
				System.out.println(imageList.size());
				for (int j = 0; j <= imageList.size() / 100; j++) {
					if (j == imageList.size() / 100) {
						mapper.batchSave(imageList.subList(j * 100, imageList.size()));
						break;
					}
					mapper.batchSave(imageList.subList(j * 100, j * 100 + 100));
					System.out.println((j * 100 + 100) + " uploaded!");
				}
				System.out.println(file.getName() + " done");
				URLhashes.clear();
				imageList.clear();
			}
		}
	}

	public static void main(String[] args) throws Exception {
		final DynamoDBWrapper w21 = new DynamoDBWrapper();
		final DynamoDBWrapper w22 = new DynamoDBWrapper();
		final DynamoDBWrapper w23 = new DynamoDBWrapper();
		final DynamoDBWrapper w24 = new DynamoDBWrapper();
		final DynamoDBWrapper w25 = new DynamoDBWrapper();
//		final DynamoDBWrapper w26 = new DynamoDBWrapper();
//		final DynamoDBWrapper w27 = new DynamoDBWrapper();
//		final DynamoDBWrapper w28 = new DynamoDBWrapper();
//		final DynamoDBWrapper w29 = new DynamoDBWrapper();
//		final DynamoDBWrapper w30 = new DynamoDBWrapper();
		// switch (args[0]) {
		// case "title":
		// w.pushDataToSingleWordTitle("/Users/woody/Downloads/455ProjectData/IndexerOutput/TitleOut");
		// break;
		// case "content":
		// w.pushDataToSingleWordContent("/Users/woody/Downloads/455ProjectData/IndexerOutput/ContentOut");
		// break;
		// case "image":
		// w.pushDataToImageContent("/Users/woody/Downloads/455ProjectData/IndexerOutput/ImageOut");
		//
		// break;
		// }
		Thread[] threads = new Thread[5];
		threads[0] = new Thread() {

			public void run() {
				try {
					w21.pushDataToSingleWordContent("/Users/woody/Downloads/455ProjectData/IndexerOutput/SmallContentOut/16");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		threads[1] = new Thread() {

			public void run() {
				try {
					w22.pushDataToSingleWordContent("/Users/woody/Downloads/455ProjectData/IndexerOutput/SmallContentOut/17");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		threads[2] = new Thread() {

			public void run() {
				try {
					w23.pushDataToSingleWordContent("/Users/woody/Downloads/455ProjectData/IndexerOutput/SmallContentOut/18");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		
		threads[3] = new Thread() {

			public void run() {
				try {
					w24.pushDataToSingleWordContent("/Users/woody/Downloads/455ProjectData/IndexerOutput/SmallContentOut/19");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		threads[4] = new Thread() {

			public void run() {
				try {
					w25.pushDataToSingleWordContent("/Users/woody/Downloads/455ProjectData/IndexerOutput/SmallContentOut/20");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

//		threads[5] = new Thread() {
//
//			public void run() {
//				try {
//					w26.pushDataToSingleWordContent("/Users/woody/Downloads/455ProjectData/IndexerOutput/SmallContentOut/26");
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		};
//		
//		threads[6] = new Thread() {
//
//			public void run() {
//				try {
//					w27.pushDataToSingleWordContent("/Users/woody/Downloads/455ProjectData/IndexerOutput/SmallContentOut/27");
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		};
//
//		threads[7] = new Thread() {
//
//			public void run() {
//				try {
//					w28.pushDataToSingleWordContent("/Users/woody/Downloads/455ProjectData/IndexerOutput/SmallContentOut/28");
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		};
//
//		threads[8] = new Thread() {
//
//			public void run() {
//				try {
//					w29.pushDataToSingleWordContent("/Users/woody/Downloads/455ProjectData/IndexerOutput/SmallContentOut/29");
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		};
//		
//		threads[9] = new Thread() {
//
//			public void run() {
//				try {
//					w30.pushDataToSingleWordContent("/Users/woody/Downloads/455ProjectData/IndexerOutput/SmallContentOut/30");
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		};
		
		threads[0].start();
		threads[1].start();
		threads[2].start();
		
		threads[3].start();
		threads[4].start();
//		threads[5].start();
		
//		threads[6].start();
//		threads[7].start();
//		threads[8].start();
//		threads[9].start();
//		threads[3].start();

	}
}
