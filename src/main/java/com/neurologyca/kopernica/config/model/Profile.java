package com.neurologyca.kopernica.config.model;

public class Profile {
	private Integer id;
    private String type;
	
	public Profile() {
	}
	
	public Profile(Integer id, String type) {
		super();
		this.id = id;
		this.type = type;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Profile [id=" + id + ", type=" + type + "]";
	}

}
