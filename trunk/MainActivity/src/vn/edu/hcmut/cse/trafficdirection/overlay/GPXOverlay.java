package vn.edu.hcmut.cse.trafficdirection.overlay;

import java.util.ArrayList;

import vn.edu.hcmut.cse.trafficdirection.main.R;
import vn.edu.hcmut.cse.trafficdirection.node.NodeGPS;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class GPXOverlay extends Overlay {

	private Projection project;
	private ArrayList<NodeGPS> data;
	private MapView mapView;
	private MapActivity activity;

	public GPXOverlay(MapActivity mainActivity, MapView m_MapView,
			ArrayList<NodeGPS> data) {
		// TODO Auto-generated constructor stub
		activity = mainActivity;
		this.data = data;
		this.mapView = m_MapView;

		double startLat = data.get(0).lat;
		double startLon = data.get(0).lon;

		GeoPoint startPoint = new GeoPoint((int) (startLat * 1E6),
				(int) (startLon * 1E6));

		mapView.getController().animateTo(startPoint);
		mapView.getController().setZoom(17);

		project = mapView.getProjection();
	}

	public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
			long when) {

		NodeGPS pre = null;
		NodeGPS cur = null;

		int step = 21 - mapView.getZoomLevel();
		int n = data.size();

		for (int i = 0; i < n;) {
			cur = data.get(i);
			if (pre != null && cur != null) {
				if (pre == cur)
					break;

				double speed = (pre.speed + cur.speed) / 2.0;

				Paint mPaint = new Paint();

				mPaint.setAntiAlias(true);
				mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
				mPaint.setStrokeJoin(Paint.Join.ROUND);
				mPaint.setStrokeCap(Paint.Cap.ROUND);
				mPaint.setStrokeWidth(6);

				GeoPoint gp1 = new GeoPoint((int) (pre.lat * 1E6),
						(int) (pre.lon * 1E6));
				GeoPoint gp2 = new GeoPoint((int) (cur.lat * 1E6),
						(int) (cur.lon * 1E6));

				Point p1 = new Point();
				Point p2 = new Point();
				Path path = new Path();

				project.toPixels(gp1, p1);
				project.toPixels(gp2, p2);

				speed *= 3.6;

				int redColor = (int) ((1.0 - speed / 30.0) * 255);
				int greenColor = (int) ((speed / 30.0) * 255);

				mPaint.setColor(Color.argb(100, redColor, greenColor, 0));

				path.moveTo(p2.x, p2.y);
				path.lineTo(p1.x, p1.y);
				canvas.drawPath(path, mPaint);
			}
			pre = cur;

			i += step;
			if (i >= n)
				i = n - 1;
		}
		final float scale = activity.getResources().getDisplayMetrics().density;
		int dip = (int) (8 * scale);
		cur = data.get(0);
		Bitmap bmp1 = BitmapFactory.decodeResource(activity.getResources(),
				R.drawable.startpoint);

		Point screenPts1 = new Point();
		GeoPoint p1 = new GeoPoint((int) (cur.lat * 1E6), (int) (cur.lon * 1E6));
		mapView.getProjection().toPixels(p1, screenPts1);
		canvas.drawBitmap(bmp1, screenPts1.x - dip, screenPts1.y - dip, null);

		Bitmap bmp2 = BitmapFactory.decodeResource(activity.getResources(),
				R.drawable.endpoint);
		cur = data.get(n - 1);
		Point screenPts2 = new Point();
		GeoPoint p2 = new GeoPoint((int) (cur.lat * 1E6), (int) (cur.lon * 1E6));
		mapView.getProjection().toPixels(p2, screenPts2);
		canvas.drawBitmap(bmp2, screenPts2.x - dip, screenPts2.y - dip, null);

		return super.draw(canvas, mapView, shadow, when);

	}

}
