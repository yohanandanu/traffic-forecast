package vn.edu.hcmut.cse.trafficdirection.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class TouchPoint extends MapActivity {
	private MapView mapview;
	private Button tap_Button;
	private int x;
	private int y;
	private String Point1 = null;
	private float scale;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_touch_point);

		mapview = (MapView) findViewById(R.id.mapViewTouch);
		mapview.setBuiltInZoomControls(true);
		MapOverlay mapOverlay = new MapOverlay();
		List<Overlay> listOfOverlays = mapview.getOverlays();
		listOfOverlays.clear();
		listOfOverlays.add(mapOverlay);
		this.scale =  getResources().getDisplayMetrics().density;
		tap_Button = (Button) findViewById(R.id.imageButtonSelector);
		tap_Button.setText(R.string.tap_to_choose);
		tap_Button.setVisibility(View.GONE);
		tap_Button.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				GeoPoint p = mapview.getProjection().fromPixels(x, y);

				Intent it = new Intent();

				try {
					FileWriter fstream = new FileWriter(Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ MainActivity.PATH_TO_TMP_FILE, true);
					BufferedWriter out = new BufferedWriter(fstream);

					out.write("2");
					out.newLine();
					out.write(p.toString());
					out.newLine();
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				setResult(RESULT_OK, it);
				finish();

			}
		});

		String mode = null;
		try {
			BufferedReader in = new BufferedReader(new FileReader(Environment
					.getExternalStorageDirectory().getAbsolutePath()
					+ MainActivity.PATH_TO_TMP_FILE));

			mode = in.readLine();
			if (mode != null) {
				if (mode.equals("1")) {
				} else if (mode.equals("2")) {
					Point1 = in.readLine();
				}
			}

			in.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (Point1 != null) {
		}
		mapview.invalidate();

	}

	class MapOverlay extends Overlay {

		@Override
		public boolean onTouchEvent(MotionEvent event, MapView mapView) {
			// ---when user lifts his finger---
			if (event.getAction() == 1) {
				x = (int) event.getX();
				y = (int) event.getY();

				// tap_Button.setPadding(x, y, tap_Button.getPaddingRight(),
				// tap_Button.getPaddingBottom());
				// tap_Button.setM;
				tap_Button.setVisibility(View.VISIBLE);
				RelativeLayout.LayoutParams rel_btn = new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

				rel_btn.leftMargin = x - dip(50);
				rel_btn.topMargin = y - dip(50);
				rel_btn.width = dip(100);// tap_Button.getWidth();
				rel_btn.height = dip(50);// tap_Button.getHeight();

				tap_Button.setLayoutParams(rel_btn);
			}
			return false;
		}

		public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
				long when) {
			if (Point1 == null)
				return super.draw(canvas, mapView, shadow, when);

			final float scale = getResources().getDisplayMetrics().density;
			// Log.v("dessssssssssssssssssssss", Float.toString(scale));
			int dip = (int) (8 * scale);

			Bitmap bmp1 = BitmapFactory.decodeResource(getResources(),
					R.drawable.startpoint);

			Point screenPts1 = new Point();
			GeoPoint p1 = new GeoPoint(Integer.parseInt(Point1.split(",")[0]),
					Integer.parseInt(Point1.split(",")[1]));
			mapView.getProjection().toPixels(p1, screenPts1);
			canvas.drawBitmap(bmp1, screenPts1.x - dip, screenPts1.y - dip,
					null);

			return super.draw(canvas, mapView, shadow, when);
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	public int dip(int px){
		int dip = (int) (px * scale);
		return dip;
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		MapController mc = mapview.getController();
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