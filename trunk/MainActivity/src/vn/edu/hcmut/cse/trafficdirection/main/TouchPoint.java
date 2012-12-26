package vn.edu.hcmut.cse.trafficdirection.main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class TouchPoint extends MapActivity {
	private MapView mapview;
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

		mapview.invalidate();

	}

	class MapOverlay extends Overlay {

		@Override
		public boolean onTouchEvent(MotionEvent event, MapView mapView) {
			// ---when user lifts his finger---
			if (event.getAction() == 1) {
				GeoPoint p = mapView.getProjection().fromPixels(
						(int) event.getX(), (int) event.getY());
				
				Intent it = new Intent();
				
				try {
					FileWriter fstream = new FileWriter(Environment.getExternalStorageDirectory().getAbsolutePath() + MainActivity.PATH_TO_TMP_FILE, true);
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
			return false;
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
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