package comets.FRCGamePlan;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.entity.InputStreamEntity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class FRCGamePlan extends Activity {
    
	TextView tv;
	ListView eventNamesListView;
	URL url;
	InputStream in = null;
	DataInputStream din;
	String line = "foo";

	String eventRegex = "event/2011(.*?)\">(.*?)\\s<";
	String urlRegex = "event/(.*?)\"";
	
	String[] eventNames = new String[63];
	String[] eventFullNames = new String[63];
	String[] eventURLs = new String[63];
	
	// A temporary array of arrays used to store the split values. 
	// Needed b/c ArrayAdapter only accepts a one dimensional array
	String[][] splitEventNames = new String[63][126]; 
	
	Pattern eventPattern = Pattern.compile(eventRegex);
	
    @SuppressWarnings("null")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    	
        // Configure UI
        tv = (TextView) findViewById(R.id.fooText);
        eventNamesListView = (ListView) findViewById(R.id.eventNamesListView);
        
        // Attempt to open the webpage
        try {
            URL url = new URL("http://thebluealliance.com/events");

            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

            String line;
            String page = "";
            
            while ((line = reader.readLine()) != null) {
            	page = page + line;
            }
            
            reader.close();
            
            Matcher eventMatcher = eventPattern.matcher(page);
            
            // Add the event names and urls to arrays
            int i = 0;
            while (eventMatcher.find()) {
            	eventNames[i] = eventMatcher.group();
            	splitEventNames[i] = eventNames[i].split("2011(.*?)\">");
            	splitEventNames[i][1] = splitEventNames[i][1].replace(" <", "");
            	eventFullNames[i] = splitEventNames[i][1];
            	Log.d("DEBUG", splitEventNames[i][1]);
            	eventURLs[i] = buildEventURL(eventMatcher.group(), i);
            	
            	i++;
            }
            // Put the event names in alphabetical order
            Arrays.sort(eventFullNames);
            
            eventNamesListView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					// Navigate to new view and download new data based on event shortname
					String selectedItemText = eventNamesListView.getItemAtPosition(arg2).toString();
					Log.d("DEBUG", "User chose: " + selectedItemText);
					
					for (int i = 0; i <= splitEventNames.length; i++) {
						if (splitEventNames[i][1] == selectedItemText) {
							Log.d("DEBUG", eventURLs[i]);
							Intent specificEventIntent = new Intent(FRCGamePlan.this, specificEventView.class);
							startActivityForResult(specificEventIntent, 0);
							break;
						}
					}
				}
            	
            });
            
            eventNamesListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, eventFullNames));
            
        } catch (MalformedURLException e) {
            e.printStackTrace();
            tv.setText("Error. Please restart application.");
        }  catch (IOException e) {
            e.printStackTrace();
            tv.setText("Error. Please restart application.");
        }
    }
    
    public String buildEventURL(String raw, int i) {
    	Pattern eventURL = Pattern.compile(urlRegex);
    	Matcher urlMatcher = eventURL.matcher(raw);
    	if (urlMatcher.find()) {
    		eventURLs[i] = urlMatcher.group();
    	}
    	return eventURLs[i];
    }
}