package com.janoz.kodi.service;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionFactory {

	Connection open() throws SQLException;
	void close(Connection connection) throws SQLException;
	
}
