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
import com.neurologyca.kopernica.config.repository.QuestionRepository;

@RestController
@RequestMapping("questions")
public class QuestionController {
    @Autowired
    private QuestionRepository questionRepository;
    
	@GetMapping("/getQuestions/{project}/{study}")
	public List<Question> getQuestionList(@PathVariable String project, @PathVariable String study) throws Exception {
		return questionRepository.getQuestionList();
	}
	
    @GetMapping("/getNewId")
	public Integer getNewId() throws Exception {
		return questionRepository.getNewId();
	}
	   
    @PostMapping()
    public Integer saveQuestion(@RequestBody Question question) throws Exception {
        return questionRepository.save(question);
    }
    
    @DeleteMapping("/deleteQuestion/{id}")
    public void deleteQuestion(@PathVariable Integer id) throws Exception {
        questionRepository.deleteQuestion(id);
    }
    
    @DeleteMapping("/deleteAllQuestion/")
    public void deleteAllQuestions() throws Exception {
        questionRepository.deleteAll();
    }

}