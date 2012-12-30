package vn.edu.hcmut.cse.trafficdirection.overlay;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

import vn.edu.hcmut.cse.trafficdirection.main.MainActivity;
import vn.edu.hcmut.cse.trafficdirection.main.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.location.Location;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

public class MyPositionOverlay extends Overlay{
	private MainActivity  mainActivity;
	private boolean isDown = false;
	private long time = 0;
	/** Get the position location */
	public Location getLocation() {
		return location;
	}

	/** Set the position location */
	public void setLocation(Location location) {
		this.location = location;
	}

	Location location;

	public void GPXOverlay(MainActivity mainActivity){
		this.mainActivity = mainActivity;
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
	
	public boolean onTouchEvent(MotionEvent event, MapView mapView) {
		// ---when user lifts his finger---
		if(System.currentTimeMillis() - time > 2000 && isDown)
		{
			
			final Dialog overlayDialog = new Dialog(mainActivity);
			overlayDialog.setTitle("Choose your layout");
            overlayDialog.setItems(R.array.select_dialog_items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    /* User clicked so do some stuff */
                    String[] items = mainActivity.getResources().getStringArray(R.array.select_dialog_items);
                    new AlertDialog.Builder(mainActivity)
                            .setMessage("You selected: " + which + " , " + items[which])
                            .show();
                }
            })
            .create();
			
			
			
			isDown = false;
		}
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			isDown = true;
			time = System.currentTimeMillis();
		}
		else if(event.getAction() == MotionEvent.ACTION_UP)
		{
			isDown = false;
		}
		return false;
	}
}