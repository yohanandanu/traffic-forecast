package vn.edu.hcmut.cse.trafficdirection.node;

public class NodeDrawable {

	private double lat;
	private double lon;
	private String speed;
	private String density;

	public NodeDrawable() {
		lat = 0.0;
		lon = 0.0;
		speed = "";
		density = "";
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public String getSpeed() {
		return speed;
	}

	public void setSpeed(String speed) {
		this.speed = speed;
	}

	public String getDensity() {
		return density;
	}

	public void setDensity(String density) {
		this.density = density;
	}

}
