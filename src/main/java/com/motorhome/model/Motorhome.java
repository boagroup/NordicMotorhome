package com.motorhome.model;

import java.util.Map;

public class Motorhome implements ModelInterface {
	private int id;
	private String image;
	private String type;
	private int beds;

	public Motorhome(Map<String, Object> row) {
		this.id = (int) row.get("id");
		this.image = (String) row.get("image");
		this.type = (String) row.get("type");
		this.beds = (int) row.get("beds");
	}

	public Motorhome(int id, String image, String type, int beds) {
		this.id = id;
		this.image = image;
		this.type = type;
		this.beds = beds;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getBeds() {
		return beds;
	}

	public void setBeds(int beds) {
		this.beds = beds;
	}



}
