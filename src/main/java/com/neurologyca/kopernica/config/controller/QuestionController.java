package com.neurologyca.kopernica.config.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
    
	@GetMapping()
	public List<Question> getQuestionList() throws Exception {
		return questionRepository.getQuestionList();
	}
	
    @PostMapping()
    public Integer createQuestion(@RequestBody Question question) throws Exception {
        return questionRepository.save(question);
    }

}