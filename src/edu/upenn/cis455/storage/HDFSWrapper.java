package edu.upenn.cis455.storage;

import java.io.*;
import java.util.*;
import java.net.*;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.conf.*;

/**
 * wrapper to read files from bdb and write to hdfs
 * 
 * @author zhiyuanli
 *
 */
public class HDFSWrapper {

	private Configuration configuration;
	private FileSystem fs;
	private DatabaseWrapper db;
	private DynamoDBWrapper dynamoDB;

	/**
	 * init
	 * 
	 * @throws Exception
	 */
	public HDFSWrapper() throws Exception {
		// TODO Auto-generated constructor stub
		db = new DatabaseWrapper("indexdatabase");
		configuration = new Configuration();
		configuration.addResource(new Path("/usr/local/Cellar/hadoop/2.7.1/libexec/etc/hadoop/core-site.xml"));
		fs = FileSystem.get(configuration);
		dynamoDB = new DynamoDBWrapper();
	}

	/**
	 * read files from hdfs
	 * 
	 * @param path
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	@SuppressWarnings("unchecked")
	public void readFromHDFS(String path) throws IOException, URISyntaxException {
		FileStatus[] status = fs.listStatus(new Path(path));
//		File file = new File("SingleWordContent.json");
//		file.createNewFile();
//		FileWriter fileWriter = new FileWriter(file);
//		fileWriter.write("{\n");
//		fileWriter.write("\t\"SingleWordContent\": [\n");

		for (int i = 0; i < status.length; i++) {
			BufferedReader br = new BufferedReader(new InputStreamReader(fs.open(status[i].getPath())));
			String line;
			line = br.readLine();
			int j = 0;
			while (line != null) {
				j++;
				// System.out.println(line);
				String[] s = line.split("\t");
				if (s.length != 4) {

					System.out.println("bad line at i : " + i);
					break;
				}
//				fileWriter.write("{\n");
//				fileWriter.write("\t\t\"PutRequest\": {\n\t\t\t\"Item\": {\n");
//				fileWriter.write("\t\t\t\t\"word\":{\n\t\t\t\t\t\"S\": \"" + s[0] + "\"\n},\n");
//				fileWriter.write("\t\t\t\t\"url\":{\n\t\t\t\t\t\"S\": \"" + s[1] + "\"\n},\n");
//				fileWriter.write("\t\t\t\t\"idf\":{\n\t\t\t\t\t\"N\": \""
//						+ Double.parseDouble(String.format("%.3f", Double.parseDouble(s[2]))) + "\"\n},\n");
//				fileWriter.write("\t\t\t\t\"tf_idf\":{\n\t\t\t\t\t\"N\": \""
//						+ Double.parseDouble(String.format("%.3f", Double.parseDouble(s[3]))) + "\"\n}\n");
//				fileWriter.write("}\n}\n}");
//				dynamoDB.addSingleWordContent(s[0], s[1], Double.parseDouble(s[2]), Double.parseDouble(s[3]));
//				dynamoDB.addBiWordContent(s[0], s[1], Double.parseDouble(s[2]), Double.parseDouble(s[3]));
				System.out.println(j);

				line = br.readLine();

//				if (line != null) {
//					fileWriter.write(",");
//				}
//				fileWriter.write("\n");
//				fileWriter.flush();

			}

		}
		// fileWriter.write("]\n}");
		// fileWriter.close();
	}

	/**
	 * read files from bdb and write to hdfs
	 * 
	 * @param path
	 * @throws IOException
	 */
	public void writeToHDFS(String path) throws IOException {
		Path filenamePath = new Path(path);
		if (fs.exists(filenamePath)) {
			fs.delete(filenamePath, true);
		}
		FSDataOutputStream fin = fs.create(filenamePath);
		List<WebDocument> docs = db.getDocumentList();
		for (WebDocument doc : docs) {
			byte[] b = (doc.getURL() + "\t" + doc.getDocumentContent() + "\n").getBytes("utf-8");
			// byte[] b = (doc.getURL() + "\t" + doc.getDocumentTitle() +
			// "\n").getBytes("utf-8");
			fin.write(b);
		}
		fin.close();
	}

	public static void main(String[] args) throws Exception {
		HDFSWrapper h = new HDFSWrapper();
		// h.writeToHDFS("hdfs://localhost:9000/user/input/content.txt");
		h.readFromHDFS("hdfs://localhost:9000/user/output_bicontent_less/output_bicontent");

	}

}
