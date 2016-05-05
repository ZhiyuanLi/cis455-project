package edu.upenn.cis455.storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

import com.planetj.math.rabinhash.RabinHashFunction32;

public class PageRankWrapper {

	public static void main(String[] args) throws IOException {

		RabinHashFunction32 hash = RabinHashFunction32.DEFAULT_HASH_FUNCTION;
		HashSet<Integer> URLhashs = new HashSet<>();

		File output = new File("images_unique.txt");
		File input = new File("images_contentseeninput.txt");
		output.createNewFile();
		FileWriter fileWriter = new FileWriter(output);
		BufferedReader reader = new BufferedReader(new FileReader(input));
		String line = null;
		String key = null;
		int checksum = 0;
		while ((line = reader.readLine()) != null) {
			if (!line.equals("")) {
				String[] s = line.split("\t", 2);
				key = s[0];
				checksum = hash.hash(key);
				if (!URLhashs.contains(checksum)) {
					fileWriter.write(key + "\t" + s[1].trim() + "\n");
					URLhashs.add(checksum);
				}
			}
		}
		fileWriter.close();
		reader.close();
	}

}
