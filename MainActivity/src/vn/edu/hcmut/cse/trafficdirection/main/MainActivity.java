package vn.edu.hcmut.cse.trafficdirection.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import vn.edu.hcmut.cse.trafficdirection.database.DatabaseHelper;

import vn.edu.hcmut.cse.trafficdirection.main.R;
import vn.edu.hcmut.cse.trafficdirection.network.TCPClient;
import vn.edu.hcmut.cse.trafficdirection.overlay.MyPositionOverlay;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class MainActivity extends MapActivity {
	public static final String PATH_TO_FOLDER = "/TrafficDirection";
	public static final String PATH_TO_TMP_FILE = PATH_TO_FOLDER + "/tmpFile";

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
	public final static String VAL_EXTERNAL_STORAGE = PATH_TO_FOLDER
			+ "/GPSTrackingFiles";
	public final static String VAL_SERVER_ADDRESS_INTERVAL = "300";

	boolean isGPSEnabled = false;
	static boolean isTracking = false;

	// Nhom hien thi
	public static final int OVERLAY_V = 1;
	public static final int OVERLAY_D = 2;

	// Nhom hien thi
	private MapView m_MapView = null;
	private MapController mapController;
	public GeoPoint m_CurrentLocation = null;
	private int m_iOverlayType = OVERLAY_V;
	private MyPositionOverlay positionOverlay;

	private String GPXfile = null;
	private String Point1 = null;
	private String Point2 = null;

	Dialog dialog;// = new Dialog(MainActivity.this,R.style.OverLayDialog);

	File nodeSQLFile = null;
	File streetSQLFile = null;

	public int zoomLevel;
	
	private DatabaseHelper md;

	// private Random r;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("Start", "Start");
		setContentView(R.layout.activity_main);
		checkDirectoryExist();
		md = new DatabaseHelper(this);
		checkDatabaseExist();
		dialog = new Dialog(MainActivity.this, R.style.DialogTitleStyle);
		m_MapView = (MapView) findViewById(R.id.mapView);
		LinearLayout zoomLayout = (LinearLayout) findViewById(R.id.layout_MainMenu);
		zoomLayout.addView(m_MapView.getZoomControls(),
				new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT));
		m_MapView.displayZoomControls(true);
		mapController = m_MapView.getController();
		mapController.setZoom(17);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Location location = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		isGPSEnabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);

		// Se thay bang vi tri hien tai lay tu GPS

		positionOverlay = new MyPositionOverlay();
		positionOverlay.GPXOverlay(this, m_MapView);
		List<Overlay> overlays = m_MapView.getOverlays();
		overlays.add(positionOverlay);

		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		String provider = locationManager.getBestProvider(criteria, true);
		Location location1 = null;
		if (provider != null) {
			location1 = locationManager.getLastKnownLocation(provider);
			updateWithNewLocation(location1);
			locationManager.requestLocationUpdates(provider, 2000, 10,
					locationListener);
		} else
			updateWithNewLocation(location);
		if (!isGPSEnabled)
			showSettingsAlert();
		
		TCPClient.getSingletonObject().setDatabase(md);
		TCPClient.getSingletonObject().start();
	}

	private void checkDirectoryExist() {
		// TODO Auto-generated method stub
		File directory = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + PATH_TO_FOLDER);

		if (!directory.isDirectory()) {
			directory.mkdir();
		}
	}

	private final LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			updateWithNewLocation(location);
		}

		public void onProviderDisabled(String provider) {

			updateWithNewLocation(null);
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	private void showSettingsAlert() {
		// TODO Auto-generated method stub
		final Dialog showSettingDialog = new AlertDialog.Builder(
				MainActivity.this)
				.setIcon(R.drawable.alert_dialog_icon)
				.setTitle(R.string.setting_gps)
				.setMessage(R.string.gps_not_enable_yet)
				.setPositiveButton(R.string.menu_settings,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {

								Intent intent = new Intent(
										Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								MainActivity.this.startActivity(intent);
							}
						})
				.setNegativeButton(R.string.alert_dialog_cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dialog.dismiss();
								/* User clicked Cancel so do some stuff */
							}
						}).create();
		showSettingDialog.show();
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
			// Toast.makeText(getApplicationContext(), "Settings is Selected",
			// Toast.LENGTH_SHORT).show();
			startActivity(new Intent(MainActivity.this, Preferences.class));
			break;
		case R.id.buildRoute:
			File tmp = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath() + PATH_TO_TMP_FILE);

			tmp.delete();
			try {
				tmp.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
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

			Button dialogButton = (Button) dialog
					.findViewById(R.id.bt_direction);

			dialogButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if (Point1 == null) {
						Toast.makeText(getApplicationContext(),
								R.string.please_input_startpoint,
								Toast.LENGTH_SHORT).show();
					} else if (Point2 == null) {
						Toast.makeText(getApplicationContext(),
								R.string.please_input_endpoint,
								Toast.LENGTH_SHORT).show();
					} else {
						if(TCPClient.getSingletonObject() != null && TCPClient.getSingletonObject().out != null)
						{
							TCPClient.getSingletonObject().out.println("BULIDROAD:" + Point1.toString() + ":" + Point2.toString());
						}
						Intent it = new Intent(getApplicationContext(),
								ShowBuildRouteActivity.class);
						Bundle extras = new Bundle();
						extras.putString("POINT1", Point1);
						extras.putString("POINT2", Point2);
						it.putExtras(extras);
						startActivity(it);

						Point1 = null;
						Point2 = null;
						File tmp = new File(Environment
								.getExternalStorageDirectory()
								.getAbsolutePath()
								+ PATH_TO_TMP_FILE);
						tmp.delete();
						try {
							tmp.createNewFile();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						dialog.dismiss();
					}
				}
			});

			dialog.show();
			break;
		case R.id.forecast:
			// Toast.makeText(getApplicationContext(), "Forecast is Selected",
			// Toast.LENGTH_SHORT).show();
			final Dialog dialogForecast = new Dialog(MainActivity.this,
					R.style.DialogTitleStyle);
			dialogForecast.setContentView(R.layout.dialog_forecast);
			dialogForecast.setTitle(R.string.dialog_forecast_title);
			Button dlButton = (Button) dialogForecast
					.findViewById(R.id.bt_forecast_ok);

			final EditText et = (EditText) dialogForecast
					.findViewById(R.id.forecast_input_time);

			dlButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent it = new Intent(getApplicationContext(),
							ForecastActivity.class);
					Bundle extras = new Bundle();

					if (et.getText().toString().equalsIgnoreCase("")) {
						Toast.makeText(getApplicationContext(),
								R.string.please_input_time, Toast.LENGTH_SHORT)
								.show();
					} else {
						int minutes = Integer.parseInt(et.getText().toString());
						if (minutes < 10 || minutes > 120) {
							Toast.makeText(getApplicationContext(),
									R.string.error_input_time,
									Toast.LENGTH_SHORT).show();
						} else {
							extras.putString("TIME", et.getText().toString());
							it.putExtras(extras);
							long time = Long.parseLong(et.getText().toString());
							time = time*60*1000 + System.currentTimeMillis();
							startActivity(it);
							if(TCPClient.getSingletonObject() != null && TCPClient.getSingletonObject().out != null)
								TCPClient.getSingletonObject().out.println("FORECAST:"+time +"");
							dialogForecast.dismiss();
						}
					}
				}
			});
			dialogForecast.show();

			break;
		case R.id.turnPower:
			// Toast.makeText(getApplicationContext(), "Turn is Selected",
			// Toast.LENGTH_SHORT).show();
			final Dialog trackingDialog = new Dialog(MainActivity.this,
					R.style.DialogTitleStyle);
			trackingDialog.setContentView(R.layout.dialog_tracking_controls);
			trackingDialog.setTitle(R.string.tracking_controls);
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
			// Toast.makeText(getApplicationContext(),
			// "Show Current is Selected",
			// Toast.LENGTH_SHORT).show();
//			final Dialog overlayDialog = new Dialog(MainActivity.this,
//					R.style.DialogTitleStyle);
//			overlayDialog.setContentView(R.layout.dialog_overlay);
//			overlayDialog.setTitle(R.string.show_current_dialog_title);
//			Button okButton = (Button) overlayDialog.findViewById(R.id.btnOk);
//
//			okButton.setOnClickListener(new OnClickListener() {
//				public void onClick(View v) {
//					RadioGroup radioSexGroup = (RadioGroup) overlayDialog
//							.findViewById(R.id.radioOverlay);
//					int selectedId = radioSexGroup.getCheckedRadioButtonId();
//					if (selectedId == R.id.radioVelocity)
//						m_iOverlayType = OVERLAY_V;
//					else
//						m_iOverlayType = OVERLAY_D;

					// showCurrent(m_iOverlayType);
					Intent it = new Intent(getApplicationContext(),
							ShowCurrentActivity.class);
					Bundle extras = new Bundle();
					extras.putString("OVERLAY", m_iOverlayType + "");
					it.putExtras(extras);
					startActivity(it);

//					overlayDialog.hide();
//				}
//			});
//
//			overlayDialog.show();
			break;
		case R.id.trackList:
			// Toast.makeText(getApplicationContext(), "Track",
			// Toast.LENGTH_SHORT)
			// .show();
			trackList();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void trackList() {
		Intent it = new Intent(getApplicationContext(),
				AndroidListFilesActivity.class);
		Bundle extras = new Bundle();
		extras.putString("PATH", VAL_EXTERNAL_STORAGE);
		it.putExtras(extras);
		startActivityForResult(it, 0);
		// startActivity(it);
	}

	protected void onResume() {
		super.onResume();
		String mode = null;
		try {
			BufferedReader in = new BufferedReader(new FileReader(Environment
					.getExternalStorageDirectory().getAbsolutePath()
					+ PATH_TO_TMP_FILE));

			mode = in.readLine();
			if (mode != null) {
				if (mode.equals("1")) {
					GPXfile = in.readLine();
				} else if (mode.equals("2")) {
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

		if (mode != null && mode.equals("1")) {
			if (GPXfile != null) {
				// showGPX(GPXfile);
				Intent it = new Intent(getApplicationContext(),
						ShowGPXActivity.class);
				Bundle extras = new Bundle();
				extras.putString("FILE", GPXfile);
				it.putExtras(extras);
				startActivity(it);
			}
			File f = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath() + PATH_TO_TMP_FILE);
			f.delete();
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (mode != null && mode.equals("2")) {
			if (Point1 != null) {
				EditText et1 = (EditText) dialog.findViewById(R.id.et_start);
				et1.setText(R.string.pointed_on_map);

			}

			if (Point2 != null) {
				EditText et2 = (EditText) dialog.findViewById(R.id.et_end);
				et2.setText(R.string.pointed_on_map);
				// et2.setText("Point on Map");
			}
		}
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
		case KeyEvent.KEYCODE_BACK:
			final Dialog overlayDialog = new AlertDialog.Builder(
					MainActivity.this)
					.setIcon(R.drawable.alert_dialog_icon)
					.setTitle(R.string.alert_dialog_two_buttons_title)
					.setPositiveButton(R.string.alert_dialog_ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									/* User clicked OK so do some stuff */
									finish();
								}
							})
					.setNegativeButton(R.string.alert_dialog_cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									/* User clicked Cancel so do some stuff */
								}
							}).create();
			overlayDialog.show();
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void updateWithNewLocation(Location location) {
		if (location != null) {
			// Update my location marker
			positionOverlay.setLocation(location);
			// Update the map location.
			Double geoLat = location.getLatitude() * 1E6;
			Double geoLng = location.getLongitude() * 1E6;
			GeoPoint point = new GeoPoint(geoLat.intValue(), geoLng.intValue());
			mapController.animateTo(point);
		} else {
			positionOverlay.setLocation(location);
			Double geoLat = 10.770579 * 1E6;
			Double geoLng = 106.657963 * 1E6;
			GeoPoint point = new GeoPoint(geoLat.intValue(), geoLng.intValue());
			mapController.animateTo(point);
		}
	}

	private void checkDatabaseExist() {
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

		} else {
			tmp.delete();
			try {
				tmp.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
