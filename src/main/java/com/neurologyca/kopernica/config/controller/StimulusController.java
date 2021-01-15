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

import com.neurologyca.kopernica.config.model.Question;
import com.neurologyca.kopernica.config.model.Stimulus;
import com.neurologyca.kopernica.config.repository.StimulusRepository;

@RestController
@RequestMapping("stimuli")
public class StimulusController {
    @Autowired
    private StimulusRepository stimulusRepository;
    
    
    @GetMapping("/getStimuli/{project}/{study}")
	public List<Stimulus> getStimuliList(@PathVariable String project, @PathVariable String study) throws Exception {
		return stimulusRepository.getStimulusList();
	}
	   
    @PostMapping()
    public Integer saveStimulus(@RequestBody Stimulus stimulus) throws Exception {
        return stimulusRepository.save(stimulus);
    }
    
    @DeleteMapping("/deleteStimulus/{id}")
    public void deleteStimulus(@PathVariable Integer id) throws Exception {
    	stimulusRepository.deleteStimulus(id);
    }
    
    @DeleteMapping("/deleteAllStimulus/")
    public void deleteAllStimulus() throws Exception {
    	stimulusRepository.deleteAll();
    }


}