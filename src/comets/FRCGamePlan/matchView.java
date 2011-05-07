package comets.FRCGamePlan;

import java.util.ArrayList;

import comets.FRCGamePlan.R.layout;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

public class matchView extends Activity {
	private ArrayList<Path> _graphics = new ArrayList<Path>();
	private Paint mPaint;

	private float lastTouchX;
	private float lastTouchY;

	private float lastPosX;
	private float lastPosY;

	Button r1;
	Button r2;
	Button r3;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.match_view);

		LinearLayout drawingLayout = (LinearLayout) findViewById(R.id.drawingLinearLayout);
		drawingLayout.addView(new DrawingPanel(this));

		r1 = (Button) findViewById(R.id.robot1);
		r2 = (Button) findViewById(R.id.robot2);
		r3 = (Button) findViewById(R.id.robot3);

		mPaint = new Paint();
		mPaint.setDither(true);
		mPaint.setColor(0xFFFFFF00);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(3);

//		r1.setOnTouchListener(new View.OnTouchListener() {
//
//			@Override
//			public boolean onTouch(View v, MotionEvent me) {
//				switch (me.getAction()) {
//				case MotionEvent.ACTION_DOWN: {
//					lastTouchX = me.getX();
//					lastTouchY = me.getY();
//					break;
//				}
//				case MotionEvent.ACTION_MOVE: {
//					final float currentX = me.getX();
//					final float currentY = me.getY();
//					
//					final float dx = currentX - lastTouchX;
//					final float dy = currentY - lastTouchY;
//					
//					Log.d("", "X: " + me.getX() + "Y: " + me.getY());
//					Log.d("", "Dist X: " + dx + "Dist Y: " + dy);
//					v.setPadding((int)dx, 0, 0, 0);
//					v.invalidate();
//					break;
//				}
//				}
//				return false;
//			}
//
//		});

	}

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
			// TODO Auto-generated method stub
			_thread.setRunning(true);
			_thread.start();
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			boolean retry = true;
			_thread.setRunning(false);
			while (retry) {
				try {
					_thread.join();
					retry = false;
				} catch (InterruptedException e) {
					// we will try it again and again...
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
