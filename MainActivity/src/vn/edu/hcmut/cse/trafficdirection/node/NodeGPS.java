package vn.edu.hcmut.cse.trafficdirection.node;

public class NodeGPS {

	public double lat;
	public double lon;
	public float ele;
	public String time;
	public double speed;
	public float accuracy;
	public double course;

	public NodeGPS(String lat, String lon) {
		// TODO Auto-generated constructor stub
		this.lat = Double.parseDouble(lat);
		this.lon = Double.parseDouble(lon);
		speed = 0.0;
	}

	public void setEle(String ele) {
		// TODO Auto-generated method stub
		this.ele = Float.parseFloat(ele);

	}

	public void setSpeed(String speed) {
		// TODO Auto-generated method stub
		this.speed = Double.parseDouble(speed);
	}

	public void setTime(String time) {
		// TODO Auto-generated method stub
		this.time = time;
	}

}
