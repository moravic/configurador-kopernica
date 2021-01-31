package com.neurologyca.kopernica.config.model;

public class ProtocolGroupList {
	private Integer id;
	private Integer groupId;
	private Integer protocolId;
	
	public ProtocolGroupList() {
	}

	
	public ProtocolGroupList(Integer id, Integer groupId, Integer protocolId) {
		super();
		this.id = id;
		this.groupId = groupId;
		this.protocolId = protocolId;
	}


	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public Integer getGroupId() {
		return groupId;
	}


	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}


	public Integer getProtocolId() {
		return protocolId;
	}


	public void setProtocolId(Integer protocolId) {
		this.protocolId = protocolId;
	}


	@Override
	public String toString() {
		return "ProtocolGroupList [id=" + id + ", groupId=" + groupId + ", protocolId=" + protocolId + "]";
	}

}
