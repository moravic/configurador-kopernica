package com.neurologyca.kopernica.config.model;

import java.util.List;

public class Protocol {
	private Integer id;
	private String name;
	private List<SegmentList> segmentListArray;
	private List<GroupList> groupListArray;
	private List<BlockList> blockListArray;

	public Protocol(Integer id, String name, List<SegmentList> segmentListArray, List<GroupList> groupListArray, List<BlockList> blockListArray) {
		super();
		this.id = id;
		this.name = name;
		this.segmentListArray = segmentListArray;
		this.groupListArray = groupListArray;
		this.blockListArray = blockListArray;
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

	public List<SegmentList> getSegmentListArray() {
		return segmentListArray;
	}

	public void setSegmentListArray(List<SegmentList> segmentListArray) {
		this.segmentListArray = segmentListArray;
	}

	public List<GroupList> getGroupListArray() {
		return groupListArray;
	}

	public void setGroupListArray(List<GroupList> groupListArray) {
		this.groupListArray = groupListArray;
	}
	
	public List<BlockList> getBlockListArray() {
		return blockListArray;
	}

	public void setBlockListArray(List<BlockList> blockListArray) {
		this.blockListArray = blockListArray;
	}

	@Override
	public String toString() {
		return "Protocol [id=" + id + ", name=" + name + ", segmentListArray=" + segmentListArray + ", groupListArray=" + groupListArray + ", blockListArray=" + blockListArray
				+ "]";
	}

}
