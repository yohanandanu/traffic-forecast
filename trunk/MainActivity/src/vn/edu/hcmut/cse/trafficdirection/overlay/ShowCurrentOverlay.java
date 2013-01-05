package vn.edu.hcmut.cse.trafficdirection.overlay;

import java.util.ArrayList;

import vn.edu.hcmut.cse.trafficdirection.database.DatabaseHelper;
import vn.edu.hcmut.cse.trafficdirection.main.MainActivity;
import vn.edu.hcmut.cse.trafficdirection.main.ShowCurrentActivity;
import vn.edu.hcmut.cse.trafficdirection.node.DrawableStreet;
import vn.edu.hcmut.cse.trafficdirection.node.NodeDrawable;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class ShowCurrentOverlay extends Overlay {

	private int m_iOverlayType = MainActivity.OVERLAY_V;
	MapView mapView;
	DatabaseHelper md;
	private Projection project;
	ShowCurrentActivity main;

	public ShowCurrentOverlay(MapView mapView, int m_iOverlayType,
			DatabaseHelper md, ShowCurrentActivity mainActivity) {
		this.m_iOverlayType = m_iOverlayType;
		this.mapView = mapView;
		this.md = md;
		project = mapView.getProjection();
		this.main = mainActivity;
	}

	public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
			long when) {
		long start = System.currentTimeMillis();

		if (mapView.getZoomLevel() < 14) {
			return super.draw(canvas, mapView, shadow, when);
		}
		ArrayList<DrawableStreet> streetList = main.streetList;
		if (main.streetList.size() != 0)
			streetList = main.streetList;
		else if (main.streetList2.size() != 0)
			streetList = main.streetList2;

		if (m_iOverlayType == MainActivity.OVERLAY_V) {

			int step = 21 - mapView.getZoomLevel();

			for (int i = 0; i < streetList.size(); i++) {
				if (mapView.getZoomLevel() > 17) {
					if (!streetList.get(i).getType().equals("0x5")
							&& !streetList.get(i).getType().equals("0x6"))
						continue;
				} else {
					if (!streetList.get(i).getType().equals("0x5"))// &&
																	// !main.streetList.get(i).getType().equals("0x6"))
						continue;
				}

				ArrayList<NodeDrawable> notes = streetList.get(i).m_nodeArray;
				if (notes.size() <= 1)
					continue;
				NodeDrawable pre = null;
				NodeDrawable cur = null;
				for (int j = 0; j < notes.size();) {
					cur = notes.get(j);
					if (pre != null && cur != null) {
						if (pre == cur)
							break;

						double speed = (Double.parseDouble(pre.getSpeed()) + Double
								.parseDouble(cur.getSpeed())) / 2.0;
						Paint mPaint = new Paint();

						mPaint.setAntiAlias(true);
						mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
						mPaint.setStrokeJoin(Paint.Join.ROUND);
						mPaint.setStrokeCap(Paint.Cap.ROUND);
						int stroke = 6;
						stroke = (int) (stroke * Math.pow(1.5,
								mapView.getZoomLevel() - 17));
						mPaint.setStrokeWidth(stroke);

						GeoPoint gp1 = new GeoPoint((int) (pre.getLat() * 1E6),
								(int) (pre.getLon() * 1E6));
						GeoPoint gp2 = new GeoPoint((int) (cur.getLat() * 1E6),
								(int) (cur.getLon() * 1E6));

						Point p1 = new Point();
						Point p2 = new Point();
						Path path = new Path();

						project.toPixels(gp1, p1);
						project.toPixels(gp2, p2);

						int redColor = (int) ((1.0 - speed / 40.0) * 255);
						int greenColor = (int) ((speed / 40.0) * 255);

						mPaint.setColor(Color
								.argb(100, redColor, greenColor, 0));

						path.moveTo(p2.x, p2.y);
						path.lineTo(p1.x, p1.y);
						canvas.drawPath(path, mPaint);
					}
					pre = cur;
					j += step;
					if (j >= notes.size())
						j = notes.size() - 1;
				}
			}
		} else {
		}

		Log.d("Time to draw", "" + (System.currentTimeMillis() - start));
		return super.draw(canvas, mapView, shadow, when);
	}

}
