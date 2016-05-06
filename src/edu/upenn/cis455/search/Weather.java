package edu.upenn.cis455.search;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Hashtable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * This class is to get weather info from http://openweathermap.org/
 * 
 * @author zhiyuanli
 *
 */
public class Weather {

	private Hashtable<String, String> weatherTable;

	/**
	 * get the weather for a city and country
	 * 
	 * @param city
	 * @param country
	 * @return weatherTable
	 */
	public Hashtable<String, String> getWeather(String city, String country) {

		try {
			weatherTable = new Hashtable<>();
			String sURL = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "," + country
					+ "&appid=caa435a9edda610de96a331590e23dcf";
			URL url = new URL(sURL);
			HttpURLConnection request = (HttpURLConnection) url.openConnection();
			request.connect();
			JsonParser jp = new JsonParser();
			JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
			JsonObject object = root.getAsJsonObject();
			JsonArray weather = object.getAsJsonArray("weather");
			String weatherDescription = weather.get(0).getAsJsonObject().get("description").getAsString().replace("\"",
					"");
			weatherTable.put("weather", weatherDescription);
			double temp = object.getAsJsonObject("main").get("temp").getAsDouble() * 1.8 - 459.67;
			weatherTable.put("temp_f", String.format("%.2f", temp));
//			int visibility = object.getAsJsonObject("visibility").getAsInt();
//			weatherTable.put("visibility", new String(visibility + ""));

		} catch (Exception e) {
			return null;
		}
		return weatherTable;
	}

	public static void main(String[] args) {
		Weather weather = new Weather();
		System.out.println(weather.getWeather("philadelphia", "us"));
	}
}