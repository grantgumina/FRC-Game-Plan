package comets.FRCGamePlan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class FRCGamePlan extends Activity {

	TextView tv;
	ListView eventNamesListView;
	String line = "foo";
	String[] eventNames = new String[63];
	String[] eventURLs = new String[63];

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		FRCScraper eventScraper = new FRCScraper(
				"http://www.thebluealliance.com/events");

		// Configure UI
		tv = (TextView) findViewById(R.id.instructionText);
		tv.setText("Choose an event below.");
		
		eventNamesListView = (ListView) findViewById(R.id.eventNamesListView);

		// Create objects to get data
		eventNames = eventScraper.getEventFullNames();
		eventURLs = eventScraper.getEventURLs();

		eventNamesListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// Navigate to new view and download new data based on event
				// shortname
				String selectedItemText = eventNamesListView.getItemAtPosition(
						arg2).toString();

				for (int i = 0; i < eventNames.length; i++) {
					if (eventNames[i] == selectedItemText) {
						Intent specificEventIntent = new Intent(FRCGamePlan.this, specificEventView.class);
						specificEventIntent.putExtra("eventShortName", eventURLs[i]);
						startActivityForResult(specificEventIntent, 0);
						break;
					}
				}
			}
		});

		eventNamesListView.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, eventNames));

	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    return true;
	}
}