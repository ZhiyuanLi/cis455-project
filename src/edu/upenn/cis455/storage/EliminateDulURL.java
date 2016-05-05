package edu.upenn.cis455.storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

import com.planetj.math.rabinhash.RabinHashFunction32;

public class EliminateDulURL {

	public static void main(String[] args) throws IOException {

		RabinHashFunction32 hash = RabinHashFunction32.DEFAULT_HASH_FUNCTION;
		HashSet<Integer> URLhashs = new HashSet<Integer>();

		File output = new File("/Users/woody/Downloads/455ProjectData/IndexerInput/link/link_url.txt");
		File inputDir = new File("/Users/woody/Downloads/455ProjectData/CrawlerOutput/link");
		output.createNewFile();
		FileWriter fileWriter = new FileWriter(output);
		BufferedReader reader = null;
		String line = null;
		String key = null;
		int checksum = 0;
		for (File f : inputDir.listFiles()) {
			System.out.println(f.getName());
			reader = new BufferedReader(new FileReader(f));
			while ((line = reader.readLine()) != null) {
				if (!line.equals("")) {
					String[] s = line.split("\t", 2);
					if (s.length == 2) {
						key = s[0].trim();
						checksum = hash.hash(key);
						if (!URLhashs.contains(checksum)) {
							fileWriter.write(key + "\t" + s[1].trim() + "\n");
							URLhashs.add(checksum);
						}
					} else {
						System.out.println(f.getName() + ":" + line);
					}
				}
			}
		}
		fileWriter.close();
	}

}
