package vn.edu.hcmut.cse.trafficdirection.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class ForecastActivity extends MapActivity {
	private MapView m_MapView = null;
	private ArrayList<GeoPoint> traffic_jam = new ArrayList<GeoPoint>();
	private ArrayList<GeoPoint> traffic_jam_draw = new ArrayList<GeoPoint>();

	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_gpx);
		
		traffic_jam.clear();
		traffic_jam.add(new GeoPoint(10770450, 106658156));
		traffic_jam.add(new GeoPoint(10777786, 106656140));
		traffic_jam.add(new GeoPoint(10768764,106652495));
		traffic_jam.add(new GeoPoint(10768764,106652495));
		traffic_jam.add(new GeoPoint(10776373,106663535));
		traffic_jam.add(new GeoPoint(10767636,106667161));
		traffic_jam.add(new GeoPoint(10767963,106667944));
		traffic_jam.add(new GeoPoint(10763789,106660016));
		traffic_jam.add(new GeoPoint(10762134,106656958));
		traffic_jam.add(new GeoPoint(10759784,106668910));
		traffic_jam.add(new GeoPoint(10767604,106674467));
		traffic_jam.add(new GeoPoint(10782866,106672128));
		traffic_jam.add(new GeoPoint(10790633,106652462));
		traffic_jam.add(new GeoPoint(10792815,106653407));
		traffic_jam.add(new GeoPoint(10794269,106650574));
		traffic_jam.add(new GeoPoint(10796050,106647270));
		traffic_jam.add(new GeoPoint(10798400,106642903));
		traffic_jam.add(new GeoPoint(10771936,106657709));
		traffic_jam.add(new GeoPoint(10772621,106660724));
		traffic_jam.add(new GeoPoint(10773791,106661603));
		traffic_jam.add(new GeoPoint(10767973,106658878));
		traffic_jam.add(new GeoPoint(10773335,106689370));
		traffic_jam.add(new GeoPoint(10776496,106683791));
		traffic_jam.add(new GeoPoint(10759295,106651776));
		traffic_jam.add(new GeoPoint(10760307,106653664));
		traffic_jam.add(new GeoPoint(10752992,106633086));
		traffic_jam.add(new GeoPoint(10753983,106634674));
		traffic_jam.add(new GeoPoint(10754573,106637034));
		traffic_jam.add(new GeoPoint(10754531,106638386));
		traffic_jam.add(new GeoPoint(10754426,106639781));
		traffic_jam.add(new GeoPoint(10754320,106640811));
		traffic_jam.add(new GeoPoint(10754447,106642957));
		traffic_jam.add(new GeoPoint(10756449,106652398));
		traffic_jam.add(new GeoPoint(10757314,106656539));
		traffic_jam.add(new GeoPoint(10758431,106661475));
		traffic_jam.add(new GeoPoint(10760771,106674371));
		traffic_jam.add(new GeoPoint(10763216,106668127));
		traffic_jam.add(new GeoPoint(10764629,106672246));
		traffic_jam.add(new GeoPoint(10767727,106671324));
		traffic_jam.add(new GeoPoint(10769456,106670659));
		traffic_jam.add(new GeoPoint(10771627,106665401));
		traffic_jam.add(new GeoPoint(10773124,106664844));
		traffic_jam.add(new GeoPoint(10762331,106686516));
		traffic_jam.add(new GeoPoint(10762289,106676452));
		traffic_jam.add(new GeoPoint(10765282,106681516));
		traffic_jam.add(new GeoPoint(10761024,106683383));
		traffic_jam.add(new GeoPoint(10759337,106684155));
		traffic_jam.add(new GeoPoint(10754636,106679049));
		traffic_jam.add(new GeoPoint(10752549,106668148));
		traffic_jam.add(new GeoPoint(10752571,106669564));
		traffic_jam.add(new GeoPoint(10754384,106669436));
		traffic_jam.add(new GeoPoint(10757419,106674371));
		traffic_jam.add(new GeoPoint(10757482,106669307));
		traffic_jam.add(new GeoPoint(10762183,106657162));
		traffic_jam.add(new GeoPoint(10781745,106636133));
		traffic_jam.add(new GeoPoint(10785771,106641798));
		traffic_jam.add(new GeoPoint(10774726,106648149));
		traffic_jam.add(new GeoPoint(10766737,106642356));
		traffic_jam.add(new GeoPoint(10765303,106646605));
		traffic_jam.add(new GeoPoint(10758305,106643922));
		traffic_jam.add(new GeoPoint(10757925,106649287));
		traffic_jam.add(new GeoPoint(10753203,106650982));
		traffic_jam.add(new GeoPoint(10755353,106662505));
		traffic_jam.add(new GeoPoint(10756112,106666346));
		traffic_jam.add(new GeoPoint(10759801,106668921));
		traffic_jam.add(new GeoPoint(10767580,106667140));
		traffic_jam.add(new GeoPoint(10768065,106667976));
		traffic_jam.add(new GeoPoint(10767748,106674349));
		traffic_jam.add(new GeoPoint(10770910,106672933));
		traffic_jam.add(new GeoPoint(10777677,106681602));
		traffic_jam.add(new GeoPoint(10763828,106655853));
		traffic_jam.add(new GeoPoint(10765198,106654909));
		traffic_jam.add(new GeoPoint(10768613,106652527));
		traffic_jam.add(new GeoPoint(10769372,106651561));
		traffic_jam.add(new GeoPoint(10767833,106650574));
		traffic_jam.add(new GeoPoint(10771100,106652699));
		traffic_jam.add(new GeoPoint(10775400,106653192));
		
		
		m_MapView = (MapView) findViewById(R.id.mapViewShowGPX);

		LinearLayout zoomLayout = (LinearLayout) findViewById(R.id.layout_MainMenuShowGPX);
		zoomLayout.addView(m_MapView.getZoomControls(),
				new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT));

		showForecase();
	}

	private void showForecase() {
		// TODO Auto-generated method stub
		ForecastOverlay mapOverlay = new ForecastOverlay();
		List<Overlay> listOfOverlay = m_MapView.getOverlays();
		listOfOverlay.clear();
		listOfOverlay.add(mapOverlay);

		m_MapView.invalidate();
	}

	class ForecastOverlay extends Overlay {
		private int play_frame;
		private final int n_frame = 20;
		private final long DELAY = 80;//24 frame/s
		private final float MAX_RADIUS = (float) 30.0;
		private long last_time;
		private long timer;
		private Random r;
		private Projection project;
		
		public ForecastOverlay()
		{
			play_frame = 0;
			last_time = 0;
			project = m_MapView.getProjection();
			timer = System.currentTimeMillis();
			r = new Random();
			
			traffic_jam_draw.clear();
			timer = System.currentTimeMillis();
			for(int i = 0; i < traffic_jam.size() ; i++)
			{
				if(r.nextDouble() >= 0.5)
					traffic_jam_draw.add(traffic_jam.get(i));
			}
		}
		public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
				long when) {
			int size = traffic_jam.size();
			
			if(System.currentTimeMillis() - timer >= 60000)
			{
				traffic_jam_draw.clear();
				timer = System.currentTimeMillis();
				for(int i = 0; i < size ; i++)
				{
					if(r.nextDouble() >= 0.5)
						traffic_jam_draw.add(traffic_jam.get(i));
				}
				Log.d("Forecast", traffic_jam_draw.size() + "");
			}
			
			
			for(int i = 0; i < traffic_jam_draw.size() ; i++)
			{
				GeoPoint cur = traffic_jam_draw.get(i);
				
				Paint mPaint = new Paint();

				mPaint.setAntiAlias(true);
				mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
				mPaint.setStrokeJoin(Paint.Join.ROUND);
				mPaint.setStrokeCap(Paint.Cap.ROUND);
				mPaint.setStrokeWidth(6);
				
				
				
				if(play_frame >= n_frame)
				{
					play_frame = 0;
				}
					
				Point p = new Point();
				project.toPixels(cur, p);
				
				long time = (System.currentTimeMillis() - last_time);
				mPaint.setColor(Color.argb((int)(255 * (1.0 - play_frame / MAX_RADIUS)), 255, 0, 0));
				if(time >= DELAY)
				{
					last_time = System.currentTimeMillis();
					play_frame++;
					canvas.drawCircle(p.x, p.y, play_frame * (play_frame / MAX_RADIUS), mPaint);
					m_MapView.postInvalidate();
				}
				else
				{
					canvas.drawCircle(p.x, p.y, play_frame * (play_frame / MAX_RADIUS), mPaint);
					m_MapView.postInvalidate();
				}

					
				
			}
			return super.draw(canvas, mapView, shadow, when);
		}
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
