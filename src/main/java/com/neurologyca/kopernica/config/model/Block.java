package com.neurologyca.kopernica.config.model;

import java.util.List;

public class Block {
	private Integer id;
	private String name;
	private List<BlockElementList> blockElementListArray;
	
	public Block(Integer id, String name, List<BlockElementList> blockElementListArray) {
		super();
		this.id = id;
		this.name = name;
		this.blockElementListArray = blockElementListArray;
	}

	public Block() {
		// TODO Auto-generated constructor stub
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

	public List<BlockElementList> getBlockElementListArray() {
		return blockElementListArray;
	}

	public void setBlockElementListArray(List<BlockElementList> blockElementListArray) {
		this.blockElementListArray = blockElementListArray;
	}

	@Override
	public String toString() {
		return "Block [id=" + id + ", name=" + name + ", blockElementListArray=" + blockElementListArray + "]";
	}

}
