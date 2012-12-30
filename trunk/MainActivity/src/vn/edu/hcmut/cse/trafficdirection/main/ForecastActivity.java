package vn.edu.hcmut.cse.trafficdirection.main;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class ForecastActivity extends MapActivity {
	private MapView m_MapView = null;
	private ArrayList<GeoPoint> traffic_jam = new ArrayList<GeoPoint>();

	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_gpx);
		
		traffic_jam.clear();
		traffic_jam.add(new GeoPoint(10770450, 106658156));
		traffic_jam.add(new GeoPoint(10777786, 106656140));

		m_MapView = (MapView) findViewById(R.id.mapViewShowGPX);

		LinearLayout zoomLayout = (LinearLayout) findViewById(R.id.layout_MainMenuShowGPX);
		zoomLayout.addView(m_MapView.getZoomControls(),
				new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT));

		showForecase();
	}

	private void showForecase() {
		// TODO Auto-generated method stub
		ForecastOverlay mapOverlay = new ForecastOverlay();
		List<Overlay> listOfOverlay = m_MapView.getOverlays();
		listOfOverlay.clear();
		listOfOverlay.add(mapOverlay);

		m_MapView.invalidate();
	}

	class ForecastOverlay extends Overlay {
		private int play_frame;
		private final int n_frame = 20;
		private final long DELAY = 80;//24 frame/s
		private final float MAX_RADIUS = (float) 30.0;
		private long last_time;
		private Projection project;
		
		public ForecastOverlay()
		{
			play_frame = 0;
			last_time = 0;
			project = m_MapView.getProjection();
		}
		public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
				long when) {
			for(int i = 0; i < traffic_jam.size(); i++)
			{
				GeoPoint cur = traffic_jam.get(i);
				
				Paint mPaint = new Paint();

				mPaint.setAntiAlias(true);
				mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
				mPaint.setStrokeJoin(Paint.Join.ROUND);
				mPaint.setStrokeCap(Paint.Cap.ROUND);
				mPaint.setStrokeWidth(6);
				
				
				
				if(play_frame >= n_frame)
				{
					play_frame = 0;
				}
					
				Point p = new Point();
				project.toPixels(cur, p);
				
				long time = (System.currentTimeMillis() - last_time);
				mPaint.setColor(Color.argb((int)(255 * (1.0 - play_frame / MAX_RADIUS)), 255, 0, 0));
				if(time >= DELAY)
				{
					last_time = System.currentTimeMillis();
					play_frame++;
					canvas.drawCircle(p.x, p.y, play_frame * (play_frame / MAX_RADIUS), mPaint);
					m_MapView.postInvalidate();
				}
				else
				{
					canvas.drawCircle(p.x, p.y, play_frame * (play_frame / MAX_RADIUS), mPaint);
					m_MapView.postInvalidate();
				}

					
				
			}
			return super.draw(canvas, mapView, shadow, when);
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		MapController mc = m_MapView.getController();
		switch (keyCode) {
		case KeyEvent.KEYCODE_I:
			mc.zoomIn();
			break;
		case KeyEvent.KEYCODE_O:
			mc.zoomOut();
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			mc.scrollBy(-50, 0);
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			mc.scrollBy(50, 0);
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			mc.scrollBy(0, 50);
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			mc.scrollBy(0, -50);
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

}
