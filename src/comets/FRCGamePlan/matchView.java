package comets.FRCGamePlan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import comets.FRCGamePlan.R.layout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Display;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class matchView extends Activity {
	private ArrayList<Path> _graphics = new ArrayList<Path>();
	private Paint mPaint;
	String[] matchDetails;

	RobotButton r1;
	RobotButton r2;
	RobotButton r3;
	RobotButton r4;
	RobotButton r5;
	RobotButton r6;
	AlertDialog.Builder builder;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d("",
				"\n\n----------------------------- matchView --------------------------------------");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.match_view);

		LinearLayout drawingLayout = (LinearLayout) findViewById(R.id.drawingLinearLayout);
		drawingLayout.addView(new DrawingPanel(this));

		r1 = (RobotButton) findViewById(R.id.robot1);
		r2 = (RobotButton) findViewById(R.id.robot2);
		r3 = (RobotButton) findViewById(R.id.robot3);
		r4 = (RobotButton) findViewById(R.id.robot4);
		r5 = (RobotButton) findViewById(R.id.robot5);
		r6 = (RobotButton) findViewById(R.id.robot6);
		RobotButton[] rba = new RobotButton[] { r1, r2, r3, r4, r5, r6 };
		
		Bundle extras = getIntent().getExtras();
		matchDetails = extras.getStringArray("md");
		
		String[] teamNumbers = new String[] { matchDetails[5], matchDetails[7],
				matchDetails[9], matchDetails[11], matchDetails[13],
				matchDetails[15] };
		final String[] fTn = teamNumbers;

		builder = new AlertDialog.Builder(this);

		for (int i = 0; i < rba.length; i++) {
			rba[i].setTeamNumber(teamNumbers[i]);
			rba[i].setText(teamNumbers[i]);
			
			final int index = i;
			final RobotButton frba = rba[i];
			rba[i].setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					builder.setTitle("Choose a team");
					builder.setItems(fTn,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int item) {
									frba.setText(fTn[item]);
									frba.setTeamNumber(fTn[item]);
								}
							});
					AlertDialog al = builder.create();
					al.show();
					return true;
				}
			});
		}

		mPaint = new Paint();
		mPaint.setDither(true);
		mPaint.setColor(0xFFFFFF00);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(3);
	}
	
	// disclaimer - found this code online (don't worry it's open sourced - i
	// didn't steal it)
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
