package com.janoz.kodi.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.janoz.kodi.model.KodiArt;
import com.janoz.kodi.model.KodiArt.MediaType;
import com.janoz.kodi.model.KodiArt.Type;
import com.janoz.kodi.model.KodiEpisode;
import com.janoz.kodi.model.KodiFile;
import com.janoz.kodi.model.KodiPath;
import com.janoz.kodi.model.KodiSeason;
import com.janoz.kodi.model.KodiTvShow;

public abstract class AbstractDao {

	protected static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd");
	protected static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
	
	private Cache cache;

	public AbstractDao(Cache cache) {
		this.cache = cache;
	}

	protected Cache getCache() {
		return cache;
	}
	
	protected interface StatementSetter {
		public void set(PreparedStatement statement) throws SQLException;
	}

	protected StatementSetter nullSetter = new StatementSetter() {
		@Override
		public void set(PreparedStatement statement) throws SQLException {
			// nothing
		}
	};

	protected interface RowMapper {
		public void mapRow(ResultSet resultSet) throws SQLException;
	}

	protected void runQuery(String sql, StatementSetter setter,
			RowMapper mapper, Connection connection) throws SQLException {
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(sql);
			setter.set(statement);
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				mapper.mapRow(result);
			}
		} finally {
			if (statement != null)
				statement.close();
		}
	}

	protected static final String COLUMNS_SEASON = "seasons.idSeason, seasons.season ";
	protected static final int COLUMNS_SEASON_NR = 2;
	protected KodiSeason constructSeason(ResultSet resultSet, int offset, KodiTvShow tvShow) throws SQLException {
		int id = resultSet.getInt(offset + 1);
		KodiSeason result = cache.getSeason(id);
		if (result == null) {
			result = new KodiSeason();
			result.setId(id);
			cache.put(result);
		}
		result.setSeason(resultSet.getInt(offset + 2));
		result.setShow(tvShow);
		return result;
	}
	
	protected static final String COLUMNS_EPISODE = "episode.idEpisode, "
		+ "episode.c00, episode.c01, episode.c03, episode.c05, "
		+ "episode.c09, episode.c13 ";
	protected static final int COLUMNS_EPISODE_NR = 7;
	protected KodiEpisode constructEpisode(ResultSet resultSet, int offset, KodiSeason season, KodiFile file) throws SQLException {
		int id = resultSet.getInt(offset + 1);
		KodiEpisode result = cache.getEpisode(id);
		if (result == null) {
			result = new KodiEpisode();
			result.setId(id);
			cache.put(result);
		}
		result.setTitle(resultSet.getString(offset + 2));
		result.setPlot(resultSet.getString(offset + 3));
		result.setRating(resultSet.getDouble(offset + 4));
		result.setFirstAired(LocalDate.parse(resultSet.getString(offset + 5),DATE_FORMAT));
		result.setLength(resultSet.getInt(offset + 6));
		result.setEpsiode(resultSet.getInt(offset + 7));
		result.setSeason(season);
		result.setFile(file);
		return result;
	}
	
	protected static final String COLUMNS_PATH = "path.idPath, path.strPath, path.idParentPath ";
	protected static final int COLUMNS_PATH_NR = 3;
	protected KodiPath constructPath(ResultSet resultSet, int offset)
			throws SQLException {
		int id = resultSet.getInt(offset + 1);
		KodiPath result = cache.getPath(id);
		if (result == null) {
			result = new KodiPath();
			result.setId(id);
			cache.put(result);
		}
		result.setPath(resultSet.getString(offset + 2));
		int parentId = resultSet.getInt(offset + 3);
		if (parentId > 0) {
			result.setParentId(parentId);
			result.setParent(cache.getPath(parentId)); //only fill it if we already have it
		}
		return result;
	}

	protected static final String COLUMNS_TVSHOW = "tvshow.idShow, "
		+ "tvshow.c00, tvshow.c01, tvshow.c04, tvshow.c05, "
		+ "tvshow.c12, tvshow.c13, tvshow.c14 ";
	protected static final int COLUMNS_TVSHOW_NR = 8;
	protected KodiTvShow constructShow(ResultSet resultSet, int offset)
			throws SQLException {
		int id = resultSet.getInt(1 + offset);
		KodiTvShow result = cache.getTvShow(id);
		if (result == null) {
			result = new KodiTvShow();
			result.setId(id);
			cache.put(result);
		}
		result.setTitle(resultSet.getString(2 + offset));
		result.setPlot(resultSet.getString(3 + offset));
		result.setRating(resultSet.getDouble(4 + offset));
		result.setFirstAired(LocalDate.parse(resultSet.getString(5 + offset), DATE_FORMAT));
		result.setTvdbId(resultSet.getInt(6 + offset));
		result.setContentRating(resultSet.getString(7 + offset));
		result.setNetwork(resultSet.getString(8 + offset));
		return result;
	}

	protected static final String COLUMNS_FILE = "files.idFile, files.strFilename, "
		+ "files.dateAdded ";
	protected static final int COLUMNS_FILE_NR = 3;
	protected KodiFile constructFile(ResultSet resultSet, int offset, KodiPath path)
			throws SQLException {
		int id = resultSet.getInt(1 + offset);
		KodiFile result = cache.getFile(id);
		if (result == null) {
			result = new KodiFile();
			result.setId(id);
			cache.put(result);
		}
		result.setFilename(resultSet.getString(2 + offset));

		result.setAddDate(toDateTime(resultSet.getString(3 + offset)));
		result.setPath(path);
		return result;
	}

	protected static final String COLUMNS_ART = " art_id, media_id, media_type, type, url ";
	protected static final int COLUMNS_ART_NR = 5;
	
	protected KodiArt constructArt(ResultSet resultSet, int offset) throws SQLException {
		int id = resultSet.getInt(1 + offset);
		KodiArt result = cache.getArt(id);
		if (result == null) {
			result = new KodiArt();
			result.setId(id);
			cache.put(result);
		}
		result.setMediaId(resultSet.getInt(2 + offset));
		result.setMediaType(MediaType.valueOf(resultSet.getString(3 + offset).toUpperCase()));
		result.setType(Type.valueOf(resultSet.getString(4 + offset).toUpperCase()));
		result.setUrl(resultSet.getString(5 + offset));
		return result;
	}
	
	protected static LocalDateTime toDateTime(String string) {
		if (string == null) return null;
		return LocalDateTime.parse(string, DATETIME_FORMAT);
	}

	protected static LocalDate toDate(String string) {
		if (string == null) return null;
		return LocalDate.parse(string, DATE_FORMAT);
	}
}
