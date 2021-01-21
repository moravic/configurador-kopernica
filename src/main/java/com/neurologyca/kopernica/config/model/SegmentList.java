package com.neurologyca.kopernica.config.model;

public class SegmentList {
	private Integer id;
    private Segment segment;
	
	public SegmentList() {
	}
	
	public SegmentList(Integer id, Segment segment) {
		super();
		this.id = id;
		this.segment = segment;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Segment getSegment() {
		return segment;
	}

	public void setSegment(Segment segment) {
		this.segment = segment;
	}

	@Override
	public String toString() {
		return "SegmentList [id=" + id + ", segment=" + segment + "]";
	}

}
