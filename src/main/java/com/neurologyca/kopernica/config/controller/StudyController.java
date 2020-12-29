package com.neurologyca.kopernica.config.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neurologyca.kopernica.config.model.Study;
import com.neurologyca.kopernica.config.repository.StudyRepository;

@RestController //@CrossOrigin(origins = "http://localhost:8080/config-kopernica")
@RequestMapping("studies")
public class StudyController {
    @Autowired
    private StudyRepository studyRepository;

    
    @PostMapping()
    public Integer createStudy(@RequestBody Study study) throws Exception {
    	//System.out.println("createStudy");
        return studyRepository.save(study);
    }

}