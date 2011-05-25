package comets.FRCGamePlan;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import comets.FRCGamePlan.R.layout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class matchView extends Activity {
	private ArrayList<Path> _graphics = new ArrayList<Path>();
	private Paint mPaint;
	String[] matchDetails;
	String[] teamNumbers;
	String[][] teamStats;

	RobotButton r1;
	RobotButton r2;
	RobotButton r3;
	RobotButton r4;
	RobotButton r5;
	RobotButton r6;
	RobotButton[] rba;

	AlertDialog.Builder builder;
	AlertDialog.Builder teamStatsBuilder;

	private ProgressDialog progressDialog;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			progressDialog.dismiss();

			// Buttons
			r1 = (RobotButton) findViewById(R.id.robot1);
			r2 = (RobotButton) findViewById(R.id.robot2);
			r3 = (RobotButton) findViewById(R.id.robot3);
			r4 = (RobotButton) findViewById(R.id.robot4);
			r5 = (RobotButton) findViewById(R.id.robot5);
			r6 = (RobotButton) findViewById(R.id.robot6);
			rba = new RobotButton[] { r1, r2, r3, r4, r5, r6 };

			// Robot team numbers
			Bundle extras = getIntent().getExtras();

			matchDetails = extras.getStringArray("md");
			;
			teamNumbers = new String[] { matchDetails[5], matchDetails[7],
					matchDetails[9], matchDetails[11], matchDetails[13],
					matchDetails[15] };

			// Set Long Click and Click events
			for (int i = 0; i < rba.length; i++) {
				rba[i].setTeamNumber(teamNumbers[i]);
				rba[i].setText(teamNumbers[i]);
				rba[i].setOnLongClickListener(showTeamNumbers);
				rba[i].setOnClickListener(showRobotStats);
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.match_view);

		LinearLayout drawingLayout = (LinearLayout) findViewById(R.id.drawingLinearLayout);
		drawingLayout.addView(new DrawingPanel(this));

		mPaint = new Paint();
		mPaint.setDither(true);
		mPaint.setColor(0xFFFFFF00);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(3);

		builder = new AlertDialog.Builder(this);
		teamStatsBuilder = new AlertDialog.Builder(this);

		processThread();
	}

	private void processThread() {
		progressDialog = ProgressDialog.show(matchView.this, "",
				"Downloading Data. Please wait...");

		new Thread() {
			public void run() {
				Bundle extras = getIntent().getExtras();
				// Grab team rankings
				String eventShortName = extras.getString("eventShortName");
				FRCScraper teamScraper = new FRCScraper(
						"http://www2.usfirst.org/2011comp/events/"
								+ eventShortName + "/rankings.html");
				teamStats = teamScraper.getTeamStats(); // this stores all the
														// data for each team
				handler.sendEmptyMessage(0);
			}
		}.start();
	}

	private OnLongClickListener showTeamNumbers = new OnLongClickListener() {
		@Override
		public boolean onLongClick(final View v) {
			builder.setTitle("Choose a team");
			builder.setItems(teamNumbers,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int item) {
							for (int i = 0; i < rba.length; i++) {
								if (rba[i].getId() == v.getId()) {
									rba[i].setText(teamNumbers[item]);
									rba[i].setTeamNumber(teamNumbers[item]);
								}
							}
						}
					});
			AlertDialog ad = builder.create();
			ad.show();
			return true;
		}
	};

	private OnClickListener showRobotStats = new OnClickListener() {
		@Override
		public void onClick(View v) {
			for (int x = 0; x < rba.length; x++) {
				Log.d("", "This Button ID: " + v.getId() + "Button IDs: " + rba[x].getId());
				if (v.getId() == rba[x].getId()) {
					for (int i = 0; i < teamStats.length; i++) {
						if (teamStats[i][2].equals(rba[x].getTeamNumber())) {
							teamStatsBuilder.setTitle(teamStats[i][2]
									+ " Stats");
							teamStatsBuilder.setMessage("Rank:"
									+ teamStats[i][0] + "\nW-L-T: "
									+ teamStats[i][4] + "-" + teamStats[i][6]
									+ "-" + teamStats[i][8]
									+ "\nMatches Played: " + teamStats[i][10]
									+ "\nQS: " + teamStats[i][12] + "\nRS: "
									+ teamStats[i][14]);
							AlertDialog ad = teamStatsBuilder.create();
							ad.show();
							break;
						}
					}
				}
			}

		}
	};
	
	class DrawingPanel extends SurfaceView implements SurfaceHolder.Callback {
		private DrawingThread _thread;
		private Path path;

		public DrawingPanel(Context context) {
			super(context);
			getHolder().addCallback(this);
			_thread = new DrawingThread(getHolder(), this);
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			synchronized (_thread.getSurfaceHolder()) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					path = new Path();
					path.moveTo(event.getX(), event.getY());
				} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
					path.lineTo(event.getX(), event.getY());
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					path.lineTo(event.getX(), event.getY());
					_graphics.add(path);
				}
				return true;
			}
		}

		@Override
		public void onDraw(Canvas canvas) {
			for (Path path : _graphics) {
				// canvas.drawPoint(graphic.x, graphic.y, mPaint);
				canvas.drawPath(path, mPaint);
			}
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {

		}

		public void surfaceCreated(SurfaceHolder holder) {
			_thread.setRunning(true);
			_thread.start();
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			boolean retry = true;
			_thread.setRunning(false);
			while (retry) {
				try {
					_thread.join();
					retry = false;
				} catch (InterruptedException e) {
				}
			}
		}
	}

	class DrawingThread extends Thread {
		private SurfaceHolder _surfaceHolder;
		private DrawingPanel _panel;
		private boolean _run = false;

		public DrawingThread(SurfaceHolder surfaceHolder, DrawingPanel panel) {
			_surfaceHolder = surfaceHolder;
			_panel = panel;
		}

		public void setRunning(boolean run) {
			_run = run;
		}

		public SurfaceHolder getSurfaceHolder() {
			return _surfaceHolder;
		}

		@Override
		public void run() {
			Canvas c;
			while (_run) {
				c = null;
				try {
					c = _surfaceHolder.lockCanvas(null);
					synchronized (_surfaceHolder) {
						_panel.onDraw(c);
					}
				} finally {
					if (c != null) {
						_surfaceHolder.unlockCanvasAndPost(c);
					}
				}
			}
		}
	}
}
