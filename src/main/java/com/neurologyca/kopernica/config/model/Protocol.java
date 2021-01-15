package com.neurologyca.kopernica.config.model;

public class Protocol {
	private Integer id;
	private String name;
	private SegmentList segmentList;
	private Integer blockList;

	public Protocol(Integer id, String name, SegmentList segmentList, Integer blockList) {
		super();
		this.id = id;
		this.name = name;
		this.segmentList = segmentList;
		this.blockList = blockList;
	}

	public Protocol() {

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

	public SegmentList getSegmentList() {
		return segmentList;
	}

	public void setSegmentList(SegmentList segmentList) {
		this.segmentList = segmentList;
	}

	public Integer getBlockList() {
		return blockList;
	}

	public void setBlockList(Integer blockList) {
		this.blockList = blockList;
	}

	@Override
	public String toString() {
		return "Protocol [id=" + id + ", name=" + name + ", segmentList=" + segmentList + ", blockList="
				+ blockList + "]";
	}

}
