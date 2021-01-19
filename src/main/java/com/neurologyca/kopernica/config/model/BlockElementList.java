package com.neurologyca.kopernica.config.model;

import java.util.List;

public class BlockElementList {
	private Integer id;
    private BlockElement blockElement;
    
	public BlockElementList(Integer id, BlockElement blockElement) {
		super();
		this.id = id;
		this.blockElement = blockElement;
	}

	public BlockElementList() {
		// TODO Auto-generated constructor stub
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public BlockElement getBlockElement() {
		return blockElement;
	}

	public void setBlockElement(BlockElement blockElement) {
		this.blockElement = blockElement;
	}

	@Override
	public String toString() {
		return "BlockElementList [id=" + id + ", blockElement=" + blockElement + "]";
	}
	
}
