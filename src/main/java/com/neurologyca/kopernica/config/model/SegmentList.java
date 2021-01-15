package com.neurologyca.kopernica.config.model;

import java.util.List;

public class SegmentList {
	private Integer id;
    private List<Segment> segmentArray;
	
	public SegmentList() {
	}
	
	public SegmentList(Integer id, List<Segment> segmentArray) {
		super();
		this.id = id;
		this.segmentArray = segmentArray;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public List<Segment> getSegmentArray() {
		return segmentArray;
	}

	public void setSegmentArray(List<Segment> segmentArray) {
		this.segmentArray = segmentArray;
	}

	@Override
	public String toString() {
		return "SegmentList [id=" + id + ", segmentArray=" + segmentArray + "]";
	}

}
