package com.janoz.kodi.model;

public class KodiArt {

	private Integer id;
	private int mediaId;
	private MediaType mediaType;
	private Type type;
	private String url;	
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getMediaId() {
		return mediaId;
	}

	public void setMediaId(int mediaId) {
		this.mediaId = mediaId;
	}

	public MediaType getMediaType() {
		return mediaType;
	}

	public void setMediaType(MediaType mediaType) {
		this.mediaType = mediaType;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public static enum MediaType {
		MOVIE("movie"),
		ACTOR("actor"),
		EPISODE("episode"),
		SET("set"),
		MUSICVIDEO("musicvideo"),
		TVSHOW("tvshow"),
		SEASON("season"),
		GENRE("genre");
		
		private String persistanceValue;
		private MediaType(String persistanceValue) {
			this.persistanceValue = persistanceValue;
		}
		
		
		public String getPersistanceValue() {
			return persistanceValue;
		}
	}
	
	public static enum Type {
		FANART("fanart"),
		THUMB("thumb"),
		POSTER("poster"),
		BANNER("banner"),
		CLEARART("clearart"),
		CLEARLOGO("clearlogo"),
		LANDSCAPE("landscape"),
		CHARACTERART("characterart"),
		DISCART("discart");
		
		private String persistanceValue;
		private Type(String persistanceValue) {
			this.persistanceValue = persistanceValue;
		}
		
		public String getPersistanceValue() {
			return persistanceValue;
		}
	}
}
