package com.neurologyca.kopernica.config.model;

public class BlockElement {
	private Integer id;
	private Question question;
	private Stimulus stimulus;
	
	public BlockElement(Integer id, Question question, Stimulus stimulus) {
		super();
		this.id = id;
		this.question = question;
		this.stimulus = stimulus;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	public Stimulus getStimulus() {
		return stimulus;
	}

	public void setStimulus(Stimulus stimulus) {
		this.stimulus = stimulus;
	}

	@Override
	public String toString() {
		return "BlockElement [id=" + id + ", question=" + question + ", stimulus=" + stimulus + "]";
	}

}
