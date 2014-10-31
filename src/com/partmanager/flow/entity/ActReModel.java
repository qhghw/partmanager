package com.partmanager.flow.entity;

import java.io.Serializable;

public class ActReModel  implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String key;
	private String description;
	private String flatkey;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getFlatkey() {
		return flatkey;
	}
	public void setFlatkey(String flatkey) {
		this.flatkey = flatkey;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	
}
