package vn.edu.hcmut.cse.trafficdirection.main;

import java.io.File;

import vn.edu.hcmut.cse.trafficdirection.main.R;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.provider.Settings;

public class Preferences extends PreferenceActivity {

	@SuppressWarnings("unused")
	private static final String TAG = Preferences.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		// Update GPS logging interval summary to the current value
		Preference pref = findPreference(MainActivity.KEY_GPS_DISTANCE_LOGGING_INTERVAL);
		pref.setSummary(prefs.getString(
				MainActivity.KEY_GPS_DISTANCE_LOGGING_INTERVAL,
				MainActivity.VAL_GPS_DISTANCE_LOGGING_INTERVAL)
				+ " "
				+ getResources().getString(
						R.string.prefs_gps_logging_interval_meters)
				+ ". "
				+ getResources().getString(
						R.string.prefs_gps_distance_logging_interval_summary));
		pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				// Set summary with the interval and "meters"
				preference
						.setSummary(newValue
								+ " "
								+ getResources()
										.getString(
												R.string.prefs_gps_logging_interval_meters)
								+ ". "
								+ getResources()
										.getString(
												R.string.prefs_gps_distance_logging_interval_summary));
				return true;
			}
		});

		pref = findPreference(MainActivity.KEY_GPS_TIME_LOGGING_INTERVAL);
		pref.setSummary(prefs.getString(
				MainActivity.KEY_GPS_TIME_LOGGING_INTERVAL,
				MainActivity.VAL_GPS_TIME_LOGGING_INTERVAL)
				+ " "
				+ getResources().getString(
						R.string.prefs_gps_logging_interval_seconds)
				+ ". "
				+ getResources().getString(
						R.string.prefs_gps_time_logging_interval_summary));
		pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				// Set summary with the interval and "seconds"
				preference
						.setSummary(newValue
								+ " "
								+ getResources()
										.getString(
												R.string.prefs_gps_logging_interval_seconds)
								+ ". "
								+ getResources()
										.getString(
												R.string.prefs_gps_time_logging_interval_summary));
				return true;
			}
		});

		pref = findPreference(MainActivity.KEY_GPS_SETTINGS);
		pref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				startActivity(new Intent(
						Settings.ACTION_LOCATION_SOURCE_SETTINGS));
				return true;
			}
		});

		pref = findPreference(MainActivity.KEY_SERVER_ADDRESS_INTERVAL);
		pref.setSummary(prefs.getString(
				MainActivity.KEY_SERVER_ADDRESS_INTERVAL,
				MainActivity.VAL_SERVER_ADDRESS_INTERVAL)
				+ " "
				+ getResources().getString(R.string.prefs_server_time_interval)
				+ ". ");
		pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				// Set summary with the interval and "seconds"
				preference.setSummary(newValue
						+ " "
						+ getResources().getString(
								R.string.prefs_server_time_interval) + ". ");
				return true;
			}
		});

		pref = findPreference(MainActivity.KEY_EXTERNAL_STORAGE);
		pref.setSummary(prefs.getString(MainActivity.KEY_EXTERNAL_STORAGE,
				MainActivity.VAL_EXTERNAL_STORAGE));
		pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				// Set summary with the interval and "seconds"
				if (!((String) newValue).startsWith(File.separator)) {
					newValue = File.separator + (String) newValue;
				}

				preference.setSummary((String) newValue);
				return true;
			}
		});
	}
}