package com.janoz.kodi.service.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.janoz.kodi.service.ConnectionFactory;

public class NonClosingConnectionFactory implements ConnectionFactory{

	private Connection connection;

	public NonClosingConnectionFactory(String host, String database, String username, String password) throws SQLException {
		connect(host, database, username, password);
	}

	protected void connect(String host, String database, String username,
			String password) throws SQLException {
		String dbUrl = "jdbc:mysql://"+host+"/"+database;
		String dbClass = "com.mysql.jdbc.Driver";
		
		try {
			Class.forName(dbClass);
		} catch (ClassNotFoundException e) {
			throw new SQLException(e);
		}
		connection = DriverManager.getConnection(dbUrl,
				username, password);
	}
	
	@Override
	public Connection open() throws SQLException {		
		return connection;
	}

	@Override
	public void close(Connection connection) throws SQLException {
		//do nothing
	}

	public void shutdown() throws SQLException {
		connection.close();
	}

}
