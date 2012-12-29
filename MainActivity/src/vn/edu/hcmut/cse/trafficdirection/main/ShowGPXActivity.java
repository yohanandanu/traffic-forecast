package vn.edu.hcmut.cse.trafficdirection.main;

import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import vn.edu.hcmut.cse.trafficdirection.overlay.GPXOverlay;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class ShowGPXActivity extends MapActivity {
	private MapView m_MapView = null;
	private String m_file = null;

	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_gpx);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			m_file = extras.getString("FILE");
		}

		m_MapView = (MapView) findViewById(R.id.mapViewShowGPX);
		if (m_MapView == null) {
			Log.d("Null", "m_MapView");
		}
		LinearLayout zoomLayout = (LinearLayout) findViewById(R.id.layout_MainMenuShowGPX);
		zoomLayout.addView(m_MapView.getZoomControls(),
				new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT));

		if (m_file != null) {
			showGPX(m_file);
		}
	}

	private void showGPX(String file) {
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
