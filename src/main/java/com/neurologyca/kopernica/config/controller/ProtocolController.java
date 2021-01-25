package com.neurologyca.kopernica.config.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neurologyca.kopernica.config.model.Block;
import com.neurologyca.kopernica.config.model.BlockElementList;
import com.neurologyca.kopernica.config.model.BlockList;
import com.neurologyca.kopernica.config.model.Participant;
import com.neurologyca.kopernica.config.model.Protocol;
import com.neurologyca.kopernica.config.model.SegmentList;
import com.neurologyca.kopernica.config.repository.ProtocolRepository;

@RestController // @CrossOrigin(origins = "http://localhost:8080/config-kopernica")
@RequestMapping("/protocol/")
public class ProtocolController {
	@Autowired
	private ProtocolRepository protocolRepository;

	@GetMapping("/getProtocols")
	public List<Protocol> getProtocols() throws Exception {
		return protocolRepository.getProtocols();
	}

	@GetMapping("/getBlockElementsList")
	public List<BlockElementList> getBlockElementsList() throws Exception {
		return protocolRepository.getBlockElementsList();
	}

	@GetMapping("/getBlocks")
	public List<Block> getBlocks() throws Exception {
		return protocolRepository.getBlocks();
	}
	
	@PutMapping("/saveSegmentList/{protocolId}/{protocolName}")
	public SegmentList saveSegmentList(@PathVariable Integer protocolId, @PathVariable String protocolName, @RequestBody SegmentList segmentList) throws Exception
	{
		return protocolRepository.saveSegmentList(protocolId, protocolName, segmentList);
	}

	@DeleteMapping("/deleteSegmentList/{protocolId}/{protocolName}/{segmentListId}/{segmentId}")
	public void deleteSegmentList(@PathVariable Integer protocolId, @PathVariable String protocolName, @RequestBody @PathVariable Integer segmentListId, @RequestBody @PathVariable Integer segmentId) throws Exception
	{
		protocolRepository.deleteSegmentList(protocolId, protocolName, segmentListId, segmentId);
	}
	
	@PutMapping("/saveBlockList/{protocolId}/{protocolName}")
	public BlockList saveBlockList(@PathVariable Integer protocolId, @PathVariable String protocolName, @RequestBody BlockList blockList) throws Exception {
		//System.out.println("saveBlockList");
		return protocolRepository.saveBlockList(protocolId, protocolName, blockList);
	}

	@DeleteMapping("/deleteBlockList/{protocolId}/{protocolName}/{blockListId}/{blockId}")
	public void deleteBlockList(@PathVariable Integer protocolId, @PathVariable String protocolName, @RequestBody @PathVariable Integer blockListId, @RequestBody @PathVariable Integer blockId) throws Exception
	{
		protocolRepository.deleteBlockList(protocolId, protocolName, blockListId, blockId);
	}
	
	@DeleteMapping("/deleteBlockElementList/{protocolId}/{protocolName}/{blockElementListId}/{blockElementId}")
	public void deleteBlockElementList(@PathVariable Integer protocolId, @PathVariable String protocolName, @RequestBody @PathVariable Integer blockElementListId, @RequestBody @PathVariable Integer blockElementId) throws Exception
	{
		protocolRepository.deleteBlockElementList(protocolId, protocolName, blockElementListId, blockElementId);
	}
	
	@DeleteMapping("/deleteProtocol/{protocolId}")
	public void deleteProtocol(@PathVariable Integer protocolId) throws Exception
	{
		protocolRepository.deleteProtocol(protocolId);
	}
	
	@PutMapping("/saveProtocol")
	public void saveProtocol(@RequestBody Protocol protocol) throws Exception
	{
		protocolRepository.saveProtocol(protocol.getId(), protocol.getName());
	}
}