package vn.edu.hcmut.cse.trafficdirection.overlay;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

import vn.edu.hcmut.cse.trafficdirection.main.MainActivity;
import vn.edu.hcmut.cse.trafficdirection.main.R;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.location.Location;

public class MyPositionOverlay extends Overlay{
	private MainActivity  whereAmI;
	/** Get the position location */
	public Location getLocation() {
		return location;
	}

	/** Set the position location */
	public void setLocation(Location location) {
		this.location = location;
	}

	Location location;

	public void GPXOverlay(MainActivity whereAmI){
		this.whereAmI = whereAmI;
	}

	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		Projection projection = mapView.getProjection();
		Double latitude=null;
		Double longitude=null;
		if (shadow == false) {
			if( location == null){
				
				latitude = 10.770579 * 1E6;
				longitude = 106.657963 * 1E6;
			}
			else
			{
				latitude = location.getLatitude() * 1E6;
				longitude = location.getLongitude() * 1E6;
			}
			GeoPoint geoPoint = new GeoPoint(latitude.intValue(),
					longitude.intValue());
			Point point = new Point();
			projection.toPixels(geoPoint, point);
			final float scale = whereAmI.getResources().getDisplayMetrics().density;
			int dip = (int) (8 * scale);
			Bitmap bmp1 = BitmapFactory.decodeResource(whereAmI.getResources(),
					R.drawable.startpoint);
			canvas.drawBitmap(bmp1, point.x - dip, point.y - dip, null);
		}
		super.draw(canvas, mapView, shadow);
	}

	public boolean onTap(GeoPoint point, MapView mapView) {
		return false;
	}
}