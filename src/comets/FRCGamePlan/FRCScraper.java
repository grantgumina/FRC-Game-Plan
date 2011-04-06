package comets.FRCGamePlan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

public class FRCScraper {

	private static int NUMBER_OF_EVENTS = 63;
	private int NUMBER_OF_FIELDS = 10;

	private String[] eventLines = new String[NUMBER_OF_EVENTS];
	private String[] eventFullNames = new String[NUMBER_OF_EVENTS];
	private String[] eventURLs = new String[NUMBER_OF_EVENTS];
	private String[][] splitEventLines = new String[NUMBER_OF_EVENTS][NUMBER_OF_EVENTS * 2];

	private String data = null;
	private String splitRegex = "2011(.*?)\">";
	private String eventRegex = "event/2011(.*?)\">(.*?)\\s<";
	private String urlRegex = "event/2011(.*?)\"";
	private String matchRegex = ">(.*?)<";

	public FRCScraper(String url) {
		// Attempt to open the webpage
		try {
			URL dataURL = new URL(url);

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					dataURL.openStream()));

			String line;
			String page = "";

			while ((line = reader.readLine()) != null) {
				page = page + line;
			}
			data = page;
			reader.close();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String[] getEventFullNames() {
		Pattern eventNamePattern = Pattern.compile(eventRegex);
		Matcher eventNameMatcher = eventNamePattern.matcher(data);

		// Add the event names and urls to arrays
		int i = 0;
		while (eventNameMatcher.find()) {
			eventLines[i] = eventNameMatcher.group();
			splitEventLines[i] = eventLines[i].split(splitRegex);
			;
			eventFullNames[i] = splitEventLines[i][1].replace(" <", "");

			i++;
		}

		// Put the event names in alphabetical order
		// TODO: need to have this sorted array corespond with the url array
		// Arrays.sort(eventFullNames);

		return eventFullNames;
	}

	public String[] getEventURLs() {
		Pattern eventURLPattern = Pattern.compile(urlRegex);
		Matcher eventURLMatcher = eventURLPattern.matcher(data);

		String[][] tempURLs = new String[NUMBER_OF_EVENTS][NUMBER_OF_EVENTS * 2];

		int i = 0;
		while (eventURLMatcher.find()) {
			splitEventLines[i][0] = eventURLMatcher.group();

			eventURLs[i] = splitEventLines[i][0];
			tempURLs[i] = eventURLs[i].split("11"); // split line after 2011
			eventURLs[i] = tempURLs[i][1];
			i++;
		}
		return eventURLs;
	}

	public void getMatches() {
		String[] rawMatchData = data.split("background-color:#FFFFFF");
		Pattern matchPattern = Pattern.compile(matchRegex);
		Matcher matchMatcher = matchPattern.matcher(data);

		int numberOfMatches = rawMatchData.length;
		String[][] matches = new String[numberOfMatches][];
		
		int i = 0;
		while (matchMatcher.find()) {			
			matches[i][0] = matchMatcher.group();
			Log.d("DEBUG", matches[i][0]);
			i++;
		}
		Log.d("DEBUG", "" + numberOfMatches);

		// return matches;
	}

	private int getNumberOfTeams() {
		int numberOfTeams = 0;

		return numberOfTeams;
	}
}
