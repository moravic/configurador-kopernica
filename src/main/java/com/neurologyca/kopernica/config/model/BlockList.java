package com.neurologyca.kopernica.config.model;

public class BlockList {
	private Integer id;
    private Block block;
	
	public BlockList() {
	}

	public BlockList(Integer id, Block block) {
		super();
		this.id = id;
		this.block = block;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Block getBlock() {
		return block;
	}

	public void setBlock(Block block) {
		this.block = block;
	}

	@Override
	public String toString() {
		return "BlockList [id=" + id + ", block=" + block + "]";
	}

}
