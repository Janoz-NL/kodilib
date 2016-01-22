package com.janoz.kodi.service.impl;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

import com.janoz.kodi.service.ConnectionFactory;
import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * Created by vriesgij on 25-10-2015.
 */
public class PooledConnectionFactory implements ConnectionFactory {


    private ComboPooledDataSource cpds;

    public PooledConnectionFactory(String host, String database, String username, String password) throws PropertyVetoException {
        cpds = new ComboPooledDataSource();
        cpds.setDriverClass("com.mysql.jdbc.Driver");
        cpds.setJdbcUrl("jdbc:mysql://" + host + "/" + database + "?autoReconnect=true");
        cpds.setUser(username);
        cpds.setPassword(password);
    }


    @Override public Connection open() throws SQLException {
        return cpds.getConnection();
    }

    @Override public void close(Connection connection) throws SQLException {
        connection.close();
    }
}
