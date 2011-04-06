package comets.FRCGamePlan;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ListView;

public class specificEventView extends Activity {

	ListView matchesListView;
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
        			Log.d("DEBUG", "Done downloading");
        		}
        		// matchListScraper.getMatches();
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

	
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}
}
