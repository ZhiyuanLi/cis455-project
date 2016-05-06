package edu.upenn.cis455.pagerank;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Read {
	public static void main(String[] args) throws IOException {
		File file = new File("cityNew.csv");
		File input = new File("city.csv");
		file.createNewFile();
		FileWriter fileWriter = new FileWriter(file);
		BufferedReader reader = new BufferedReader(new FileReader(input));
		String line = null;
		while ((line = reader.readLine()) != null) {
//			System.out.println(line);
			String[] s = line.split(",");
//			System.out.println(s.length);
//			System.out.println(s[0].replace("\"", ""));
			String[] numbers = s[0].replace("\"", "").trim().split("\\.");
//			System.out.println(numbers.length);
			double bigNumber = Integer.parseInt(numbers[0]) * Math.pow(10, 9)
					+ Integer.parseInt(numbers[1]) * Math.pow(10, 6) + Integer.parseInt(numbers[2]) * Math.pow(10, 3)
					+ Integer.parseInt(numbers[3]);
//			System.out.println(bigNumber + "\t" + s[3].replace("\"", "") + "\t" + s[4].replace("\"", ""));
			fileWriter.write((long)bigNumber + "," + s[3].replace("\"", "") + "," + s[4].replace("\"", "")+"\n");
		}
		fileWriter.close();
		reader.close();
	}
	
	
}
