package com.marvel.busbook;;


public class BusInfo implements Comparable<BusInfo>{
	public BusInfo(String num, String startPlace, String endPlace) {
		super();
		this.num = num;
		this.startPlace = startPlace;
		this.endPlace = endPlace;
	}
	private String num;
	private String startPlace;
	private String endPlace;
	private String time;
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String[] getStops() {
		return stops;
	}
	public void setStops(String[] stops) {
		this.stops = stops;
	}
	private String[] stops;
	
	public String getNum() {
		return num;
	}
	public void setNum(String num) {
		this.num = num;
	}
	public String getStartPlace() {
		return startPlace;
	}
	public void setStartPlace(String startPlace) {
		this.startPlace = startPlace;
	}
	public String getEndPlace() {
		return endPlace;
	}
	public void setEndPlace(String endPlace) {
		this.endPlace = endPlace;
	}
	
	public int compareTo(BusInfo info) {
		  int flag=this.getNum().compareTo(info.getNum());
		  return flag;
	}

}
