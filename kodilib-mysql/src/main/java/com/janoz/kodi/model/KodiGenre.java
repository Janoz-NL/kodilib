package com.janoz.kodi.model;

public enum KodiGenre {

	DRAMA("Drama"),
	ANIMATION("Animation"),
	ADVENTURE("Adventure"),
	FAMILY("Family"),
	FANTASY("Fantasy"),
	MUSICAL("Musical"),
	ROMANCE("Romance"),
	CHILDREN("Children"),
	ACTION("Action"),
	SCIFI("Sci-Fi", "Science-Fiction", "Science Fiction"),
	COMEDY("Comedy"),
	THRILLER("Thriller"),
	CRIME("Crime"),
	MUSIC("Music"),
	HISTORY("History"),
	BIOGRAPHY("Biography"),
	MYSTERY("Mystery"),
	MINISERIES("Mini-Series"),
	DOCUMENTARY("Documentary"),
	SHORT("Short"),
	WAR("War"),
	HORROR("Horror"),
	SPORT("Sport");

	private String persistanceValue;
	private String[] synonyms;
	
	private Integer id;

	KodiGenre(String persistanceValue, String... synonyms) {
		this.persistanceValue = persistanceValue;
		this.synonyms = synonyms;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getPersistanceValue() {
		return persistanceValue;
	}
	public String[] getSynonyms() {
		return synonyms;
	}
	
	public static KodiGenre getByName(String name) {
		return getByName(name, false);
	}

	public static KodiGenre getByNameOrSynonym(String name) {
		return getByName(name, true);
	}

	private  static KodiGenre getByName(String name, boolean alsoSynonym) {
		for (KodiGenre genre : values()) {
			if (genre.persistanceValue.equals(name)) {
				return genre;
			} if (alsoSynonym) {
				for (String synonym : genre.synonyms) {
					if (synonym.equals(name)) {
						return genre;
					}
				}
			}
		}
		return null;
	}
}
