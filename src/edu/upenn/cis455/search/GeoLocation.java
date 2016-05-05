package edu.upenn.cis455.search;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class GeoLocation {
	public static List<String> cityLines = new ArrayList<String>();
	public static String state = null;
	public static String city = null;

	// in download and store
	public static String calculateLocation(String ip) {
		long target = convertIp(ip.trim());
		// System.out.println("ip number is " + target);
		String stateAndCity = search(target);
		String[] stateCityPair = stateAndCity.split(",");
		state = stateCityPair[1];
		city = stateCityPair[2];
		return state;
	}

	private static long convertIp(String ip) {
		String[] ipArr = ip.split("\\.");
		double ipLong = Integer.parseInt(ipArr[0]) * Math.pow(10, 9) + Integer.parseInt(ipArr[1]) * Math.pow(10, 6)
				+ Integer.parseInt(ipArr[2]) * Math.pow(10, 3) + Integer.parseInt(ipArr[3]);
		return (long) ipLong;
	}

	public static void readCityDB(String path) {
		File file = new File(path);
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line;
			line = reader.readLine();
			cityLines.add(line);
			while (line != null && !line.equals("")) {
				line = reader.readLine();
				cityLines.add(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String search(long target) {
		int totalLineNum = cityLines.size();
		int left = 0;
		int right = totalLineNum - 1;
		while (left < right - 1) {
			// System.out.println(left + "->" + right);
			int mid = left + (right - left) / 2;
			String midStr = cityLines.get(mid);
			long midValue = Long.parseLong(midStr.split(",")[0]);
			// System.out.println("middle value = " + midValue);
			// System.out.println("target = " + target);
			if (target < midValue) {
				right = mid - 1;
			} else {
				left = mid;
			}
		}
		// System.out.println("left is "+left+ " right is "+right);
		// String result = cityLines.get(left);
		// System.out.println("result is "+result);
		return cityLines.get(left);
	}

	private static void readAndWrite() throws IOException {
		File inputFolder = new File("pageRankRaw");
		File[] inputFileList = inputFolder.listFiles();
		for (File inputFile : inputFileList) {
			if (inputFile.getName().contains(".DS_Store")) {
				continue;
			}
			File outputFile = new File("stateCityOutput/" + inputFile.getName());
			outputFile.createNewFile();
			System.out.println("file " + inputFile.getName() + " created");
			FileWriter fileWriter = new FileWriter(outputFile);
			BufferedReader reader = new BufferedReader(new FileReader(inputFile));
			String line = null;
			while ((line = reader.readLine()) != null) {
				// System.out.println("each line is "+line);
				String[] s = line.split("\t");
				if (s.length == 2) {
					String url = s[0].trim();
					String host;
					try {
						host = new URL(url).getHost();
					} catch (MalformedURLException e1) {
						continue;
					}
					// String host = getDomainName(url);
					// System.out.println("host is " + host);
					InetAddress address;
					try {
						address = InetAddress.getByName(host);
						calculateLocation(address.toString().split("/")[1].trim());
						// System.out.println("ip address is " +
						// address.toString().split("/")[1].trim());

						// write to output file

						// System.out.println("@ "+s[0].trim() + "\t" +
						// s[1].trim()
						// + "\t" + state + "\t" + city);
						fileWriter.write(s[0].trim() + "\t" + s[1].trim() + "\t" + state + "\t" + city + "\n");
					} catch (UnknownHostException e) {
						System.out.println("Error url: " + url);
						System.out.println("Error host: " + host);
						fileWriter.write(s[0].trim() + "\t" + s[1].trim() + "\t" + "N/A" + "\t" + "N/A" + "\n");
						continue;
						// e.printStackTrace();
					}

				} else {
					System.out.println("error!\t" + line);
				}
			}
			fileWriter.close();
			System.out.println("file " + inputFile.getName() + " finished");
			reader.close();
		}

	}

	public static String getDomainName(String url) throws MalformedURLException {
		if (!url.startsWith("http") && !url.startsWith("https")) {
			url = "http://" + url;
		}
		URL netUrl = new URL(url);
		String host = netUrl.getHost();
		if (host.startsWith("www")) {
			host = host.substring("www".length() + 1);
		}
		return host;
	}

	public static void main(String[] args) throws IOException {
		long time1 = System.currentTimeMillis();
		String cityDBPath = "cityNew.csv";
		long time2 = System.currentTimeMillis();
		System.out.println("reading time1 is " + (time2 - time1));
		readCityDB(cityDBPath);
		long time3 = System.currentTimeMillis();
		System.out.println("reading time2 is " + (time3 - time2));
		readAndWrite();
	}
}
