package com.motorhome.persistence;

import com.motorhome.model.Brand;
import com.motorhome.model.Model;
import com.motorhome.model.Motorhome;
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

	@Test
	void executePassword() {
		db = Database.getInstance();
		var dR = db.executeQuery("SELECT AES_DECRYPT(password, ?) AS password FROM users WHERE username = ?", System.getProperty("key"), "admin");
		System.out.println(dR.getMap().toString());
	}

	@Test
	void executeFetchStaff() {
		String column = "firstName";
		String order = "ASC";
		//language=SQL
		String query =
				"SELECT staff.id, firstName, lastName, image, telephone, role, gender, " +
						"users.id, staff_id, username, AES_DECRYPT(password, ?) AS decrypted_password, admin " +
						"FROM staff JOIN users ON staff.id = users.staff_id " +
						"ORDER BY " + column + " " + order + ";";
		db = Database.getInstance();
		DataResult results = db.executeQuery(query, System.getProperty("key"));
		System.out.println();
		System.out.println(results.getMap().toString());
		System.out.println(results.getNumberOfRows());
		System.out.println();
		String pass = null;
		while (results.next()) {
			var row = results.getCurrentRow();
			System.out.println(row.toString());
			var m = row.get("decrypted_password");
			pass = new String((byte[]) m);
			System.out.println(pass);
			System.out.println();
		}
		assertNotNull(pass);
		assertEquals("1111", pass);
	}

	@Test
	void executeFetchMotorhome() {
		db = Database.getInstance();
		String column ="brands.name";
		String order = "ASC";
		//language=SQL
		String query =
				"SELECT * FROM motorhomes " +
				"JOIN models ON motorhomes.model_id = models.id " +
				"JOIN brands ON models.brand_id = brands.id " +
				"ORDER BY " + column + " " + order + ";";
		DataResult results = db.executeQuery(query);
		System.out.println(results.getMap().toString());
		while (results.next()) {
			var row = results.getCurrentRow();
			System.out.println(row.toString());
			Motorhome motorhome = new Motorhome(
					(int) row.get("motorhomes.id"),
					(int) row.get("model_id"),
					(String) row.get("image"),
					(boolean) row.get("rented"),
					(String) row.get("type"),
					(int) row.get("beds")
			);
			Brand brand = new Brand(
					(int) row.get("brands.id"),
					(String) row.get("brands.name"),
					(double) row.get("brands.price")
			);
			Model model = new Model(
					(int) row.get("models.id"),
					(int) row.get("brand_id"),
					(String) row.get("models.name"),
					(double) row.get("models.price")
			);
		}
	}

}
