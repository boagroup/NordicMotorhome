package com.motorhome.persistence;

import java.sql.*;

/**
 * Contains simple methods to connect to the database
 * Author(s): Bartosz Birylo
 */
public final class Database {
    private static Database instance;

    private final String user;
    private final String password;
    private final String url;

    private static Connection connection = null;

    /**
     * singleton pattern lazy method to get Instance of the singleton object
     * if the singleton was already created with other paremeters or the default ones, new instance will not be created
     * @return {@code Database} instance
     */
    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    /**
     * singleton pattern lazy method to get Instance of the singleton object
     * if the singleton was already created with other paremeters or the default ones, new instance will not be created
     * @param user {@date String} - database user
     * @param password {@date String} - database user's password
     * @param url {@date String} - database url
     * @return {@code Database} instance
     */
    public static Database getInstance(String user, String password, String url) {
        if (instance == null) {
            instance = new Database(user, password, url);
        }
        return instance;
    }

    /**
     * private constructor to keep it singleton
     */
    private Database() {
        this.user = System.getProperty("user");
        this.password = System.getProperty("password");
        this.url = "jdbc:mysql://eu-cdbr-west-02.cleardb.net:3306/heroku_e8f7f82549e360a?reconnect=true";
    }

    /**
     * private constructor to keep it singleton
     */
    private Database(String user, String password, String url) {
        this.user = user;
        this.password = password;
        this.url = url;
    }

    /**
     * method to connect to the database
     * @return {@code true} if successful, else {@code false}
     */
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

    /**
     * method to close the connection to the database
     * @return {@code true} if successful, else {@code false}
     */
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

    /**
     * method that executes SQL query in the database and returns the result as {@code DataResult}
     * @param query {@code String} - string representation of SQL query
     * @param parameters {@code String...} - strings that will be interpolated into SQL query
     * @return {@code DataResult} - custom representation of results from the query as columns and rows
     */
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
}