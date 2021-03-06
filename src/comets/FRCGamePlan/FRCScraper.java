package comets.FRCGamePlan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

public class FRCScraper {

	private static int NUMBER_OF_EVENTS = 63;
	private static int NUMBER_OF_FIELDS = 8;

	private String[] eventLines = new String[NUMBER_OF_EVENTS];
	private String[] eventFullNames = new String[NUMBER_OF_EVENTS];
	private String[] eventURLs = new String[NUMBER_OF_EVENTS];
	private String[][] splitEventLines = new String[NUMBER_OF_EVENTS][NUMBER_OF_EVENTS * 2];

	private String data = null;
	private String splitRegex = "2011(.*?)\">";
	private String eventRegex = "event/2011(.*?)\">(.*?)\\s<";
	private String urlRegex = "event/2011(.*?)\"";
	// private String matchRegex = ">([^<>]*?)<";
	private String matchRegex = ">(.*?)<";

	public FRCScraper(String url) {
		// Attempt to open the webpage

		try {
			URL dataURL = new URL(url);

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					dataURL.openStream()));

			String line = "";
			StringBuilder page = new StringBuilder();

			while ((line = reader.readLine()) != null) {
				page.append(line);
			}
			data = page.toString();
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
			eventFullNames[i] = splitEventLines[i][1].replace(" <", "");

			i++;
		}

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

	public String[][] getMatches() {
		String[] rawMatchData = data.split("</table>");
		String[] matches = rawMatchData[2].split("#FFFFFF;");

		ArrayList<String> matchList = new ArrayList<String>(
				Arrays.asList(matches));
		matchList.remove(0);
		matches = matchList.toArray(new String[matchList.size()]);
		String[][] matchDetails = new String[matches.length][];

		for (int i = 0; i < matches.length; i++) {
			matchDetails[i] = findData(matches[i]);
		}

		return matchDetails;
	}

	public String[][] getTeamStats() {
		String[] rawRankingData = data.split("<TR style=\"background-color:#FFFFFF;\" >");
		ArrayList<String> rawRankingList = new ArrayList<String>(Arrays.asList(rawRankingData));
		rawRankingList.remove(0);
		rawRankingData = rawRankingList.toArray(new String[rawRankingList.size()]);
		
		String[][] teamRankingDetails = new String[rawRankingData.length][];

		for (int i = 0; i < rawRankingData.length; i++) {
			teamRankingDetails[i] = findData(rawRankingData[i]);
		}
		
		return teamRankingDetails;
	}

	private String[] findData(String parsedMatchData) {
		Pattern matchPattern = Pattern.compile(matchRegex);
		Matcher matchMatcher = matchPattern.matcher(parsedMatchData);

		// the html is wonky so I need extra space for the stuff that the parser
		// misses
		// HACK ALERT!!!
		String[] tableData = new String[200];

		int i = 0;
		while (matchMatcher.find()) {
			tableData[i] = matchMatcher.group();
			tableData[i] = parseMatchCells(tableData[i]);
			i++;
		}
		return tableData;
	}

	private String parseMatchCells(String retrievedCell) {
		String tempString = retrievedCell.replace("<", "");
		tempString = tempString.replace(">", "");
		return tempString;
	}

}
