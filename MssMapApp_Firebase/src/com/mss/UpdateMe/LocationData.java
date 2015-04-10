package com.mss.UpdateMe;

/**
 * @author greg
 * @since 6/21/13
 */
public class LocationData {

	private String Lat;
	private String Lon;
	private String Time;

	LocationData(String lat, String lon, String time) {
		this.Lat = lat;
		this.Lon = lon;
		this.Time = time;
	}

	public String getLat() {
		return Lat;
	}

	public String getLong() {
		return Lon;
	}

	public String getTime() {
		return Time;
	}
}
