package com.janoz.kodi.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.janoz.kodi.model.KodiEpisode;
import com.janoz.kodi.model.KodiFile;
import com.janoz.kodi.model.KodiGenre;
import com.janoz.kodi.model.KodiPath;
import com.janoz.kodi.model.KodiSeason;
import com.janoz.kodi.model.KodiTvShow;
/**
 * 
 * @author Gijs de Vries aka Janoz
 *
 *
 *
 *  TODO's
 *  - paths (pathlinktvshow)
 *  
 */
public class TvShowDao extends AbstractDao {

	public TvShowDao(Cache cache) {
		super(cache);
	}
	
	public List<KodiTvShow> getTvShows(Connection connection) throws SQLException {
		return getTvShows("", connection, nullSetter);
	}

	public KodiTvShow getTvShow(final int id, Connection connection) throws SQLException {
		List<KodiTvShow> tvShows = getTvShows("WHERE tvshow.idShow = ?", connection, new StatementSetter() {
			@Override
			public void set(PreparedStatement statement) throws SQLException {
				statement.setInt(1, id);
			}
		});
		if (tvShows.isEmpty()) {
			return null;
		} else {
			return tvShows.get(0);
		}
	}
	
	public void addTvShow(KodiTvShow tvShow, Connection connection) throws SQLException {
		if (tvShow.getId() != null) throw new SQLException("TvShow already has id.");
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement("INSERT INTO tvshow ("+
					"c00,c01,c04,c05,c08,c12,c13,c14 )" +
					" VALUES (?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			//c00 Titel
			statement.setString(1, tvShow.getTitle());
			//c01 omschrijving
			statement.setString(2, tvShow.getPlot());
			//c04 rating
			if (tvShow.getRating() == null) {
				statement.setNull(3, Types.FLOAT);
			} else {
				statement.setDouble(3, tvShow.getRating());
			}
			//c05 firstaired
			if (tvShow.getFirstAired() == null) {
				statement.setNull(4, Types.VARCHAR);
			} else {
				statement.setString(4, tvShow.getFirstAired().toString("yyyy-MM-dd"));
			}
			//c08 genre denormalized.. Seems to be needed..
			statement.setString(5, implode(tvShow.getGenres()));
			//c12 thetvdbid
			statement.setObject(6, tvShow.getTvdbId(), Types.INTEGER);
			//c13 content rating
			statement.setString(7, tvShow.getContentRating());
			//c14 network
			statement.setString(8, tvShow.getNetwork());

			statement.executeUpdate();
			ResultSet rs = statement.getGeneratedKeys();
			if (rs.next()) {
				tvShow.setId(rs.getInt(1));
				getCache().put(tvShow);
			}
		} finally {
			if (statement != null)
				statement.close();
		}
	}

	private String implode(Set<KodiGenre> genres) {
		Iterator<KodiGenre> genreIt = genres.iterator();
		StringBuilder sb = new StringBuilder();
		while (genreIt.hasNext()) {
			sb.append(genreIt.next().getPersistanceValue());
			if (genreIt.hasNext()) {
				sb.append(" / ");
			}
		}
		return sb.toString();
	}

	public KodiTvShow getTvShowByTvDbId(final int id, Connection connection) throws SQLException {
		List<KodiTvShow> tvShows = getTvShows("WHERE tvshow.c12 = ?", connection, new StatementSetter() {
			@Override
			public void set(PreparedStatement statement) throws SQLException {
				statement.setInt(1, id);
			}
		});
		if (tvShows.isEmpty()) {
			return null;
		} else {
			return tvShows.get(0);
		}
	}
	
	public KodiEpisode getEpisode(final int id, Connection connection) throws SQLException {
		List<KodiEpisode> episodes = getEpisodes("WHERE episode.idEpisode = ?", connection, new StatementSetter() {
			@Override
			public void set(PreparedStatement statement) throws SQLException {
				statement.setInt(1, id);
			}
		});
		if (episodes.isEmpty()) {
			return null;
		} else {
			return episodes.get(0);
		}
	}
	
	public KodiSeason getOnlySeason(final int showId, final int season, Connection connection) throws SQLException {
		String where = "WHERE tvshow.idShow = ? AND seasons.season = ?";
		StatementSetter setter = new StatementSetter() {
			
			@Override
			public void set(PreparedStatement statement) throws SQLException {
				statement.setInt(1, showId);
				statement.setInt(2, season);
			}
		};
		
		final List<KodiSeason> seasons = new ArrayList<KodiSeason>();
		String sql = "SELECT " +
			COLUMNS_TVSHOW + "," +
			COLUMNS_SEASON +
			"FROM tvshow " +
			"JOIN seasons ON tvshow.idShow = seasons.idShow " + where;
		RowMapper mapper = new RowMapper() {
			
			@Override
			public void mapRow(ResultSet resultSet) throws SQLException {
				KodiTvShow tvShow = constructShow(resultSet, 0);
				KodiSeason season = constructSeason(resultSet,
						COLUMNS_TVSHOW_NR, 
						tvShow);
				seasons.add(season);
			}
		};
		runQuery(sql, setter, mapper, connection);
		if (seasons.isEmpty()) {
			return null;
		} else {
			return seasons.get(0);
		}
	}
	
	public List<KodiEpisode> getSeason(final int showId, final int season, Connection connection) throws SQLException {
		return getEpisodes("WHERE episode.idShow = ? AND seasons.season = ? ORDER BY 0+episode.c13", connection, new StatementSetter() {
			@Override
			public void set(PreparedStatement statement) throws SQLException {
				statement.setInt(1, showId);
				statement.setInt(2, season);
			}
		});	
	}

	public void addSeason(KodiSeason season, Connection connection) throws SQLException {
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement("INSERT INTO seasons ("+
					"idShow, season)" +
					" VALUES (?,?)", Statement.RETURN_GENERATED_KEYS);
			statement.setInt(1, season.getShow().getId());
			statement.setInt(2, season.getSeason());
			statement.executeUpdate();
			ResultSet rs = statement.getGeneratedKeys();
			if (rs.next()) {
				season.setId(rs.getInt(1));
				getCache().put(season);
			}
		} finally {
			if (statement != null)
				statement.close();
		}
	}
	
	public void addEpisode(KodiEpisode episode, Connection connection) throws SQLException {
		if (episode.getId() != null) throw new SQLException("Episoode already has id.");
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement("INSERT INTO episode ("+
					"c00, c01, c03, " +
					"c05, c09, c12, c13," +
					"idShow, idFile )" +
					" VALUES (?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, episode.getTitle());
			statement.setString(2, episode.getPlot());
			if (episode.getRating() == null) {
				statement.setNull(3, Types.FLOAT);
			} else {
				statement.setDouble(3, episode.getRating());
			}
			statement.setString(4, episode.getFirstAired().toString("yyyy-MM-dd"));
			if (episode.getLength() == null) {
				statement.setNull(5, Types.INTEGER);
			} else {
				statement.setInt(5, episode.getLength());
			}
			statement.setInt(6, episode.getSeason().getSeason());
			statement.setInt(7, episode.getEpsiode());
			statement.setInt(8, episode.getSeason().getShow().getId());
			statement.setInt(9, episode.getFile().getId());

			

			statement.executeUpdate();
			ResultSet rs = statement.getGeneratedKeys();
			if (rs.next()) {
				episode.setId(rs.getInt(1));
				getCache().put(episode);
			}
		} finally {
			if (statement != null)
				statement.close();
		}
	}
	
	
	
	private List<KodiEpisode> getEpisodes(String where, Connection connection, StatementSetter setter) throws SQLException {
		
		final List<KodiEpisode> episodes = new ArrayList<KodiEpisode>();
		String sql = "SELECT " +
				COLUMNS_TVSHOW + "," +
				COLUMNS_SEASON + "," +
				COLUMNS_PATH + "," +
				COLUMNS_FILE + "," +
				COLUMNS_EPISODE + 
				"FROM episode " +
				"JOIN files ON episode.idFile = files.idFile " +
				"JOIN path ON files.idPath = path.idPath " +
				"JOIN seasons ON episode.idShow = seasons.idShow AND episode.c12 = seasons.season " +
				"JOIN tvshow ON episode.idShow = tvshow.idShow " + where;
		RowMapper mapper = new RowMapper() {
			
			@Override
			public void mapRow(ResultSet resultSet) throws SQLException {
				KodiTvShow tvShow = constructShow(resultSet, 0);
				KodiSeason season = constructSeason(resultSet,
						COLUMNS_TVSHOW_NR, 
						tvShow);
				KodiPath path = constructPath(resultSet,
						COLUMNS_TVSHOW_NR + COLUMNS_SEASON_NR);
				KodiFile file = constructFile(resultSet,
						COLUMNS_TVSHOW_NR + COLUMNS_SEASON_NR + COLUMNS_PATH_NR, 
						path);
				KodiEpisode episode = constructEpisode(resultSet,
						COLUMNS_TVSHOW_NR + COLUMNS_SEASON_NR + COLUMNS_PATH_NR + COLUMNS_FILE_NR, 
						season, file);
				episodes.add(episode);
			}
		};
		runQuery(sql, setter, mapper, connection);
		return episodes;	
	}
	
	private List<KodiTvShow> getTvShows(String where, Connection connection, StatementSetter setter) throws SQLException {
		final List<KodiTvShow> tvShows = new ArrayList<KodiTvShow>();
		String sql = "SELECT " +
				COLUMNS_TVSHOW+
				"FROM tvshow " + where;
		RowMapper mapper = new RowMapper(){
			@Override
			public void mapRow(ResultSet resultSet) throws SQLException {
				KodiTvShow tvShow = constructShow(resultSet,0);
				tvShows.add(tvShow);
			}
		};
		runQuery(sql, setter, mapper, connection);
		return tvShows;
	}
	
}
