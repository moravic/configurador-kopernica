package com.neurologyca.kopernica.config.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
	public void saveSegmentList(@PathVariable Integer protocolId, @PathVariable String protocolName, @RequestBody List<SegmentList> segmentListArray) throws Exception
	{
		protocolRepository.saveSegmentList(protocolId, protocolName, segmentListArray);
	}

	@PostMapping("/saveBlockList/{protocolId}/{protocolName}")
	public void saveBlockList(@PathVariable Integer protocolId, @PathVariable String protocolName, @RequestBody BlockList blockList) throws Exception {
		//System.out.println("saveBlockList");
		protocolRepository.blockSegmentList(protocolId, protocolName, blockList);
	}

}