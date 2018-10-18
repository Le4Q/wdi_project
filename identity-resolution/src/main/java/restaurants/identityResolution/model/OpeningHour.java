package restaurants.identityResolution.model;

import java.text.DateFormat;
import java.time.LocalTime;

public class OpeningHour {
	public String day;
	public LocalTime opens;
	public LocalTime closes;
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public LocalTime getOpens() {
		return opens;
	}
	public void setOpens(LocalTime opens) {
		this.opens = opens;
	}
	public LocalTime getCloses() {
		return closes;
	}
	public void setCloses(LocalTime closes) {
		this.closes = closes;
	}
}
