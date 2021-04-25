package com.neurologyca.kopernica.config.controller;

import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    ReentrantLock lock = new ReentrantLock();
    
	@GetMapping("/getStudy/{project}/{study}")
	public Study getTypeStudy(@PathVariable String project, @PathVariable String study) throws Exception {
		return studyRepository.getStudy();
	}
	
    @PostMapping("/addStudy")
    public Integer createStudy(@RequestBody Study study) throws Exception {
    	System.out.println("createStudy");
        return studyRepository.createStudy(study);
    }
    
    @PostMapping("/saveStudy")
    public Integer save(@RequestBody Study study) throws Exception {
		System.out.println("saveStudy");
		 lock.lock();
		 try {
			return studyRepository.saveStudy(study);
		 }finally {
		     lock.unlock();
		 }
    }

}