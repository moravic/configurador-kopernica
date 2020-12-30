package com.neurologyca.kopernica.config.model;

public class Participant {
	private Integer id;
    private String name;
	private String gender;
	private Integer age;
	private String type;
	private String email;
	
	public Participant() {
	}
	
	public Participant(Integer id, String name, String gender, Integer age, String type, String email) {
		super();
		this.id = id;
		this.name = name;
		this.gender = gender;
		this.age = age;
		this.type = type;
		this.email = email;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "Participant [id=" + id + ", name=" + name + ", gender=" + gender + ", age=" + age + ", email=" + email + ", type=" + type + "]";
	}

}
