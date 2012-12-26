package vn.edu.hcmut.cse.trafficdirection.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


import vn.edu.hcmut.cse.trafficdirection.database.DatabaseHelper;
import vn.edu.hcmut.cse.trafficdirection.network.TCPClient;
import vn.edu.hcmut.cse.trafficdirection.node.DrawableStreet;
import vn.edu.hcmut.cse.trafficdirection.node.NodeDrawable;
import vn.edu.hcmut.cse.trafficdirection.overlay.ShowCurrentOverlay;

import vn.edu.hcmut.cse.trafficdirection.main.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class MainActivity extends MapActivity {
	public static final String PATH_TO_TMP_FILE = "/tmpFile";

	public static final double[] height = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
			11, 12, 13, 0.169600, 0.084800, 0.042400, 0.021200, 0.010600,
			0.005300, 0.002650, 21 };
	public static final double[] width = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
			11, 12, 13, 0.12192, 0.06096, 0.03048, 0.015240, 0.007620, 0.00381,
			0.001905, 21 };
	public static final double[] step = { 0, 21, 20, 19, 18, 17, 16, 15, 14,
			13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1 };

	// Nhom GPS
	protected static LocationManager locationManager;
	public final static String KEY_GPS_DISTANCE_LOGGING_INTERVAL = "gps.distance.logging.interval";
	public final static String KEY_GPS_TIME_LOGGING_INTERVAL = "gps.time.logging.interval";
	public static final String KEY_GPS_SETTINGS = "gps.settings";
	public static final String KEY_EXTERNAL_STORAGE = "ext.storage";
	public static final String KEY_SERVER_ADDRESS_IP = "gps.server.address.ip";
	public static final String KEY_SERVER_ADDRESS_INTERVAL = "gps.server.address.interval";
	public final static String VAL_GPS_DISTANCE_LOGGING_INTERVAL = "10";
	public final static String VAL_GPS_TIME_LOGGING_INTERVAL = "15";
	public final static String VAL_EXTERNAL_STORAGE = "/GPSTrackingFiles";
	public final static String VAL_SERVER_ADDRESS_INTERVAL = "300";

	boolean isGPSEnabled = false;
	static boolean isTracking = false;

	// Nhom hien thi
	public static final int OVERLAY_V = 1;
	public static final int OVERLAY_D = 2;

	// Nhom hien thi
	private MapView m_MapView = null;
	public GeoPoint m_CurrentLocation = null;
	private int m_iOverlayType = OVERLAY_V;

	private String GPXfile = null;
	private String Point1 = null;
	private String Point2 = null;
	
	Dialog dialog;// = new Dialog(MainActivity.this,R.style.OverLayDialog);
	
	File nodeSQLFile = null;
	File streetSQLFile = null;

	private DatabaseHelper md;
	private TCPClient tcpClient = null;

	private Timer timer = null;

	public int count;

	private boolean isShowCurrent;

	public ArrayList<DrawableStreet> streetList = new ArrayList<DrawableStreet>();
	public ArrayList<DrawableStreet> streetList2 = new ArrayList<DrawableStreet>();

	public int zoomLevel;
	
	//private Random r;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("Start", "Start");
		setContentView(R.layout.activity_main);

		// Read map data and write to Database
		md = new DatabaseHelper(this);

		// md.getWritableDatabase().execSQL("DROP TABLE " +
		// DatabaseHelper.NODE_TABLE_NAME);
		// md.getWritableDatabase().execSQL("DROP TABLE " +
		// DatabaseHelper.STREET_TABLE_NAME);
		// md.onCreate(md.getWritableDatabase());

		isShowCurrent = false;
		count = 0;

		File tmp = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + PATH_TO_TMP_FILE);

		if (!tmp.exists()) {
			File oldDb = new File(
					"/data/data/vn.edu.hcmut.cse.trafficdirection.main/databases/MapDatabase");
			oldDb.delete();

			try {
				InputStream fis = getApplicationContext().getAssets().open(
						"MapDatabase");
				FileOutputStream fos = new FileOutputStream(oldDb);

				byte[] buffer = new byte[1024];
				int length;

				while ((length = fis.read(buffer)) > 0) {
					fos.write(buffer, 0, length);
				}

				fis.close();
				fos.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
			}

			try {
				tmp.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		else
		{
			tmp.delete();
			try {
				tmp.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		tcpClient = new TCPClient(md);
		tcpClient.start();
		
		dialog = new Dialog(MainActivity.this,R.style.DialogTitleStyle);

		m_MapView = (MapView) findViewById(R.id.mapView);
		LinearLayout zoomLayout = (LinearLayout) findViewById(R.id.layout_MainMenu);
		zoomLayout.addView(m_MapView.getZoomControls(),
				new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT));
		m_MapView.displayZoomControls(true);
		m_MapView.getController().setZoom(17);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Location location = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		isGPSEnabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);

		// Se thay bang vi tri hien tai lay tu GPS
		double currentLat = 10.770579;
		double currentLon = 106.657963;

		if (location != null) {
			currentLat = location.getLatitude();
			currentLon = location.getLongitude();
		}

		m_CurrentLocation = new GeoPoint((int) (currentLat * 1E6),
				(int) (currentLon * 1E6));
		m_MapView.getController().animateTo(m_CurrentLocation);

		m_MapView.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent ev) {
				// TODO Auto-generated method stub
				m_CurrentLocation = m_MapView.getMapCenter();
				// if(isShowCurrent)
				// updateStreet();
				// Log.d("CenterPoint", m_CurrentLocation.toString());
				return false;
			}
		});

		if (!isGPSEnabled)
			showSettingsAlert();

		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				updatePosition();
			}

		}, 0, 5000);
	}

	protected void onStop() {
		super.onStop();
		Log.d("OnStop", "STOP");
		timer.cancel();
	}

	private void updateStreet() {
		if (m_MapView.getZoomLevel() < 14)
			return;
		long start = System.currentTimeMillis();
		if (count == 0)
			streetList.clear();
		else
			streetList2.clear();

		int l = m_MapView.getZoomLevel();
		double latDouble = m_CurrentLocation.getLatitudeE6() / 1E6;
		double lonDouble = m_CurrentLocation.getLongitudeE6() / 1E6;
		double latMin = latDouble - height[l] / 2;
		double latMax = latDouble + height[l] / 2;
		double lonMin = lonDouble - width[l] / 2;
		double lonMax = lonDouble + width[l] / 2;
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
			if (node.getSpeed().equals(""))
			{
				//float v = (float) (r.nextFloat()*30.0);
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

	private void updatePosition() {
		// TODO Auto-generated method stub
		if (isShowCurrent) {
			m_MapView.postInvalidate();
			
			updateStreet();
			count = (count + 1) % 2;
		}
	}

	/*private void showGPX(String file) {
		// TODO Auto-generated method stub
		GPXReader r = new GPXReader(file);
		r.readXMLfromResource(this);

		try {
			r.parseStructure();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		GPXOverlay mapOverlay = new GPXOverlay(this, m_MapView, r.handler.data);
		List<Overlay> listOfOverlay = m_MapView.getOverlays();
		listOfOverlay.clear();
		listOfOverlay.add(mapOverlay);

		m_MapView.invalidate();
	}*/

	private void showSettingsAlert() {
		// TODO Auto-generated method stub
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				MainActivity.this);

		// Setting Dialog Title
		alertDialog.setTitle(R.string.setting_gps);

		// Setting Dialog Message
		alertDialog
				.setMessage(R.string.gps_not_enable_yet);

		// On pressing Settings button
		alertDialog.setPositiveButton("Settings",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(
								Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						MainActivity.this.startActivity(intent);
					}
				});

		// on pressing cancel button
		alertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		// Showing Alert Message
		alertDialog.show();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.settings:
			Toast.makeText(getApplicationContext(), "Settings is Selected",
					Toast.LENGTH_SHORT).show();
			startActivity(new Intent(MainActivity.this, Preferences.class));
			break;
		case R.id.buildRoute:
			
			Toast.makeText(getApplicationContext(), "Build Route is Selected",
					Toast.LENGTH_SHORT).show();
			dialog.setContentView(R.layout.dialog_directions_input);
			dialog.setTitle(R.string.dialog_input_direct_title);
			
			Button p1 = (Button) dialog.findViewById(R.id.bt_point1);
			
			p1.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent it = new Intent(getApplicationContext(),
							TouchPoint.class);
					startActivity(it);
				}
			});
			
			Button p2 = (Button) dialog.findViewById(R.id.bt_point2);
			
			p2.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent it = new Intent(getApplicationContext(),
							TouchPoint.class);
					startActivity(it);
				}
			});

			Button dialogButton = (Button) dialog.findViewById(R.id.bt_direction);

			dialogButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dialog.dismiss();
				}
			});

			dialog.show();
			break;
		case R.id.forecast:
			Toast.makeText(getApplicationContext(), "Forecast is Selected",
					Toast.LENGTH_SHORT).show();
			final Dialog dialogForecast = new Dialog(MainActivity.this,R.style.DialogTitleStyle);
			dialogForecast.setContentView(R.layout.dialog_forecast);
			dialogForecast.setTitle(R.string.dialog_forecast_title);
			Button dlButton = (Button) dialogForecast.findViewById(R.id.bt_forecast_ok);

			dlButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dialogForecast.dismiss();
				}
			});
			dialogForecast.show();

			break;
		case R.id.turnPower:
			Toast.makeText(getApplicationContext(), "Turn is Selected",
					Toast.LENGTH_SHORT).show();
			final Dialog trackingDialog = new Dialog(MainActivity.this,R.style.DialogTitleStyle);
			trackingDialog.setContentView(R.layout.dialog_tracking_controls);
			trackingDialog
					.setTitle(R.string.tracking_controls);
			Button startButton = (Button) trackingDialog
					.findViewById(R.id.button_start);
			startButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					startService(new Intent(MainActivity.this, GPSService.class));
					isTracking = true;
					trackingDialog.hide();
				}
			});

			Button stopButton = (Button) trackingDialog
					.findViewById(R.id.button_stop);
			stopButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					stopService(new Intent(MainActivity.this, GPSService.class));
					isTracking = false;
					trackingDialog.hide();
				}
			});

			if (!isTracking) {
				stopButton.setClickable(false);
				stopButton.setEnabled(false);
			} else {
				startButton.setClickable(false);
				startButton.setEnabled(false);
			}

			trackingDialog.show();
			break;

		case R.id.showCurrent:
			Toast.makeText(getApplicationContext(), "Show Current is Selected",
					Toast.LENGTH_SHORT).show();
			isShowCurrent = true;
			final Dialog overlayDialog = new Dialog(MainActivity.this,R.style.DialogTitleStyle);
			overlayDialog.setContentView(R.layout.dialog_overlay);
			overlayDialog.setTitle(R.string.show_current_dialog_title);
			Button okButton = (Button) overlayDialog.findViewById(R.id.btnOk);

			okButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					RadioGroup radioSexGroup = (RadioGroup) overlayDialog
							.findViewById(R.id.radioOverlay);
					int selectedId = radioSexGroup.getCheckedRadioButtonId();
					if (selectedId == R.id.radioVelocity)
						m_iOverlayType = OVERLAY_V;
					else
						m_iOverlayType = OVERLAY_D;

					showCurrent(m_iOverlayType);
					overlayDialog.hide();
				}
			});

			overlayDialog.show();
			break;
		case R.id.trackList:
			isShowCurrent = false;
			Toast.makeText(getApplicationContext(), "Track", Toast.LENGTH_SHORT)
					.show();
			trackList();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void showCurrent(int OverlayType) {
		updateStreet();
		isShowCurrent = true;
		ShowCurrentOverlay scOverlay = new ShowCurrentOverlay(m_MapView,
				m_iOverlayType, md, this);
		List<Overlay> listOfOverlay = m_MapView.getOverlays();
		listOfOverlay.clear();
		listOfOverlay.add(scOverlay);

		m_MapView.invalidate();

	}

	private void trackList() {
		Intent it = new Intent(getApplicationContext(),
				AndroidListFilesActivity.class);
		Bundle extras = new Bundle();
		extras.putString("PATH", VAL_EXTERNAL_STORAGE);
		it.putExtras(extras);
		startActivityForResult(it, 0);
		//startActivity(it);
	}
	
	protected void onResume()
	{
		super.onResume();
		String mode = null;
		try {
			BufferedReader in = new BufferedReader(new FileReader(Environment.getExternalStorageDirectory().getAbsolutePath() + PATH_TO_TMP_FILE));
			
			mode = in.readLine();
			if(mode != null)
			{
				if(mode.equals("1"))
				{
					GPXfile = in.readLine();
				}
				else if(mode.equals("2"))
				{
					Point1 = in.readLine();
					in.readLine();
					Point2 = in.readLine();
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
		
		if(mode != null && mode.equals("1"))
		{
			if(GPXfile != null)
			{
				//showGPX(GPXfile);
				Intent it = new Intent(getApplicationContext(), ShowGPXActivity.class);
				Bundle extras = new Bundle();
				extras.putString("FILE", GPXfile);
				it.putExtras(extras);
				startActivity(it);
			}
			File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + PATH_TO_TMP_FILE);
			f.delete();
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(mode != null && mode.equals("2"))
		{
			if(Point1 != null)
			{
				Log.d("Point1", Point1);
				EditText et1 = (EditText) dialog.findViewById(R.id.et_start);
				et1.setText(Point1);
				
			}
			
			if(Point2 != null)
			{
				Log.d("Point2", Point2);
				EditText et1 = (EditText) dialog.findViewById(R.id.et_end);
				et1.setText(Point2);
			}
		}
	}
}
