package com.janoz.kodi.database;

import java.util.HashMap;
import java.util.Map;

import com.janoz.kodi.model.KodiArt;
import com.janoz.kodi.model.KodiEpisode;
import com.janoz.kodi.model.KodiFile;
import com.janoz.kodi.model.KodiPath;
import com.janoz.kodi.model.KodiSeason;
import com.janoz.kodi.model.KodiTvShow;

public class Cache {

	
	private Map<Integer, KodiTvShow> tvShows = new HashMap<Integer, KodiTvShow>();
	private Map<Integer, KodiPath> paths = new HashMap<Integer, KodiPath>();
	private Map<Integer, KodiFile> files = new HashMap<Integer, KodiFile>();
	private Map<Integer, KodiSeason> seasons = new HashMap<Integer, KodiSeason>();
	private Map<Integer, KodiEpisode> episodes = new HashMap<Integer, KodiEpisode>();
	private Map<Integer, KodiArt> arts = new HashMap<Integer, KodiArt>();

	KodiPath getPath(int id) {
		return paths.get(id);
	}
	
	void put(KodiPath path) {
		paths.put(path.getId(), path);
	}
	
	KodiTvShow getTvShow(int id) {
		return tvShows.get(id);
	}
	
	void put(KodiTvShow tvShow) {
		tvShows.put(tvShow.getId(), tvShow);
	}
	
	KodiFile getFile(int id) {
		return files.get(id);
	}
	
	void put(KodiFile file) {
		files.put(file.getId(), file);
	}


	public KodiSeason getSeason(int id) {
		return seasons.get(id);
	}
	
	public void put(KodiSeason season) {
		seasons.put(season.getId(), season);
	}
	
	public KodiEpisode getEpisode(int id) {
		return episodes.get(id);
	}

	public void put(KodiEpisode episode) {
		episodes.put(episode.getId(), episode);
	}
	
	public KodiArt getArt(int id) {
		return arts.get(id);
	}
	
	public void put(KodiArt art) {
		arts.put(art.getId(), art);
	}
	
	public void clear(){
		tvShows.clear();
		paths.clear();
		files.clear();
		seasons.clear();
		episodes.clear();
		arts.clear();
	}
}
