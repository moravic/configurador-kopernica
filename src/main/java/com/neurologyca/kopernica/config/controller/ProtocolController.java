package com.neurologyca.kopernica.config.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neurologyca.kopernica.config.model.Protocol;
import com.neurologyca.kopernica.config.repository.ProtocolRepository;

@RestController //@CrossOrigin(origins = "http://localhost:8080/config-kopernica")
@RequestMapping("protocol")
public class ProtocolController {
    @Autowired
    private ProtocolRepository protocolRepository;

	@GetMapping("/getProtocols/{project}")
	public List<Protocol> getProtocols(@PathVariable String project) throws Exception {
		return protocolRepository.getProtocols();
	}
	
    @PostMapping()
    public Integer saveProtocol(@RequestBody Protocol protocol) throws Exception {
    	System.out.println("createProtocol");
        return protocolRepository.saveProtocol(protocol);
    }

}