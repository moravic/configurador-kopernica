package com.neurologyca.kopernica.config.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.neurologyca.kopernica.config.model.Participant;
import com.neurologyca.kopernica.config.model.Stimulus;
import com.neurologyca.kopernica.config.model.Question;

@RestController
@RequestMapping("import-excel")
public class ImportExcelController {
	 
	@PostMapping("/participants")
    public ResponseEntity<List<Participant>> importParticipantExcelFile(@RequestParam("file") MultipartFile files) throws Exception {
        HttpStatus status = HttpStatus.OK;
        List<Participant> participantList = new ArrayList<>();

        XSSFWorkbook workbook = new XSSFWorkbook(files.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);

        for (int index = 0; index < worksheet.getPhysicalNumberOfRows(); index++) {
            if (index > 0) {
                Participant participant = new Participant();
                XSSFRow row = worksheet.getRow(index);
                participant.setId((int) row.getCell(0).getNumericCellValue());              
                participant.setName((String) row.getCell(1).getStringCellValue());
                participant.setGender((String) row.getCell(2).getStringCellValue());
                participant.setAge((int) row.getCell(3).getNumericCellValue());
                participant.setProfile((String) row.getCell(4).getStringCellValue());
                participant.setEmail((String) row.getCell(5).getStringCellValue());

                //participant.toString();
                
                participantList.add(participant);

            }
        }

         return new ResponseEntity<>(participantList, status);
    }
	
	@PostMapping("/stimuli")
    public ResponseEntity<List<Stimulus>> importStimulusExcelFile(@RequestParam("file") MultipartFile files) throws Exception {
        HttpStatus status = HttpStatus.OK;
        List<Stimulus> stimulusList = new ArrayList<>();

        XSSFWorkbook workbook = new XSSFWorkbook(files.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);

        for (int index = 0; index < worksheet.getPhysicalNumberOfRows(); index++) {
            if (index > 0) {
                Stimulus stimulus = new Stimulus();
                XSSFRow row = worksheet.getRow(index);
                stimulus.setId((int) row.getCell(0).getNumericCellValue());              
                stimulus.setName((String) row.getCell(1).getStringCellValue());

                //stimulus.toString();
                
                stimulusList.add(stimulus);

            }
        }

         return new ResponseEntity<>(stimulusList, status);
    }
	
	@PostMapping("/questions")
    public ResponseEntity<List<Question>> importQuestionExcelFile(@RequestParam("file") MultipartFile files) throws Exception {
        HttpStatus status = HttpStatus.OK;
        List<Question> questionList = new ArrayList<>();

        XSSFWorkbook workbook = new XSSFWorkbook(files.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);

        for (int index = 0; index < worksheet.getPhysicalNumberOfRows(); index++) {
            if (index > 0) {
            	Question question = new Question();
                XSSFRow row = worksheet.getRow(index);
                question.setId((int) row.getCell(0).getNumericCellValue());              
                question.setQuestion((String) row.getCell(1).getStringCellValue());

                //stimulus.toString();
                
                questionList.add(question);

            }
        }

         return new ResponseEntity<>(questionList, status);
    }
}