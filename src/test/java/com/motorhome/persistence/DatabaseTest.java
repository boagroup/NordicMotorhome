package com.motorhome.persistence;

import org.junit.jupiter.api.Test;

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
		System.out.println(dR.getMap().toString());
		assertFalse(dR.isEmpty());
	}

}