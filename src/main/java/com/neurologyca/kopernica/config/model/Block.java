package com.neurologyca.kopernica.config.model;

public class Block {
	private Integer id;
	private String name;
	private BlockElementList blockElementList;
	
	public Block(Integer id, String name, BlockElementList blockElementList) {
		super();
		this.id = id;
		this.name = name;
		this.blockElementList = blockElementList;
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

	public BlockElementList getBlockElementList() {
		return blockElementList;
	}

	public void setBlockElementList(BlockElementList blockElementList) {
		this.blockElementList = blockElementList;
	}

	@Override
	public String toString() {
		return "Block [id=" + id + ", name=" + name + ", blockElementList=" + blockElementList + "]";
	}

}
