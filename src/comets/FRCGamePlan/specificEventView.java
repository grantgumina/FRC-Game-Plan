package comets.FRCGamePlan;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class specificEventView extends Activity {

	ListView matchesListView;
	String[] matchData;
	
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
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent matchIntent = new Intent(specificEventView.this, matchView.class);
				startActivity(matchIntent);
			}
		});
		
		processThread();
	}

	private void processThread() {
        progressDialog = ProgressDialog.show(specificEventView.this, "", "Doing...");
        new Thread() {
            public void run() {
            	String eventShortName = "";
        		Bundle extras = getIntent().getExtras();
        		if (extras != null) {
        			eventShortName = extras.getString("eventShortName");

        			FRCScraper matchListScraper = new FRCScraper(
        					"http://www2.usfirst.org/2011comp/events/" + eventShortName
        							+ "/matchresults.html");
            		matchData = matchListScraper.getMatches();
        			Log.d("DEBUG", "Done downloading");
        			updateListView.sendEmptyMessage(0);
        		}
        		

                handler.sendEmptyMessage(0);
            }
        }.start();
    }
	
	private Handler updateListView = new Handler() {
		@Override
		public void handleMessage(Message msg) {
	        matchesListView.setAdapter(new ArrayAdapter<String>(specificEventView.this, 
	        		android.R.layout.simple_list_item_1, matchData));
		}
	};

	
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}
}
