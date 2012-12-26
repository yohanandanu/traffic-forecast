package vn.edu.hcmut.cse.trafficdirection.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import org.xmlpull.v1.XmlSerializer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

public class GPSService extends Service {
	private static LocationManager locationManager;
	protected static MyLocationListener handler;

	private Timer timer;
	private InetAddress serverAddr;
	private int delay;

	private long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES;
	private long MINIMUM_TIME_BETWEEN_UPDATES;

	private static int count = 0;
	private static double preLat = 0.0;
	private static double preLon = 0.0;
	private static float preTime = 0;

	static String trackList = "";
	public String tempList = "";

	// flag for GPS status
	boolean isGPSEnabled = false;

	// flag for network status
	boolean isNetworkEnabled = false;

	// flag for GPS status
	boolean canGetLocation = false;

	// private float CurrentLongtitude;
	// private float CurrentLatitude;

	private static final String TAG = "GPS Service";

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		Toast.makeText(this, "My Service Created", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onCreate");
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {

		runService();

		updateData();

		super.onStart(intent, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		runService();

		updateData();

		return Service.START_STICKY;
	}

	private void runService() {
		// TODO Auto-generated method stub

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		handler = new MyLocationListener();

		final Criteria criteria = new Criteria();

		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);

		final String bestProvider = locationManager.getBestProvider(criteria,
				true);

		MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = Long.parseLong(PreferenceManager
				.getDefaultSharedPreferences(this.getApplicationContext())
				.getString(MainActivity.KEY_GPS_DISTANCE_LOGGING_INTERVAL,
						MainActivity.VAL_GPS_DISTANCE_LOGGING_INTERVAL));

		MINIMUM_TIME_BETWEEN_UPDATES = Long.parseLong(PreferenceManager
				.getDefaultSharedPreferences(this.getApplicationContext())
				.getString(MainActivity.KEY_GPS_TIME_LOGGING_INTERVAL,
						MainActivity.VAL_GPS_TIME_LOGGING_INTERVAL)) * 1000;

		if (bestProvider != null && bestProvider.length() > 0) {
			locationManager.requestLocationUpdates(bestProvider,
					MINIMUM_TIME_BETWEEN_UPDATES,
					MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, handler);
		} else {
			final List<String> providers = locationManager.getAllProviders();

			for (final String provider : providers) {
				locationManager.requestLocationUpdates(provider,
						MINIMUM_TIME_BETWEEN_UPDATES,
						MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, handler);
			}
		}
		Toast.makeText(getApplicationContext(), bestProvider, Toast.LENGTH_LONG)
				.show();

	}

	public void sendToServer() {
		/*----------------------------sendData--------------------------------------------------*/
		if (tempList != "") {
			try {

				// Retrieve the ServerName
				String serverIP = "www.vre.cse.hcmut.edu.vn";
				serverAddr = InetAddress.getByName(serverIP);

				/* Create new UDP-Socket */
				DatagramSocket clientSocket = new DatagramSocket();

				/* Prepare some data to be sent. */

				// byte [] sendData = new byte[1024];
				byte[] buf = tempList.getBytes();

				/*
				 * Create UDP-packet with data & destination(url+port)
				 */
				DatagramPacket packet = new DatagramPacket(buf, buf.length,
						serverAddr, 6666);
				Log.d("a11", "C Sending: '" + new String(buf) + "'");

				/* Send out the packet */
				clientSocket.send(packet);

				clientSocket.close();

				tempList = "";

			} catch (Exception e) {

			}
		}

		/*---------------------------------end sendData -------------------------------------------*/
	}

	public void updateData() {
		delay = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(
				this.getApplicationContext()).getString(
				MainActivity.KEY_SERVER_ADDRESS_INTERVAL,
				MainActivity.VAL_SERVER_ADDRESS_INTERVAL)) * 1000;
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				sendToServer();
			}
		}, 0, delay);
	}

	private class MyLocationListener implements LocationListener {
		public void onLocationChanged(Location location) {

			String latitude, longitude, elevation, accuracy, speed;
			String currentTime;

			Date date = new Date();
			DateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ssZ");
			TimeZone timezone = TimeZone.getTimeZone("GMT+7");
			dateFormat.setTimeZone(timezone);

			String onePoint, oneNode;

			float currentHour, currentMinute, currentSecond, curTime;
			double curLat, curLon, distance, duration;

			currentHour = date.getHours();
			currentMinute = date.getMinutes();
			currentSecond = date.getSeconds();

			currentTime = dateFormat.format(date);

			curTime = currentHour * 3600 + currentMinute * 60 + currentSecond;

			latitude = String.valueOf(location.getLatitude());
			longitude = String.valueOf(location.getLongitude());

			curLat = Float.parseFloat(latitude) * 1E6;
			curLon = Float.parseFloat(longitude) * 1E6;

			TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

			String device_id = tm.getDeviceId();

			if (count == 0) {
				preLat = curLat;
				preLon = curLon;
				preTime = 0;
			}

			if (location.hasAltitude()) {
				elevation = String.valueOf(location.getAltitude());
			} else {
				elevation = "0";
			}

			if (location.hasAccuracy()) {
				accuracy = String.valueOf(location.getAccuracy());
			} else {
				accuracy = "0";
			}

			if (location.hasSpeed()) {
				speed = String.valueOf(location.getSpeed());
			} else {

				if (curTime < preTime) {
					duration = 86400 - preTime + curTime;
				} else {
					duration = curTime - preTime;
				}
				distance = calculateDistance(preLat, preLon, curLat, curLon);
				speed = String.valueOf(distance * 1000 / duration);
				preLat = curLat;
				preLon = curLon;
				preTime = curTime;
			}

			oneNode = device_id + " " + latitude + " " + longitude + " "
					+ elevation + " " + currentTime + " " + accuracy + " "
					+ speed + " ";
			// trackList = trackList + oneNode;

			String newLine = System.getProperty("line.separator");
			onePoint = "\t  <trkpt lat=\"" + latitude + "\" lon=\"" + longitude
					+ "\">" + newLine + "\t\t<ele>" + elevation + "</ele>"
					+ newLine + "\t\t<time>" + currentTime + "</time" + newLine
					+ "\t\t<extensions>" + newLine + "\t\t  <ogt10:accuracy>"
					+ accuracy + "</ogt10:accuracy>" + newLine
					+ "\t\t  <speed>" + speed + "</speed>" + newLine
					+ "\t\t</extensions>" + newLine + "\t  <trkpt>" + newLine;

			trackList += onePoint;
			tempList = tempList + oneNode;

			count += 1;

		}

		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}
	}

	public void exportGPX() {

		Date date = new Date();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

		Calendar calender = Calendar.getInstance();
		String year = Integer.toString(calender.get(Calendar.YEAR));
		String month = Integer.toString(calender.get(Calendar.MONTH) + 1);
		String day = Integer.toString(calender.get(Calendar.DAY_OF_MONTH));
		String hour = Integer.toString(calender.get(Calendar.HOUR_OF_DAY));
		String minute = Integer.toString(calender.get(Calendar.MINUTE));
		String sec = Integer.toString(calender.get(Calendar.SECOND));
		String filespec = year + month + day + hour + minute + sec;
		String filename = "Track" + filespec + ".gpx";

		String currentTime = dateFormat.format(date);
		// 2012-05-03T00:31:24Z
		String trackName = year + "-" + month + "-" + day + " " + hour + "-"
				+ minute;

		String ext_path = PreferenceManager.getDefaultSharedPreferences(
				this.getApplicationContext()).getString(
				MainActivity.KEY_EXTERNAL_STORAGE,
				MainActivity.VAL_EXTERNAL_STORAGE);
		File sdcard_path = Environment.getExternalStorageDirectory();
		String path = sdcard_path + "/" + ext_path;
		String file_path = sdcard_path + "/" + ext_path + "/" + filename;

		Toast.makeText(getBaseContext(), filename, Toast.LENGTH_SHORT).show();

		String GPX_TAG = "";
		GPX_TAG += "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>"
				+ "\n";
		GPX_TAG += "<gpx version=\"1.1\" creator=\"Traffic Direction\""
				+ " xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/gpx/1/1/gpx.xsd\""
				+ " xmlns=\"http://www.topografix.com/GPX/1/1\""
				+ " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
				+ " xmlns:gpx10=\"http://www.topografix.com/GPX/1/0\""
				+ " xmlns:ogt10=\"http://gpstracker.android.sogeti.nl/GPX/1/0\">";

		File myFile = new File(path);
		BufferedWriter bufferedWriter = null;

		boolean success = false;
		if (!myFile.exists()) {
			success = myFile.mkdirs();
		}
		if (success || myFile.exists()) {
			try {
				String newLine = System.getProperty("line.separator");
				bufferedWriter = new BufferedWriter(new FileWriter(file_path));
				bufferedWriter.append(GPX_TAG).append(newLine);
				bufferedWriter.append("  <metadata>" + newLine + "\t<time>"
						+ currentTime + "</time>" + newLine + "  </metadata>"
						+ newLine);
				bufferedWriter.append("  <trk>" + newLine);
				bufferedWriter.append("\t<name>Track" + trackName + "</name>"
						+ newLine);
				bufferedWriter.append(trackList);
				bufferedWriter.append("  </trk>" + newLine);
				bufferedWriter.append("</gpx>");
				bufferedWriter.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	public void exportXML() {
		// Date date = Calendar.getInstance().getTime();
		// String tmp = trackList;

		try {
			// File myfile2 = new File()
			Calendar calender = Calendar.getInstance();
			String year = Integer.toString(calender.get(Calendar.YEAR));
			String month = Integer.toString(calender.get(Calendar.MONTH) + 1);
			String day = Integer.toString(calender.get(Calendar.DAY_OF_MONTH));
			String hour = Integer.toString(calender.get(Calendar.HOUR_OF_DAY));
			String minute = Integer.toString(calender.get(Calendar.MINUTE));
			String sec = Integer.toString(calender.get(Calendar.SECOND));
			String filespec = year + month + day + hour + minute;
			String filename = "Track" + filespec + ".gpx";
			String currentTime = year + "-" + month + "-" + day + "T" + hour
					+ ":" + minute + ":" + sec + "Z";
			// 2012-05-03T00:31:24Z
			String trackName = year + "-" + month + "-" + day + " " + hour
					+ "-" + minute;
			Toast.makeText(getBaseContext(), filename, Toast.LENGTH_SHORT)
					.show();
			String ext_path = PreferenceManager.getDefaultSharedPreferences(
					this.getApplicationContext()).getString(
					MainActivity.KEY_EXTERNAL_STORAGE,
					MainActivity.VAL_EXTERNAL_STORAGE);
			File sdcard_path = Environment.getExternalStorageDirectory();
			String path = sdcard_path + "/" + ext_path;
			String file_path = sdcard_path + "/" + ext_path + "/" + filename;
			// String file_path = sdcard_path+"/"+filename;
			File myFile = new File(path);
			boolean success = false;
			if (!myFile.exists()) {
				success = myFile.mkdirs();
			}
			if (success || myFile.exists()) {
				File myGPXFile = new File(file_path);
				FileOutputStream fIn = new FileOutputStream(myGPXFile);
				XmlSerializer serializer = Xml.newSerializer();
				try {
					serializer.setOutput(fIn, "UTF-8");
					serializer.startDocument(null, Boolean.valueOf(true));
					serializer
							.setFeature(
									"http://xmlpull.org/v1/doc/features.html#indent-output",
									true);
					serializer.startTag(null, "gpx");
					serializer.attribute(null, "version", "1.1");
					serializer.attribute(null, "creator", "My Apps");
					serializer
							.attribute(null, "xsi:schemaLocation",
									"http://www.topografix.com/GPX/1/1 http://www.topografix.com/gpx/1/1/gpx.xsd");
					serializer.attribute(null, "xmlns",
							"http://www.topografix.com/GPX/1/1");
					serializer.attribute(null, "xmlns:xsi",
							"http://www.w3.org/2001/XMLSchema-instance");
					serializer.attribute(null, "xmlns:gpx10",
							"http://www.topografix.com/GPX/1/0");
					serializer.attribute(null, "xmlns:ogt10",
							"http://gpstracker.android.sogeti.nl/GPX/1/0");
					serializer.startTag(null, "metadata");
					serializer.startTag(null, "time");
					serializer.text(currentTime);
					serializer.endTag(null, "time");
					serializer.endTag(null, "metadata");

					serializer.startTag(null, "trk");
					serializer.startTag(null, "name");
					serializer.text("Track" + trackName);
					serializer.endTag(null, "name");
					serializer.startTag(null, "trkseg");
					// serializer.text(String.valueOf(trackList));
					// lat lon ele curTime acc speed

					String[] src = trackList.split(" ");
					int mSize = src.length;
					for (int i = 0; i < mSize; i = i + 6) {
						serializer.startTag(null, "trkpt");
						serializer.attribute(null, "lat", src[i]);
						serializer.attribute(null, "lon", src[i + 1]);
						serializer.startTag(null, "ele");
						// serializer.text(src[i+2]);
						serializer.text(String.valueOf(i));
						serializer.endTag(null, "ele");
						serializer.startTag(null, "time");
						serializer.text(src[i + 3]);
						serializer.endTag(null, "time");
						serializer.startTag(null, "extensions");
						serializer.startTag(null, "ogt10:accuracy");
						serializer.text(src[i + 4]);
						serializer.endTag(null, "ogt10:accuracy");
						serializer.startTag(null, "speed");
						serializer.text(src[i + 5]);
						serializer.endTag(null, "speed");
						serializer.endTag(null, "extensions");
						serializer.endTag(null, "trkpt");
					}

					serializer.endTag(null, "trkseg");

					serializer.endTag(null, "trk");

					serializer.endTag(null, "gpx");
					serializer.endDocument();
					serializer.flush();
					fIn.close();
					// TextView tv = (TextView)findViewById(R.);

				} catch (Exception e) {
					Log.e("Exception", "Exception occured in wroting");
				}
			}
		} catch (Exception e) {
			Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT)
					.show();
		}
		;
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();

		if (trackList != "") {
			// exportXML();
			exportGPX();
		}
		trackList = "";
		locationManager.removeUpdates(handler);

		super.onDestroy();
	}

	private double calculateDistance(double lat1, double lon1, double lat2,
			double lon2) {
		double deltalat = lat2 - lat1;
		double deltalon = lon2 - lon2;
		double EARTH_RADIUS = 6367.45;
		double a = Math.sin(deg2rad(deltalat / 2))
				* Math.sin(deg2rad(deltalat / 2)) + Math.cos(deg2rad(lat1))
				* Math.cos(deg2rad(lat2)) * Math.sin(deg2rad(deltalon / 2))
				* Math.sin(deg2rad(deltalon / 2));
		// a = (sin(dlat/2))^2 + cos(lat1) * cos(lat2) * (sin(dlon/2))^2
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double distance = EARTH_RADIUS * c;
		return distance;
	}

	private double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	// private double rad2deg(double rad) {
	// return (rad * 180.0 / Math.PI);
	// }
}
