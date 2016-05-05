package edu.upenn.cis455.filehandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

import com.planetj.math.rabinhash.RabinHashFunction32;

public class PageRankContentSeenWrapper {

	public static void main(String[] args) throws IOException {
		RabinHashFunction32 hash = RabinHashFunction32.DEFAULT_HASH_FUNCTION;
		HashSet<Integer> URLhashs = new HashSet<>();

		// File output = new File("images_unique.txt");
		// File input = new File("images_contentseeninput.txt");

		File output = new File("/Users/woody/Downloads/455ProjectData/IndexerInput/link/link.txt");

		File input = new File("/Users/woody/Downloads/455ProjectData/IndexerInput/link/link_url.txt");
		output.createNewFile();
		FileWriter fileWriter = new FileWriter(output);

		BufferedReader reader;
		String line, key;

		reader = new BufferedReader(new FileReader(input));

		int checksum = 0;
		int count = 0;
		while ((line = reader.readLine()) != null) {
			if (!line.equals("")) {
				String[] s = line.split("\t", 2);
				if (s.length == 2) {
					key = s[1].trim();
					checksum = hash.hash(key);
					if (!URLhashs.contains(checksum)) {
						fileWriter.write(s[0] + "\t" + key + "\n");
						URLhashs.add(checksum);
					} else {
						count++;
					}
				}
			}
		}
		System.out.println(count);
		fileWriter.close();
	}
}
