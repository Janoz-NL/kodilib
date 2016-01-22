package com.janoz.kodi.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDateTime;

import com.janoz.kodi.model.KodiFile;
import com.janoz.kodi.model.KodiPath;
import com.janoz.kodi.model.KodiTvShow;

public class FileDao extends AbstractDao {

	//Cache cache;
	
	public FileDao(Cache cache) {
		super(cache);
	}
	
	public KodiFile getFile(final int id, Connection connection) throws SQLException {
		List<KodiFile> files = getFiles("WHERE files.idFile = ? ", connection, new StatementSetter() {
			@Override
			public void set(PreparedStatement statement) throws SQLException {
				statement.setInt(1, id);
			}
		});
		if (files.isEmpty()) {
			return null;
		} else {
			return files.get(0);
		}
	}

	public KodiFile getOrCreateFile(String candidate, Connection connection) throws SQLException {
		int split= candidate.lastIndexOf("/");
		final String strPath = candidate.substring(0,split+1);
		final String strFile = candidate.substring(split+1);
		final KodiPath path = getOrCreatePath(strPath, connection);
		if (path.getId() != null) {
			List<KodiFile> files = getFiles("WHERE files.strFilename = ? AND path.idPath = ?", connection,new StatementSetter() {
				@Override
				public void set(PreparedStatement statement) throws SQLException {
					statement.setString(1, strFile);
					statement.setInt(2, path.getId());
				}
			});
			if (!files.isEmpty()) {
				return files.get(0);
			}
		}
		KodiFile file = new KodiFile();
		file.setPath(path);
		file.setFilename(strFile);
		file.setAddDate(LocalDateTime.now());
		return file;
	}

	public KodiPath getOrCreatePath(final String path, Connection connection) throws SQLException {
		List<KodiPath> paths = getPaths("WHERE path.strPath=?", connection, new StatementSetter() {
			@Override
			public void set(PreparedStatement statement) throws SQLException {
				statement.setString(1, path);
			}
		});
		if (!paths.isEmpty()) {
			return paths.get(0);
		} else {
			KodiPath result = new KodiPath();

			int split= path.lastIndexOf("/", path.length()-2 );
			final String parentPathStr = path.substring(0,split+1);
			if (parentResolvmentNecessary(parentPathStr)) {
				result.setParent(getOrCreatePath(parentPathStr,connection));
			}
			result.setPath(path);
			return result;
		}
	}

	/**
	 * 
	 * @param parentPathStr
	 * @return true when constructing a (parent) path element from parentPathStr is usefull
	 * 
	 * Current implementation ignores the first 10 chars (for https:// and other 
	 * protocols) and returns true if there are 2 or more /.
	 * 
	 */
	private boolean parentResolvmentNecessary(String parentPathStr) {
		if (parentPathStr.length()<11) return false;
		String workString = parentPathStr.substring(10);
		return (workString.length() - workString.replace("/", "").length()) > 1;
	}

	public KodiPath getPath(final int pathId, Connection connection) throws SQLException {
		List<KodiPath> paths = getPaths("WHERE path.idPath=?", connection, new StatementSetter() {
			@Override
			public void set(PreparedStatement statement) throws SQLException {
				statement.setInt(1, pathId);
			}
		});
		if (!paths.isEmpty()) {
			return paths.get(0);
		} else {
			return null;
		}
	}
	
	public void addFile(KodiFile file, Connection connection) throws SQLException {
		if (file.getId() != null) throw new SQLException("File already has id.");
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement("INSERT INTO files ("+
					" strFilename, dateAdded, idPath ) " +
					" VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, file.getFilename());
			statement.setString(2, file.getAddDate().toString(DATETIME_FORMAT));
			statement.setInt(3, file.getPath().getId());

			statement.executeUpdate();
			ResultSet rs = statement.getGeneratedKeys();
			if (rs.next()) {
				file.setId(rs.getInt(1));
				getCache().put(file);
			}
		} finally {
			if (statement != null)
				statement.close();
		}
	}
	
	public void addPath(KodiPath path, Connection connection) throws SQLException {
		if (path.getId() != null) throw new SQLException("Path already has id.");
		if (path.getParent() != null){
			if (path.getParent().getId() == null) {
				addPath(path.getParent(),connection);
			}
			path.setParentId(path.getParent().getId());
		}
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement("INSERT INTO path (strPath) " +
					" VALUES (?)", Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, path.getPath());

			statement.executeUpdate();
			ResultSet rs = statement.getGeneratedKeys();
			if (rs.next()) {
				path.setId(rs.getInt(1));
				getCache().put(path);
			}
		} finally {
			if (statement != null)
				statement.close();
		}
	}
	
	
	public void addPathToTvShow(KodiTvShow tvShow, KodiPath path, Connection connection) throws SQLException {
		if (path.getId() == null) {
			addPath(path, connection);
		}
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement("INSERT INTO " +
					"tvshowlinkpath (idShow, idPath) " +
					" VALUES (?,?)");
			statement.setInt(1, tvShow.getId());
			statement.setInt(2, path.getId());

			statement.executeUpdate();
		} finally {
			if (statement != null)
				statement.close();
		}
	}
	
	private List<KodiFile> getFiles(String where, Connection connection, StatementSetter setter) throws SQLException{
		final List<KodiFile> files = new ArrayList<KodiFile>();
		String sql = "SELECT " +
					COLUMNS_PATH + "," +
					COLUMNS_FILE +
					"FROM files JOIN path ON files.idPath = path.idPath " + where;
		RowMapper mapper = new RowMapper(){
			@Override
			public void mapRow(ResultSet resultSet) throws SQLException {
				KodiPath path = constructPath(resultSet, 0);
				KodiFile file = constructFile(resultSet, COLUMNS_PATH_NR, path);
				files.add(file);
			}

		};
		runQuery(sql, setter, mapper, connection);
		return files;
	}

	

	private List<KodiPath> getPaths(String where, Connection connection, StatementSetter setter) throws SQLException{
		final List<KodiPath> paths = new ArrayList<KodiPath>();
		String sql = "SELECT " +
			COLUMNS_PATH +
			"FROM path " + where;
		RowMapper mapper = new RowMapper(){
			@Override
			public void mapRow(ResultSet resultSet) throws SQLException {
				KodiPath path = constructPath(resultSet, 0);
				paths.add(path);
			}
		};
		
		runQuery(sql, setter, mapper, connection);
		return paths;
	}

}
