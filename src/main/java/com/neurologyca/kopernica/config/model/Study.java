package com.neurologyca.kopernica.config.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "studies")
public class Study {
	private long id;
	private String project;
    private String study;
	private String type;
	
	public Study() {	  
	    }
	 
	public Study(String project, String study, String type) {
	         this.project = project;
	         this.study = study;
	         this.type = type;
	    }
	
	 @Id
	 @GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	@Column(name = "project", nullable = false)
	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
	}
	
	@Column(name = "study", nullable = false)
	public String getStudy() {
		return study;
	}
	public void setStudy(String study) {
		this.study = study;
	}
	
	@Column(name = "type", nullable = false)
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	 @Override
	    public String toString() {
	        return "Study [id=" + id + ", project=" + project + ", study=" + study + ", type=" + type
	       + "]";
	    }
	    
	    
}
