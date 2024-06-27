package com.example.demo.models;

import java.io.Serializable;

import org.springframework.data.redis.core.RedisHash;

@RedisHash("Employee")
public interface Employee extends Serializable{
	int getId();
	void setId(int id);

	String getName();
	void setName(String name);

	String getDesignation();
	void setDesignation(String designation);

	String getCity();
	void setCity(String city);

	String getDescription();
}
