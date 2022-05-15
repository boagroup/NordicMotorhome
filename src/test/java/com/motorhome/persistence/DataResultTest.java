package com.motorhome.persistence;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DataResultTest {

	DataResult tested;

	@BeforeEach
	@Order(0)
	void setUp() {
		Database db = Database.getInstance();
		String query = "SELECT * FROM users;";
		System.out.println(query);
		System.out.println();
		tested = db.executeQuery(query);
		System.out.println(tested.getMap());
	}

	@AfterEach
	@Order(1)
	void tearDown() {
		tested = null;
	}

	@Test
	@Order(2)
	void isEmpty() {
		assertFalse(tested.isEmpty());
	}

	@Test
	@Order(3)
	void getNumberOfColumns() {
		assertEquals(5, tested.getNumberOfColumns());
	}

	@Test
	@Order(5)
	void getNumberOfRows() {
		assertEquals(3, tested.getNumberOfRows());
	}

	@Test
	@Order(4)
	void getColumnName() {
		assertEquals("id", tested.getColumnName(0));
		assertNull(tested.getColumnName(5));
		assertEquals("admin", tested.getColumnName(4));
	}

	@Test
	@Order(6)
	void getColumnIndex() {
		assertEquals(0, tested.getColumnIndex("id"));
		assertEquals(4, tested.getColumnIndex("admin"));
		assertEquals(-1, tested.getColumnIndex("not it table"));
	}

	@Test
	@Order(7)
	void getData() {
		assertEquals(1, tested.getData("id",0));
		assertNull(tested.getData("non existent", 0));
		assertThrows(IndexOutOfBoundsException.class, () -> tested.getData("admin",4));
	}

	@Test
	@Order(8)
	void intGetData() {
		assertEquals(1, tested.getData(0, 0));
		assertNull(tested.getData(5, 0));
		assertThrows(IndexOutOfBoundsException.class, () -> tested.getData(0,6));
	}

	@Test
	@Order(9)
	void getColumn() {
		assertEquals(1, tested.getColumn("id").get(0));
		assertNull(tested.getColumn("nonexistent"));
	}

	@Test
	@Order(10)
	void intGetColumn() {
		assertEquals(1, tested.getColumn(0).get(0));
		assertNull(tested.getColumn(5));
	}

	@Test
	@Order(11)
	void getMap() {
		assertNotNull(tested.getMap());
		assertEquals(1, tested.getMap().get("id").get(0));
	}

	@Test
	@Order(12)
	void getRow() {
		var row= tested.getRow(0);
		assertEquals(1, row.get("id"));
		assertEquals(1, row.get("staff_id"));
		assertEquals("admin", row.get("username"));
		assertNotNull(row.get("password"));
		assertEquals(true, row.get("admin"));
	}

	@Test
	@Order(14)
	void next() {
		var id = tested.getCurrentRow();
		tested.next();
		assertNotSame(id, tested.getCurrentRow());
	}

	@Test
	@Order(13)
	void getCurrentRow() {
		var row = tested.getCurrentRow();
		assertNotNull(row);
		assertEquals(row, tested.getRow(0));
		assertEquals(1, row.get("id"));
	}

	@Test
	@Order(15)
	void resetIter() {
		var row = tested.getCurrentRow();
		tested.next();
		tested.resetIter();
			assertEquals(row, tested.getCurrentRow());
		assertEquals(1, tested.getCurrentRow().get("id"));
	}
}