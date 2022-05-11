package com.motorhome.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Contains simple methods to connect/disconnect to/from the database
 * Author(s): Octavian Roman
 */
public class SimpleDatabase {
    private static final String user = System.getProperty("user");
    private static final String password = System.getProperty("password");
    private static final String url = "jdbc:mysql://eu-cdbr-west-02.cleardb.net:3306/heroku_e8f7f82549e360a?reconnect=true";

    /**
     * Establish a connection to our Heroku ClearDB remote database
     * @return the connection
     */
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Closes the connection the remote database
     * @param connection the connection to be closed
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
