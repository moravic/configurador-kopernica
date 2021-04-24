package com.neurologyca.kopernica.config.model;

import java.sql.Date;

public class Study {
	private String project;
    private String study;
	private String type;
	private Date initDate;
	private Date endDate;
	
	public Study() {	  
	    }
	 
	public Study(String project, String study, String type) {
	         this.project = project;
	         this.study = study;
	         this.type = type;
	    }
	
	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
	}
	
	public String getStudy() {
		return study;
	}
	public void setStudy(String study) {
		this.study = study;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public Date getInitDate() {
		return initDate;
	}

	public void setInitDate(Date initDate) {
		this.initDate = initDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Override
	public String toString() {
		return "Study [project=" + project + ", study=" + study + ", type=" + type + ", initDate=" + initDate
				+ ", endDate=" + endDate + "]";
	}
	    
}
