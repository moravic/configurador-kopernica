package com.neurologyca.kopernica.config.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neurologyca.kopernica.config.model.Participant;
import com.neurologyca.kopernica.config.repository.ParticipantRepository;


@RequestMapping("participants")
public class ParticipantController {
    @Autowired
    private ParticipantRepository participantRepository;
    
    @PostMapping()
    public Integer createParticipant(@RequestBody Participant participant) throws Exception {
    	//System.out.println("createParticipant");
        return participantRepository.save(participant);
    }

}