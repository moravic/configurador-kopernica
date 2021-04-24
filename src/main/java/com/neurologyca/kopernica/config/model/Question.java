package com.neurologyca.kopernica.config.model;

public class Question {
	private Integer id;
    private String question;
	
	public Question() {
	}
	
	public Question(Integer id, String question) {
		super();
		this.id = id;
		this.question = question;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	@Override
	public String toString() {
		return "Question [id=" + id + ", question=" + question + "]";
	}

}
