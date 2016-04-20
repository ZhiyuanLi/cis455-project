package edu.upenn.cis455.storage;

import java.io.*;
import java.util.*;
import java.net.*;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;

import org.apache.hadoop.util.*;

public class HDFSWrapper {

	private Configuration configuration;
	private FileSystem fs;
	private DatabaseWrapper db;

	public HDFSWrapper() throws IOException {
		// TODO Auto-generated constructor stub
		db = new DatabaseWrapper("indexdatabase");
		configuration = new Configuration();
		configuration.addResource(new Path("/usr/local/Cellar/hadoop/2.7.1/libexec/etc/hadoop/core-site.xml"));
		fs = FileSystem.get(configuration);
	}

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

	public void writeToHDFS(String path) throws IOException {
		Path filenamePath = new Path(path);
		if (fs.exists(filenamePath)) {
			fs.delete(filenamePath, true);
		}
		FSDataOutputStream fin = fs.create(filenamePath);
		List<WebDocument> docs = db.getDocumentList();
		for (WebDocument doc : docs) {
			fin.writeBytes(doc.getURL() + "\t" + doc.getDocumentContent());
		}
		fin.close();
	}

	public static void main(String[] args) throws IOException {
		HDFSWrapper h = new HDFSWrapper();
		h.writeToHDFS("hdfs://localhost:9000/user/input/input.txt");

	}

}
