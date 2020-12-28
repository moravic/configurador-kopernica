package com.neurologyca.kopernica.config.model;

public class Study {
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
	
	 @Override
	    public String toString() {
	        return "Study [id=1, project=" + project + ", study=" + study + ", type=" + type
	       + "]";
	    }
	    
	    
}
