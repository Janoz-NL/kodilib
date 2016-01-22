package com.janoz.kodi.model;

public class KodiSeason {

	private Integer id;
	private KodiTvShow show;
	private int season;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public KodiTvShow getShow() {
		return show;
	}
	public void setShow(KodiTvShow show) {
		this.show = show;
	}
	public int getSeason() {
		return season;
	}
	public void setSeason(int season) {
		this.season = season;
	}
	
	
}
