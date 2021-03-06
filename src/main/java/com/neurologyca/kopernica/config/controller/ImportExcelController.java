package com.neurologyca.kopernica.config.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neurologyca.kopernica.config.model.Participant;
import com.neurologyca.kopernica.config.model.Question;
import com.neurologyca.kopernica.config.model.Stimulus;
import com.neurologyca.kopernica.config.repository.GroupRepository;
import com.neurologyca.kopernica.config.repository.ParticipantRepository;
import com.neurologyca.kopernica.config.repository.QuestionRepository;
import com.neurologyca.kopernica.config.repository.StimulusRepository;
import com.neurologyca.kopernica.config.repository.StudyRepository;

@RestController
@RequestMapping("import-excel")
public class ImportExcelController {
		
    @Autowired
    private ParticipantRepository participantRepository;
    
    @Autowired
    private QuestionRepository questionRepository;
    
    @Autowired
    private StimulusRepository stimulusRepository;
    
    @Autowired
    private StudyRepository studyRepository;
    
    @Autowired
    private GroupRepository groupRepository;
    
	static final String GROUP_INTEVIEW = "0";
	static final String INDIVIDUAL_INTERVIEW = "1";
    
	@PostMapping("/participants/{project}/{study}")
    public ResponseEntity<List<Participant>> importParticipantExcelFile(@PathVariable("project") String project, @PathVariable("study") String study) throws Exception {
	//public ResponseEntity<List<Participant>> importParticipantExcelFile(@RequestParam("file") MultipartFile files) throws Exception {	
		HttpStatus status = HttpStatus.OK;
        List<Participant> participantList = new ArrayList<>();
        Integer numParticipantes;
        
        final File file = new File(AppController.fullBasePath + System.getProperty("file.separator") + project + System.getProperty("file.separator") + study + "\\Participantes.xlsx");
        final InputStream targetStream = new FileInputStream(file);
        XSSFWorkbook workbook = new XSSFWorkbook(targetStream);
        XSSFSheet worksheet = workbook.getSheetAt(0);

        numParticipantes = participantRepository.deleteAll();
        
        System.out.println("Borrados " + numParticipantes);
        
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
            
                if (studyRepository.getTypeStudy().equals(GROUP_INTEVIEW)) {
                	participant.setGroup((String) row.getCell(6).getStringCellValue());
                	participant.setGroupId(groupRepository.getGroupId((String) row.getCell(6).getStringCellValue()));
                }

                //participant.toString();
                //System.out.println("Importando participante " + participant.getName());
                participantRepository.importParticipant(participant);
                
                participantList.add(participant);
                
            }
        }
        
        workbook.close();
        targetStream.close();
        return new ResponseEntity<>(participantList, status);
    }
	
	@PostMapping("/stimuli/{project}/{study}")
    public ResponseEntity<List<Stimulus>> importStimulusExcelFile(@PathVariable("project") String project, @PathVariable("study") String study) throws Exception {
        HttpStatus status = HttpStatus.OK;
        List<Stimulus> stimulusList = new ArrayList<>();

        final File file = new File(AppController.fullBasePath + System.getProperty("file.separator") + project + System.getProperty("file.separator") + study + "\\Estimulos.xlsx");
        final InputStream targetStream = new FileInputStream(file);
        XSSFWorkbook workbook = new XSSFWorkbook(targetStream);
        XSSFSheet worksheet = workbook.getSheetAt(0);
        
        stimulusRepository.deleteAll();
        
        for (int index = 0; index < worksheet.getPhysicalNumberOfRows(); index++) {
            if (index > 0) {
                Stimulus stimulus = new Stimulus();
                XSSFRow row = worksheet.getRow(index);
                stimulus.setId((int) row.getCell(0).getNumericCellValue());              
                stimulus.setName((String) row.getCell(1).getStringCellValue());

                stimulusRepository.save(stimulus);
                
                stimulusList.add(stimulus);

            }
        }

        workbook.close();
        return new ResponseEntity<>(stimulusList, status);
    }
	
	@PostMapping("/questions/{project}/{study}")
    public ResponseEntity<List<Question>> importQuestionExcelFile(@PathVariable("project") String project, @PathVariable("study") String study) throws Exception {
		HttpStatus status = HttpStatus.OK;
        List<Question> questionList = new ArrayList<>();
        
        final File file = new File(AppController.fullBasePath + System.getProperty("file.separator") + project + System.getProperty("file.separator") + study + "\\Preguntas.xlsx");
        final InputStream targetStream = new FileInputStream(file);
        XSSFWorkbook workbook = new XSSFWorkbook(targetStream);
        XSSFSheet worksheet = workbook.getSheetAt(0);
        
        questionRepository.deleteAll();

        for (int index = 0; index < worksheet.getPhysicalNumberOfRows(); index++) {
            if (index > 0) {
            	Question question = new Question();
                XSSFRow row = worksheet.getRow(index);
                question.setId((int) row.getCell(0).getNumericCellValue());              
                question.setQuestion((String) row.getCell(1).getStringCellValue());

                questionRepository.save(question);
                
                questionList.add(question);

            }
        }
        
        workbook.close();
        return new ResponseEntity<>(questionList, status);
    }
}