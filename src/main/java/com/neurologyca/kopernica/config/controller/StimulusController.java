package com.neurologyca.kopernica.config.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neurologyca.kopernica.config.model.Stimulus;
import com.neurologyca.kopernica.config.repository.StimulusRepository;

@RestController
@RequestMapping("stimuli")
public class StimulusController {
    @Autowired
    private StimulusRepository stimulusRepository;
    
	@GetMapping()
	public List<Stimulus> getStimulusList() throws Exception {
		return stimulusRepository.getStimulusList();
	}
	
    @PostMapping()
    public Integer createStimulus(@RequestBody Stimulus stimulus) throws Exception {
    	//System.out.println("createParticipant");
        return stimulusRepository.save(stimulus);
    }

}