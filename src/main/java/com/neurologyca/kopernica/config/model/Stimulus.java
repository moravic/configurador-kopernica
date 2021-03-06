package com.neurologyca.kopernica.config.model;

public class Stimulus {
	private Integer id;
    private String name;
	
	public Stimulus() {
	}
	
	public Stimulus(Integer id, String name) {
		super();
		this.id = id;
		this.name = name;
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

	@Override
	public String toString() {
		return "Participant [id=" + id + ", name=" + name + "]";
	}

}
