package com.janoz.kodi.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import com.janoz.kodi.model.KodiGenre;

public class GenreDao extends AbstractDao{

	private String TVSHOW = "tvshow";
	private String MOVIE = "movie";

	public GenreDao(Cache cache) {
		super(cache);
	}
	
	public Set<KodiGenre> getGenresForTvShow(int showId, Connection connection) throws SQLException {
		return getGenres(showId,TVSHOW,connection);
	}
	
	public void addGenresToTvShow(int showId, Set<KodiGenre> genres, Connection connection) throws SQLException {
		if (!isInitialized()) initGenres(connection);
		PreparedStatement statement = null;
		try {
			for (KodiGenre genre : genres) {
				statement = connection.prepareStatement("INSERT INTO genre_link ("+
						"media_id, genre_id, media_type)" +
						" VALUES (?,?,?)");
				statement.setInt(1, showId);
				statement.setInt(2, genre.getId());
				statement.setString(3,TVSHOW);
				statement.executeUpdate();
				statement.close();
				statement = null;
			}
		} finally {
			if (statement != null)
				statement.close();
		}
	}
	

	private Set<KodiGenre> getGenres(final int mediaId, final String mediaType, Connection connection) throws SQLException {
		if (!isInitialized()) initGenres(connection);
		String sql = "SELECT g.name from genre g JOIN genre_link l ON g.genre_id = l.genre_id WHERE l.media_id=? and l.media_type=?";
		final Set<KodiGenre> genres = new HashSet<KodiGenre>();
		RowMapper mapper = new RowMapper() {
			
			@Override
			public void mapRow(ResultSet resultSet) throws SQLException {
				KodiGenre genre = KodiGenre.getByNameOrSynonym(resultSet.getString(1));
				if (genre != null) genres.add(genre);
			}
		};
		StatementSetter setter = new StatementSetter() {
			
			@Override
			public void set(PreparedStatement statement) throws SQLException {

				statement.setInt(1, mediaId);
				statement.setString(2, mediaType);
			}
		}; 
		runQuery(sql,setter, mapper, connection);
		return genres;
	}
	
	private boolean isInitialized() {
		return KodiGenre.DRAMA.getId() != null;
	}
	
	
	void initGenres(Connection connection) throws SQLException {
		String sql = "SELECT genre_id, name FROM genre";
		RowMapper mapper = new RowMapper() {
			
			@Override
			public void mapRow(ResultSet resultSet) throws SQLException {
				KodiGenre genre = KodiGenre.getByName(resultSet.getString(2));
				if (genre != null) {
					genre.setId(resultSet.getInt(1));
				}
			}
		};
		super.runQuery(sql, nullSetter, mapper, connection);
	}
}
