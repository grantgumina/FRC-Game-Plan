package comets.FRCGamePlan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class specificEventView extends Activity {

	ListView matchesListView;
	String[][] listData;
	String[] listViewItems;

	Intent matchIntent;

	private ProgressDialog progressDialog;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			progressDialog.dismiss();
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.specific_event_view);
		matchesListView = (ListView) findViewById(R.id.matchesListView);

		matchesListView.setOnItemClickListener(new OnItemClickListener() {
			@SuppressWarnings("unused")
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int itemIndex,
					long l) {
				Bundle extras = getIntent().getExtras();
				matchIntent = new Intent(specificEventView.this,
						matchView.class);
				matchIntent.putExtra("md", listData[itemIndex]);
				Log.d("", "" + itemIndex);
				matchIntent.putExtra("eventShortName", extras.getString("eventShortName"));
				startActivity(matchIntent);

			}
		});

		processThread();
	}
	

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.selectEvent: {
			return true;
		}
		case R.id.selectMatch: {
			return true;
		}
		case R.id.selectTeam: {
			return true;
		}
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void processThread() {
		progressDialog = ProgressDialog.show(specificEventView.this, "",
				"Downloading Data. Please wait...");
		new Thread() {
			public void run() {
				String eventShortName = "";
				Bundle extras = getIntent().getExtras();
				if (extras != null) {
					eventShortName = extras.getString("eventShortName");

					FRCScraper matchListScraper = new FRCScraper(
							"http://www2.usfirst.org/2011comp/events/"
									+ eventShortName + "/matchresults.html");
					listData = matchListScraper.getMatches();
					listViewItems = buildListViewItems(listData);
					updateListView.sendEmptyMessage(0);
				}
				handler.sendEmptyMessage(0);
			}
		}.start();
	}

	private Handler updateListView = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			matchesListView.setAdapter(new ArrayAdapter<String>(
					specificEventView.this,
					android.R.layout.simple_list_item_1, listViewItems));
		}
	};

	private String[] buildListViewItems(String[][] matchData) {
		String[] temp = new String[matchData.length];
		for (int i = 0; i < matchData.length; i++) {
			temp[i] = "Match #: " + matchData[i][3] + "\n" + matchData[i][5]
					+ " " + matchData[i][7] + " " + matchData[i][9] + " vs. "
					+ matchData[i][11] + " " + matchData[i][13] + " "
					+ matchData[i][15];
		}

		return temp;
	}
}
