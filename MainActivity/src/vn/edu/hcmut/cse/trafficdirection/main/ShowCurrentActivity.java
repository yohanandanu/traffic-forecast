package vn.edu.hcmut.cse.trafficdirection.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import vn.edu.hcmut.cse.trafficdirection.database.DatabaseHelper;
import vn.edu.hcmut.cse.trafficdirection.network.TCPClient;
import vn.edu.hcmut.cse.trafficdirection.node.DrawableStreet;
import vn.edu.hcmut.cse.trafficdirection.node.NodeDrawable;
import vn.edu.hcmut.cse.trafficdirection.overlay.ShowCurrentOverlay;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class ShowCurrentActivity extends MapActivity {
	public static final double[] height = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
			11, 12, 13, 0.169600, 0.084800, 0.042400, 0.021200, 0.010600,
			0.005300, 0.002650, 21 };
	public static final double[] width = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
			11, 12, 13, 0.12192, 0.06096, 0.03048, 0.015240, 0.007620, 0.00381,
			0.001905, 21 };
	public static final double[] step = { 0, 21, 20, 19, 18, 17, 16, 15, 14,
			13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1 };

	private MapView m_MapView = null;
	private int m_iOverlayType = -1;
	private Timer timer = null;
	public ArrayList<DrawableStreet> streetList = new ArrayList<DrawableStreet>();
	public ArrayList<DrawableStreet> streetList2 = new ArrayList<DrawableStreet>();
	public int count = 0;
	public GeoPoint m_CurrentLocation = null;

	private DatabaseHelper md;
	private TCPClient tcpClient = null;
	Display display = null;

	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_gpx);

		streetList.clear();
		streetList2.clear();

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			m_iOverlayType = Integer.parseInt(extras.getString("OVERLAY"));
		}

		m_MapView = (MapView) findViewById(R.id.mapViewShowGPX);

		LinearLayout zoomLayout = (LinearLayout) findViewById(R.id.layout_MainMenuShowGPX);
		zoomLayout.addView(m_MapView.getZoomControls(),
				new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT));
		m_CurrentLocation = m_MapView.getMapCenter();
		m_MapView.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent ev) {
				// TODO Auto-generated method stub
				m_CurrentLocation = m_MapView.getMapCenter();
				// if(isShowCurrent)
				// updateStreet();
				Log.d("CenterPoint", m_CurrentLocation.toString());
				return false;
			}
		});
		
		display = getWindowManager().getDefaultDisplay();

		md = TCPClient.getSingletonObject().md;

		tcpClient = TCPClient.getSingletonObject();
		//tcpClient = new TCPClient(md);
		//tcpClient.start();

		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				updatePosition();
			}

		}, 0, 5000);

		showCurrent(m_iOverlayType);
	}

	private void showCurrent(int OverlayType) {
		updateStreet();
		ShowCurrentOverlay scOverlay = new ShowCurrentOverlay(m_MapView,
				m_iOverlayType, md, this);
		List<Overlay> listOfOverlay = m_MapView.getOverlays();
		listOfOverlay.clear();
		listOfOverlay.add(scOverlay);

		m_MapView.invalidate();

	}

	private void updateStreet() {
		if (m_MapView.getZoomLevel() < 14)
			return;
		long start = System.currentTimeMillis();
		if (count == 0)
			streetList.clear();
		else
			streetList2.clear();
		
		GeoPoint top = m_MapView.getProjection().fromPixels(0, 0);
		GeoPoint bottom = m_MapView.getProjection().fromPixels(display.getWidth(), display.getHeight());//latDouble - height[l] / 2;
		double latMin = bottom.getLatitudeE6() / 1E6;
		Log.d("latMin", latMin + "");
		double latMax = top.getLatitudeE6() / 1E6;
		Log.d("latMax", latMax + "");
		double lonMin = top.getLongitudeE6() / 1E6;
		Log.d("lonMin", lonMin + "");
		double lonMax = bottom.getLongitudeE6() / 1E6;
		Log.d("lonMax", lonMax + "");
		
		double distanceLat = latMax - latMin;
		latMin -= distanceLat * 0.2;
		latMax += distanceLat * 0.2;
		
		double distanceLon = lonMax - lonMin;
		lonMin -= distanceLon * 0.2;
		lonMax += distanceLon * 0.2;
		
		String sql = "SELECT * FROM " + DatabaseHelper.NODE_TABLE_NAME
				+ " WHERE Lat <= " + latMax + " AND Lat >= " + latMin
				+ " AND Lon <= " + lonMax + " AND Lon >= " + lonMin;
		Cursor cursor = md.getReadableDatabase().rawQuery(sql, null);

		Log.d("Time SQL", "" + (System.currentTimeMillis() - start));

		if (cursor != null)
			cursor.moveToFirst();

		DrawableStreet street = null;
		int streetID = -1;
		int index = 0;
		start = System.currentTimeMillis();
		do {
			if (streetID != cursor.getInt(4)) {
				streetID = cursor.getInt(4);
				if (street != null) {
					if (count == 0)
						streetList.add(street);
					else
						streetList2.add(street);
				}

				street = new DrawableStreet();
				street.setId(streetID);
				String streetSQL = "SELECT * FROM "
						+ DatabaseHelper.STREET_TABLE_NAME
						+ " WHERE StreetID = " + streetID;

				Cursor cursor2 = md.getReadableDatabase().rawQuery(streetSQL,
						null);

				if (cursor2.getCount() == 0) {
					Log.d("Street SQL", streetID + "");
				} else {
					cursor2.moveToFirst();
					street.setType(cursor2.getString(2));
				}
			}

			NodeDrawable node = new NodeDrawable();
			node.setLat(cursor.getFloat(2));
			node.setLon(cursor.getFloat(3));
			node.setSpeed(cursor.getString(5));
			if (node.getSpeed().equals("")) {
				// float v = (float) (r.nextFloat()*30.0);
				node.setSpeed("10.0");
			}
			node.setDensity(cursor.getString(6));
			street.m_nodeArray.add(node);

			long now = System.currentTimeMillis();
			long time = 0;
			if (cursor.getString(7).equals(""))
				time = 0;
			else
				time = Long.parseLong(cursor.getString(7));

			if (now - time > 300000) {
				// Update
				String data = "UPDATE:" + cursor.getInt(0);
				if (tcpClient != null && tcpClient.out != null) {
					tcpClient.out.println(data);
					tcpClient.out.flush();
				}
			}
			index += 1;// step[m_MapView.getZoomLevel()];
		} while (cursor.moveToPosition(index));

		Log.d("Time add to Street", "" + (System.currentTimeMillis() - start));
	}

	protected void onStop() {
		super.onStop();
		Log.d("OnStop", "STOP");
		timer.cancel();
		//tcpClient.stop();
		//md.close();
	}

	private void updatePosition() {
		// TODO Auto-generated method stub
		m_MapView.postInvalidate();

		updateStreet();
		count = (count + 1) % 2;
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
