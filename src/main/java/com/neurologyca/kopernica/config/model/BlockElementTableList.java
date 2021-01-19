package com.neurologyca.kopernica.config.model;

public class BlockElementTableList {
	private Integer id;
	private Integer blockId;
	private Integer blockElementId;

	public BlockElementTableList() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getBlockId() {
		return blockId;
	}

	public void setBlockId(Integer blockId) {
		this.blockId = blockId;
	}

	public Integer getBlockElementId() {
		return blockElementId;
	}

	public void setBlockElementId(Integer blockElementId) {
		this.blockElementId = blockElementId;
	}

	@Override
	public String toString() {
		return "BlockElementTabkeList [id=" + id + ", blockId=" + blockId + ", blockElementId=" + blockElementId +"]";
	}

}
