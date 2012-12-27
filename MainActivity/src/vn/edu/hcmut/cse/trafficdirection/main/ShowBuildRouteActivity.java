package vn.edu.hcmut.cse.trafficdirection.main;

import java.util.ArrayList;
import java.util.List;
import vn.edu.hcmut.cse.trafficdirection.node.NodeGPS;
import vn.edu.hcmut.cse.trafficdirection.overlay.GPXOverlay;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class ShowBuildRouteActivity extends MapActivity{
	private MapView m_MapView = null;
	private String Point1 = null;
	private String Point2 = null;
	public ArrayList<NodeGPS> data;
	
	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_build_route);
		
		data = new ArrayList<NodeGPS>();
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			Point1 = extras.getString("POINT1");
			Point2 = extras.getString("POINT2");
			
			double lat = Double.parseDouble(Point1.split(",")[0]) / 1E6;
			double lon = Double.parseDouble(Point1.split(",")[1]) / 1E6;
			NodeGPS p1 = new NodeGPS(lat, lon);
			data.add(p1);
			
			lat = Double.parseDouble(Point2.split(",")[0]) / 1E6;
			lon = Double.parseDouble(Point2.split(",")[1]) / 1E6;
			NodeGPS p2 = new NodeGPS(lat, lon);
			data.add(p2);
		}
		
		
		
		m_MapView = (MapView) findViewById(R.id.mapViewBuildRoute);
		LinearLayout zoomLayout = (LinearLayout) findViewById(R.id.layout_MainMenuBuildRoute);
		zoomLayout.addView(m_MapView.getZoomControls(),
				new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT));
		
		if(Point1 != null && Point2 != null)
		{
			showGPX();
		}
	}
	
	private void showGPX() {

		GPXOverlay mapOverlay = new GPXOverlay(this, m_MapView, data);
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
