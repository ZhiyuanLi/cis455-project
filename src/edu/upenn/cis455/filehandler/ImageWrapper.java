package edu.upenn.cis455.filehandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ImageWrapper {

	public static void main(String[] args) throws IOException {
		File output = new File("/Users/woody/Downloads/455ProjectData/IndexerInput/image/image.txt");
		File inputDir = new File("/Users/woody/Downloads/455ProjectData/IndexerInput/image/image_url.txt");
		output.createNewFile();
		FileWriter fileWriter = new FileWriter(output);
		BufferedReader reader = null;
		String line = null;
		reader = new BufferedReader(new FileReader(inputDir));
		while ((line = reader.readLine()) != null) {
			if (!line.equals("")) {
				String[] s = line.split("\t", 2);
				if (s.length == 2) {
					String imageTitle = s[0].replace("http://", "");
					imageTitle = imageTitle.replace("https://", "");
					if (imageTitle.contains("/")) {
						imageTitle = imageTitle.substring(imageTitle.indexOf('/'));
					}
					String[] urls = s[1].trim().split(" ");
					String imageContent;
					for (String url : urls) {
						imageContent = url.replace("http://", "");
						imageContent = imageContent.replace("https://", "");
						imageContent = imageContent.substring(imageContent.indexOf('/'));
						fileWriter.write(url + "\t" + imageTitle + imageContent + "\n");
					}
				}
			}

		}
		fileWriter.close();
	}
}
