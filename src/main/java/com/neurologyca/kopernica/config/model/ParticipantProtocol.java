package com.neurologyca.kopernica.config.model;

public class ParticipantProtocol {
	private Integer id;
	private Integer participantId;
	private Integer protocolId;
	
	public ParticipantProtocol() {
	}
	
	public ParticipantProtocol(Integer id, Integer participantId, Integer protocolId) {
		super();
		this.id = id;
		this.participantId = participantId;
		this.protocolId = protocolId;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getParticipantId() {
		return participantId;
	}
	public void setParticipantId(Integer participantId) {
		this.participantId = participantId;
	}
	public Integer getProtocolId() {
		return protocolId;
	}
	public void setProtocolId(Integer protocolId) {
		this.protocolId = protocolId;
	}
	
	@Override
	public String toString() {
		return "ParticipantProtocol [id=" + id + ", participantId=" + participantId + ", protocolId=" + protocolId + "]";
	}
}
