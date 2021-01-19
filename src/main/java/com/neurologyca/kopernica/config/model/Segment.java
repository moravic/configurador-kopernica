package com.neurologyca.kopernica.config.model;

public class Segment {
	private Integer id;
	private String type;
	private Integer valueAgeMin;
	private Integer valueAgeMax;
	private String valueGender;
	private String valueProfile;

	public Segment(Integer id, String type, Integer valueAgeMin, Integer valueAgeMax, String valueGender,
			String valueProfile) {
		super();
		this.id = id;
		this.type = type;
		this.valueAgeMin = valueAgeMin;
		this.valueAgeMax = valueAgeMax;
		this.valueGender = valueGender;
		this.valueProfile = valueProfile;
	}
	
	public Segment() {
		super();
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

	public Integer getValueAgeMin() {
		return valueAgeMin;
	}

	public void setValueAgeMin(Integer valueAgeMin) {
		this.valueAgeMin = valueAgeMin;
	}

	public Integer getValueAgeMax() {
		return valueAgeMax;
	}

	public void setValueAgeMax(Integer valueAgeMax) {
		this.valueAgeMax = valueAgeMax;
	}

	public String getValueGender() {
		return valueGender;
	}

	public void setValueGender(String valueGender) {
		this.valueGender = valueGender;
	}

	public String getValueProfile() {
		return valueProfile;
	}

	public void setValueProfile(String valueProfile) {
		this.valueProfile = valueProfile;
	}

	@Override
	public String toString() {
		return "Segment [id=" + id + ", type=" + type + ", valueAgeMin=" + valueAgeMin + ", valueAgeMax=" + valueAgeMax
				+ ", valueGender=" + valueGender + ", valueProfile=" + valueProfile + "]";
	}

}
