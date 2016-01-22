package com.janoz.kodi.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.janoz.kodi.model.KodiArt;
import com.janoz.kodi.model.KodiArt.MediaType;
import com.janoz.kodi.model.KodiArt.Type;

public class ArtDao extends AbstractDao {

	public ArtDao(Cache cache) {
		super(cache);
	}
	public List<KodiArt> getArt(final int mediaId, final MediaType mediaType, final Type type, Connection connection) throws SQLException {
		String where = "WHERE media_id = ? AND media_type = ? AND type = ?";
		StatementSetter setter = new StatementSetter() {
			
			@Override
			public void set(PreparedStatement statement) throws SQLException {
				statement.setInt(1, mediaId);
				statement.setString(2, mediaType.getPersistanceValue());
				statement.setString(3, type.getPersistanceValue());
			}
		};
		final List<KodiArt> arts = new ArrayList<KodiArt>();
		String sql = "SELECT " +
			COLUMNS_ART + 
			"FROM art " + where;
		RowMapper mapper = new RowMapper(){
			@Override
			public void mapRow(ResultSet resultSet) throws SQLException {
				KodiArt art = constructArt(resultSet, 0);
				arts.add(art);
			}

		};
		runQuery(sql, setter, mapper, connection);
		return arts;
	}
	
	public void addArt(KodiArt art, Connection connection) throws SQLException {
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement("INSERT INTO art ("+
					" media_id, media_type, type, url) " +
					" VALUES (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			statement.setInt(1, art.getMediaId());
			statement.setString(2, art.getMediaType().getPersistanceValue());
			statement.setString(3, art.getType().getPersistanceValue());
			statement.setString(4, art.getUrl());

			statement.executeUpdate();
			ResultSet rs = statement.getGeneratedKeys();
			if (rs.next()) {
				art.setId(rs.getInt(1));
			}
		} finally {
			if (statement != null)
				statement.close();
		}
	}
}
