package com.neurologyca.kopernica.config.model;

import java.util.List;

public class BlockElementList {
	private Integer id;
    private List<BlockElement> blockElementArray;
    
	public BlockElementList(Integer id, List<BlockElement> blockElementArray) {
		super();
		this.id = id;
		this.blockElementArray = blockElementArray;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public List<BlockElement> getBlockElementArray() {
		return blockElementArray;
	}

	public void setBlockElementArray(List<BlockElement> blockElementArray) {
		this.blockElementArray = blockElementArray;
	}

	@Override
	public String toString() {
		return "BlockElementList [id=" + id + ", blockElementArray=" + blockElementArray + "]";
	}
	
}
