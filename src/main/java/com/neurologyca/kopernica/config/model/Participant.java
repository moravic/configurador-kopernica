package com.neurologyca.kopernica.config.model;

public class Participant {
	private Integer id;
    private String name;
	private String email;
	private Integer age;
	private String gender;
	private String profile;
	private String group;

	
	public Participant() {
	}
	
	public Participant(Integer id, String name, String email, Integer age, String gender, String profile, String group) {
		super();
		this.id = id;
		this.name = name;
		this.gender = gender;
		this.age = age;
		this.profile = profile;
		this.email = email;
		this.group = group;
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

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}
	@Override
	public String toString() {
		return "Participant [id=" + id + ", name=" + name + ", gender=" + gender + ", age=" + age + ", email=" + email + ", profile=" + profile + ", groupName=" + group + "]";
	}

}
