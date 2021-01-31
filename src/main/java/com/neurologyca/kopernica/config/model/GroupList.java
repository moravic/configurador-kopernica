package com.neurologyca.kopernica.config.model;

public class GroupList {
	private Integer id;
    private Group group;
	
	public GroupList() {
	}

	public GroupList(Integer id, Group group) {
		super();
		this.id = id;
		this.group = group;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	@Override
	public String toString() {
		return "GroupList [id=" + id + ", group=" + group + "]";
	}

}
