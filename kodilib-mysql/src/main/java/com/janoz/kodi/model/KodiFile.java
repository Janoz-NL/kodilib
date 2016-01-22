package com.janoz.kodi.model;

import org.joda.time.LocalDateTime;

public class KodiFile {

	private Integer id;
	private KodiPath path;
	private String filename;
	private LocalDateTime addDate;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public KodiPath getPath() {
		return path;
	}
	public void setPath(KodiPath path) {
		this.path = path;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public LocalDateTime getAddDate() {
		return addDate;
	}
	public void setAddDate(LocalDateTime addDate) {
		this.addDate = addDate;
	}

	
	public String getFullPath() {
		return path.getPath() + filename;
	}
}
