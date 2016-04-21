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

	/**
	 * init
	 * 
	 * @throws IOException
	 */
	public HDFSWrapper() throws IOException {
		// TODO Auto-generated constructor stub
		db = new DatabaseWrapper("indexdatabase");
		configuration = new Configuration();
		configuration.addResource(new Path("/usr/local/Cellar/hadoop/2.7.1/libexec/etc/hadoop/core-site.xml"));
		fs = FileSystem.get(configuration);
	}

	/**
	 * read files from hdfs
	 * 
	 * @param path
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public void readFromHDFS(String path) throws IOException, URISyntaxException {
		FileStatus[] status = fs.listStatus(new Path(path));
		for (int i = 0; i < status.length; i++) {
			BufferedReader br = new BufferedReader(new InputStreamReader(fs.open(status[i].getPath())));
			String line;
			line = br.readLine();
			while (line != null) {
				System.out.println(line);
				line = br.readLine();
			}
		}
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
			fin.write(b);
		}
		fin.close();
	}

	public static void main(String[] args) throws IOException {
		HDFSWrapper h = new HDFSWrapper();
		h.writeToHDFS("hdfs://localhost:9000/user/input/input.txt");
	}

}
