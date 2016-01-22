package com.janoz.kodi.model;

import org.joda.time.LocalDate;

public class KodiEpisode {

	private Integer id;
	private KodiFile file;
	private String title;
	private String plot;
	private Double rating;
	private LocalDate firstAired;
	private Integer length;
	private KodiSeason season;
	private Integer epsiode;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public KodiFile getFile() {
		return file;
	}
	public void setFile(KodiFile file) {
		this.file = file;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPlot() {
		return plot;
	}
	public void setPlot(String plot) {
		this.plot = plot;
	}
	public Double getRating() {
		return rating;
	}
	public void setRating(Double rating) {
		this.rating = rating;
	}
	public LocalDate getFirstAired() {
		return firstAired;
	}
	public void setFirstAired(LocalDate firstAired) {
		this.firstAired = firstAired;
	}
	public Integer getLength() {
		return length;
	}
	public void setLength(Integer length) {
		this.length = length;
	}
	public KodiSeason getSeason() {
		return season;
	}
	public void setSeason(KodiSeason season) {
		this.season = season;
	}
	public Integer getEpsiode() {
		return epsiode;
	}
	public void setEpsiode(Integer epsiode) {
		this.epsiode = epsiode;
	}
}
