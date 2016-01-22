package com.janoz.kodi.model;

import java.util.HashSet;
import java.util.Set;

import org.joda.time.LocalDate;

public class KodiTvShow {

	private Integer id;
	private String title;
	private String plot;
	private Double rating;
	private LocalDate firstAired;
	private Integer tvdbId;
	private String contentRating;
	private String network;
	private Set<KodiGenre> genres = new HashSet<KodiGenre>();
	private Set<KodiPath> paths = new HashSet<KodiPath>();
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
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
	public Integer getTvdbId() {
		return tvdbId;
	}
	public void setTvdbId(Integer tvdbId) {
		this.tvdbId = tvdbId;
	}
	public String getContentRating() {
		return contentRating;
	}
	public void setContentRating(String contentRating) {
		this.contentRating = contentRating;
	}
	public String getNetwork() {
		return network;
	}
	public void setNetwork(String network) {
		this.network = network;
	}
	public Set<KodiGenre> getGenres() {
		return genres;
	}
	public void setGenres(Set<KodiGenre> genres) {
		this.genres = genres;
	}
	public Set<KodiPath> getPaths() {
		return paths;
	}
	public void setPaths(Set<KodiPath> paths) {
		this.paths = paths;
	}
}
