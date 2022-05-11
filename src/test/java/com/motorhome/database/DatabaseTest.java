package com.motorhome.database;

import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseTest {

	Database db;

	@Test
	void getInstance() {
		db = Database.getInstance();
		assertSame(db, Database.getInstance("", "", ""));
	}

	@Test
	void executeQueryNotEmpty() {
		db = Database.getInstance();
		DataResult dR = db.executeQuery("SELECT * FROM users");
		assertFalse(dR.isEmpty());
	}

}