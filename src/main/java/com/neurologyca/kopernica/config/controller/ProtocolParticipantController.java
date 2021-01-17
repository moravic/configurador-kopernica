package com.neurologyca.kopernica.config.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.neurologyca.kopernica.config.repository.ProtocolParticipantRepository;

@RestController //@CrossOrigin(origins = "http://localhost:8080/config-kopernica")
@RequestMapping("protocolparticipant")
public class ProtocolParticipantController {
    @Autowired
    private ProtocolParticipantRepository protocolParticipantRepository;
    
    @PostMapping("/applyConditions")
    public Integer applyConditions() throws Exception {
    	System.out.println("applyConditions");
        return protocolParticipantRepository.applyConditions();
    }

}