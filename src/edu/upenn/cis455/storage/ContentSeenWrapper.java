package edu.upenn.cis455.storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

import com.planetj.math.rabinhash.RabinHashFunction32;

public class ContentSeenWrapper {

	public static void main(String[] args) throws IOException {

		RabinHashFunction32 hash = RabinHashFunction32.DEFAULT_HASH_FUNCTION;
		HashSet<Integer> URLhashs = new HashSet<>();

		File output = new File("images_contentseen.txt");
		File input = new File("imagesInput.txt");
		output.createNewFile();
		FileWriter fileWriter = new FileWriter(output);
		BufferedReader reader = new BufferedReader(new FileReader(input));
		String line = null;
		String key = null;
		int checksum = 0;
		while ((line = reader.readLine()) != null) {
			if (!line.equals("")) {
				String[] s = line.split("\t", 2);
				key = s[1];
				checksum = hash.hash(key);
				if (!URLhashs.contains(checksum)) {
					fileWriter.write(s[0] + "\t" + key.trim() + "\n");
					URLhashs.add(checksum);
				}
			}
		}
		fileWriter.close();
		reader.close();
	}
}