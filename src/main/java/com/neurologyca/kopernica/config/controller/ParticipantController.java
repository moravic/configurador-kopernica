package com.neurologyca.kopernica.config.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neurologyca.kopernica.config.model.Participant;
import com.neurologyca.kopernica.config.model.Profile;
import com.neurologyca.kopernica.config.repository.ParticipantRepository;

@RestController
@RequestMapping("participants")
public class ParticipantController {
    @Autowired
    private ParticipantRepository participantRepository;
    
    @GetMapping("/getParticipants/{project}/{study}")
	public List<Participant> getParticipantList(@PathVariable String project, @PathVariable String study) throws Exception {
		return participantRepository.getParticipantList();
	}
    
    @GetMapping("/getNewId")
	public Integer getNewId() throws Exception {
		return participantRepository.getNewId();
	}
    
    @GetMapping("/getProfiles")
	public List<Profile> getProfiles() throws Exception {
		return participantRepository.getProfiles();
	}
    
    @PostMapping()
    public Integer saveParticipant(@RequestBody Participant participant) throws Exception {
    	//System.out.println("createParticipant");
        return participantRepository.save(participant);
    }
    
    @DeleteMapping("/deleteParticipant/{id}")
    public void deleteParticipant(@PathVariable Integer id) throws Exception {
    	//System.out.println("createParticipant");
        participantRepository.deleteParticipant(id);
    }
    
    @DeleteMapping("/deleteAllParticipant/")
    public void deleteAllParticipants() throws Exception {
    	//System.out.println("createParticipant");
        participantRepository.deleteAll();
    }

}