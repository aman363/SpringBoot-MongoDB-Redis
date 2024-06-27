package com.example.demo.models;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Employees")
public class Development implements Employee,Serializable {
	private int id;
	private String name;
	private String designation;
	private String city;
	private String description;

	public Development(int id, String name, String designation, String city, String description) {
		this.id = id;
		this.name = name;
		this.designation = designation;
		this.city = city;
		this.description = description;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getDesignation() {
		return designation;
	}

	@Override
	public void setDesignation(String designation) {
		this.designation = designation;
	}

	@Override
	public String getCity() {
		return city;
	}

	@Override
	public void setCity(String city) {
		this.city = city;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
