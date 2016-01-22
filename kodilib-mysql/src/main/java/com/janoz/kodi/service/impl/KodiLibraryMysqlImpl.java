package com.janoz.kodi.service.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import com.janoz.tvapilib.model.Art;
import com.janoz.tvapilib.model.impl.Episode;
import com.janoz.tvapilib.model.impl.Season;
import com.janoz.tvapilib.model.impl.Show;
import com.janoz.kodi.database.ArtDao;
import com.janoz.kodi.database.Cache;
import com.janoz.kodi.database.FileDao;
import com.janoz.kodi.database.GenreDao;
import com.janoz.kodi.database.TvShowDao;
import com.janoz.kodi.model.KodiArt;
import com.janoz.kodi.model.KodiArt.MediaType;
import com.janoz.kodi.model.KodiEpisode;
import com.janoz.kodi.model.KodiFile;
import com.janoz.kodi.model.KodiPath;
import com.janoz.kodi.model.KodiSeason;
import com.janoz.kodi.model.KodiTvShow;
import com.janoz.kodi.service.ConnectionFactory;
import com.janoz.kodi.service.KodiLibrary;

public class KodiLibraryMysqlImpl implements KodiLibrary {

	private ConnectionFactory connectionFactory;
	private Cache cache;
	
	private FileDao fileDao;
	private ArtDao artDao;
	private TvShowDao tvShowDao;
	private GenreDao genreDao;
	
	 
	
	public KodiLibraryMysqlImpl(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
		cache = new Cache();
		fileDao = new FileDao(cache);
		artDao = new ArtDao(cache);
		tvShowDao = new TvShowDao(cache);
		genreDao = new GenreDao(cache);
	}
	
	public void invalidateCache() {
		cache.clear();
	}

	@Override
	public boolean hasEpisode(Episode episode) throws SQLException {
		Connection connection = connectionFactory.open();
		try {
			KodiTvShow xShow = tvShowDao.getTvShowByTvDbId(episode.getSeason().getShow().getTheTvDbId(), connection);
			if (xShow == null) return false;
			List<KodiEpisode> xEpisodes = tvShowDao.getSeason(xShow.getId(), episode.getSeason().getSeason(), connection);
			for (KodiEpisode xEpisode : xEpisodes) {
				if (episode.getEpisode() == xEpisode.getEpsiode().intValue()) {
					return true;
				}
			}
			return false;
		} finally {
			connectionFactory.close(connection);
		}
	}
	
	@Override
	public boolean hasShow(Show show) throws SQLException {
		Connection connection = connectionFactory.open();
		try {
			KodiTvShow xShow = tvShowDao.getTvShowByTvDbId(show.getTheTvDbId(), connection);
			return xShow != null;
		} finally {
			connectionFactory.close(connection);
		}
	}

	@Override
	public void addPathToShow(Show show, String path) throws SQLException {
		Connection connection = connectionFactory.open();
		addPathToShow(show, path, connection);
		connectionFactory.close(connection);
	}

	private void addPathToShow(Show show, String path, Connection connection)
			throws SQLException {
		KodiTvShow xShow = tvShowDao.getTvShowByTvDbId(show.getTheTvDbId(), connection);
		KodiPath xPath = fileDao.getOrCreatePath(path, connection);
		fileDao.addPathToTvShow(xShow, xPath, connection);
	}

	@Override
	public void addShow(Show show, String... rootpaths) throws SQLException {
		Connection connection = connectionFactory.open();
		KodiTvShow xShow = findOrAddShow(show, connection, true);
		for (String path : rootpaths) {
			KodiPath xPath = fileDao.getOrCreatePath(path, connection);
			fileDao.addPathToTvShow(xShow, xPath, connection);
		}
		connectionFactory.close(connection);
	}
	
	@Override
	public void addEpisode(Episode episode, String fileLocation) throws SQLException {
		Connection connection = connectionFactory.open();
		addEpisode(episode, fileLocation, connection);
		connectionFactory.close(connection);
	}
	
	private void addEpisode(Episode episode, String fileLocation, Connection connection) throws SQLException {
		Season season = episode.getSeason();
		Show show = season.getShow();

		KodiFile xFile = fileDao.getOrCreateFile(fileLocation, connection);
		if (xFile.getId() != null) {
			throw new SQLException("Duplicated File. File already in library!");
		}
		if (xFile.getPath().getId() == null) {
			fileDao.addPath(xFile.getPath(), connection);
		}
		fileDao.addFile(xFile, connection);

		KodiTvShow xShow = findOrAddShow(show, connection, false);

		KodiSeason xSeason = findOrAddSeason(season, xShow, connection);

		KodiEpisode xEpisode = Transformer.fromEpisode(episode, xSeason, xFile);
		tvShowDao.addEpisode(xEpisode, connection);
		addArt(MediaType.EPISODE, xEpisode.getId(), episode.getArts(), connection);
	}




	/**
	 * Will find the show, or insert it into the library if it isn't found. Will 
	 * also insert genres and art.
	 */
	private KodiTvShow findOrAddShow(Show show, Connection connection, boolean onlyAdd) throws SQLException {
		KodiTvShow result = tvShowDao.getTvShowByTvDbId(show.getTheTvDbId(), connection);
		if (result == null) {
			result = Transformer.fromShow(show);
			tvShowDao.addTvShow(result, connection);
			addArt(MediaType.TVSHOW, result.getId(), show.getArts(), connection);
			genreDao.addGenresToTvShow(result.getId(), result.getGenres(), connection);
		} else if (onlyAdd) {
			throw new SQLException("Show already exists!");
		}
		return result;
	}
	
	/**
	 * Will find the season, or insert it into the library if it isn't found. Will 
	 * also insert art.
	 */
	private KodiSeason findOrAddSeason(Season season, KodiTvShow show, Connection connection) throws SQLException {
		KodiSeason result = null;
		result = tvShowDao.getOnlySeason(show.getId(), season.getSeason(), connection);
		if (result == null) {
			result = new KodiSeason();
			result.setSeason(season.getSeason());
			result.setShow(show);
			tvShowDao.addSeason(result, connection);
			addArt(MediaType.SEASON, result.getId(), season.getArts(), connection);
		}
		return result;
	}
	
	
	
	
	
	
	@Override
	public void addArt(Show show, Set<Art> arts) throws SQLException {
		Connection connection = connectionFactory.open();
		try {
			KodiTvShow xShow = tvShowDao.getTvShowByTvDbId(show.getTheTvDbId(), connection);
			if (xShow == null) {
				throw new SQLException("Show not found.");
			}
			addArt(MediaType.TVSHOW, xShow.getId(), arts, connection);
		} finally {
			connectionFactory.close(connection);
		}
	}

	@Override
	public void addArt(Season season, Set<Art> arts) throws SQLException {
		Connection connection = connectionFactory.open();
		try {
			KodiTvShow xShow = tvShowDao.getTvShowByTvDbId(season.getShow().getTheTvDbId(), connection);
			if (xShow == null) {
				throw new SQLException("Show not found.");
			}
			KodiSeason xSeason = tvShowDao.getOnlySeason(xShow.getId(), season.getSeason(), connection);
			addArt(MediaType.SEASON, xSeason.getId(), arts, connection);
		} finally {
			connectionFactory.close(connection);
		}
	}

	@Override
	public void addArt(Episode episode, Set<Art> arts) throws SQLException {
		Connection connection = connectionFactory.open();
		try {
			KodiTvShow xShow = tvShowDao.getTvShowByTvDbId(episode.getSeason().getShow().getTheTvDbId(), connection);
			if (xShow == null) {
				throw new SQLException("Show not found.");
			}
			List<KodiEpisode> xEpisodes = tvShowDao.getSeason(xShow.getId(), episode.getSeason().getSeason(), connection);
			for (KodiEpisode xEpisode : xEpisodes) {
				if (xEpisode.getEpsiode().equals(episode.getEpisode())) {
					addArt(MediaType.EPISODE, xEpisode.getId(), arts, connection);
					return;
				}
				throw new SQLException("Episode not found.");
			}
		} finally {
			connectionFactory.close(connection);
		}
	}

	private void addArt(MediaType type, int id, Set<Art> arts, Connection connection) throws SQLException {
		for (Art art : arts) {
			KodiArt xArt = Transformer.fromArt(art, type, id);
			artDao.addArt(xArt, connection);
		}
		
	}

}
