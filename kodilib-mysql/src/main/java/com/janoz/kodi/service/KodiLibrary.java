package com.janoz.kodi.service;

import java.sql.SQLException;
import java.util.Set;

import com.janoz.tvapilib.model.Art;
import com.janoz.tvapilib.model.impl.Episode;
import com.janoz.tvapilib.model.impl.Season;
import com.janoz.tvapilib.model.impl.Show;

public interface KodiLibrary {


	/**
	 * @param episode episode to search for. Only episodeNr, seasonNr and 
	 * (show)tvdbId is used
	 * @return true when this episode is in the library.
	 * @throws SQLException In case of a problem communicating with the database.
	 */
	boolean hasEpisode(Episode episode) throws SQLException;
	
	/**
	 * Adds an episode to the library.
	 * 
	 * Will also add show and season if not available. Make sure the art is filled 
	 * correctly because those will also be added in case of a new show or season.
	 * 
	 * @param episode The episode to add.
	 * @param fileLocation Location of the video file of this episode in XBMC style.
	 * @throws SQLException In case of a problem communicating with the database.
	 */
	void addEpisode(Episode episode, String fileLocation) throws SQLException;
	
	/**
	 * @param show show to search for. Only tvdbId is used
	 * @return true when this show is in the library.
	 * @throws SQLException In case of a problem communicating with the database.
	 */
	boolean hasShow(Show show) throws SQLException;
	
	
	/**
	 * Adds a tvshow to the library.
	 * 
	 * Make sure the art is filled correctly because those will also be added.
	 * 
	 * @param show Show to add
	 * @param rootpaths rootpaths for episodes. A show needs at least a rootpath otherwise
	 * episodes won't show in kodi
	 * @throws SQLException In case of a problem communicating with the database.
	 */
	void addShow(Show show, String... rootpaths) throws SQLException;
	
	/**
	 * A show needs at least one rootpath for the episodes to work in the library, althoug
	 * it isn't necessary to put every episode under this location. 
	 * 
	 * @param show show to add the path to (only tvdbid is used)
	 * @param path kodi style path
	 * @throws SQLException In case of a problem communicating with the database.
	 */
	void addPathToShow(Show show, String path) throws SQLException;
	
	
	/* ART */
	
	void addArt(Show show, Set<Art> arts) throws SQLException;
	void addArt(Season season, Set<Art> arts) throws SQLException;
	void addArt(Episode episode, Set<Art> arts) throws SQLException;
}
