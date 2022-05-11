package com.motorhome.database;

import java.sql.*;

/**
 * Contains simple methods to connect to the database
 * Author(s): Octavian Roman
 */
public final class Database {
    private static Database instance;

    private final String user;
    private final String password;
    private final String url;

    private static Connection connection = null;


    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public static Database getInstance(String user, String password, String url) {
        if (instance == null) {
            instance = new Database(user, password, url);
        }
        return instance;
    }

    private Database() {
        this.user = System.getProperty("user");
        this.password = System.getProperty("password");
        this.url = "jdbc:mysql://eu-cdbr-west-02.cleardb.net:3306/heroku_e8f7f82549e360a?reconnect=true";
    }

    private Database(String user, String password, String url) {
        this.user = user;
        this.password = password;
        this.url = url;
    }

    private boolean connect() {
        try {
            if (connection == null || !connection.isValid(1000)) {
                connection = DriverManager.getConnection(url,user, password);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        connection = null;
        return false;
    }

    private boolean close() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public DataResult executeQuery(String query, String... parameters) {
        try {
            if (connect()) {
               PreparedStatement statement = connection.prepareStatement(query);
                for (int i = 0; i < parameters.length; i++) {
                    statement.setString(i+1, parameters[i]);
                }
                return new DataResult(statement.executeQuery());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return null;
    }

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