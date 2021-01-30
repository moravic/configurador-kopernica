package com.neurologyca.kopernica.config.controller;

import java.io.FileOutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neurologyca.kopernica.config.model.Participant;
import com.neurologyca.kopernica.config.model.Stimulus;
import com.neurologyca.kopernica.config.repository.ParticipantRepository;
import com.neurologyca.kopernica.config.repository.QuestionRepository;
import com.neurologyca.kopernica.config.repository.StimulusRepository;
import com.neurologyca.kopernica.config.repository.StudyRepository;
import com.neurologyca.kopernica.config.model.Question;

@RestController
@RequestMapping("export-excel")
public class ExportExcelController {
	
	@Value("${base.path}")
	private String basePath;
	
	@Autowired
    private StudyRepository studyRepository;
	
    @Autowired
    private ParticipantRepository participantRepository;
    
    @Autowired
    private QuestionRepository questionRepository;
    
    @Autowired
    private StimulusRepository stimulusRepository;
    
	static final String GROUP_INTEVIEW = "0";
	static final String INDIVIDUAL_INTERVIEW = "1";
    
	@PostMapping("/participants/{project}/{study}")
    public void exportParticipantExcelFile(@PathVariable String project, @PathVariable String study) throws Exception {
	    String[] columns = {"Id", "Nombre", "Género", "Edad", "Perfil", "Email", "Grupo"};
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet worksheet = workbook.createSheet("Participantes");

     // Crea fila
        Row headerRow = worksheet.createRow(0);
     // Escribe cabecera
        for(int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }     
      //Escribe valores
        int index=1;
        List<Participant> participantList = participantRepository.getParticipantList();
        for (Participant participant: participantList) {	
                Row row = worksheet.createRow(index++);     
                row.createCell(0).setCellValue(participant.getId());
                row.createCell(1).setCellValue(participant.getName());
                row.createCell(2).setCellValue(participant.getGender());
                row.createCell(3).setCellValue(participant.getAge());
                row.createCell(4).setCellValue(participant.getProfile());
                row.createCell(5).setCellValue(participant.getEmail());
                
                if (studyRepository.getTypeStudy().equals(GROUP_INTEVIEW)) {
                	row.createCell(6).setCellValue(participant.getGroup());
                }
        }
		// Resize de todas las columnas
        for(int i = 0; i < columns.length; i++) {
        	worksheet.autoSizeColumn(i);
        }   
      // Escribe el fichero de salida
        FileOutputStream fileOut = new FileOutputStream(basePath + "\\" + project + "\\" + study + "\\" + "Participantes.xlsx");
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
    }
	
	@PostMapping("/questions/{project}/{study}")
    public void exportQuestionExcelFile(@PathVariable String project, @PathVariable String study) throws Exception {
	    String[] columns = {"Id", "Pregunta"};
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet worksheet = workbook.createSheet("Preguntas");

     // Crea fila
        Row headerRow = worksheet.createRow(0);
     // Escribe cabecera
        for(int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }     
      //Escribe valores
        int index=1;
        List<Question> questionList = questionRepository.getQuestionList();
        for (Question question: questionList) {	
                Row row = worksheet.createRow(index++);      
                row.createCell(0).setCellValue(question.getId());
                row.createCell(1).setCellValue(question.getQuestion());
        }
		// Resize de todas las columnas
        for(int i = 0; i < columns.length; i++) {
        	worksheet.autoSizeColumn(i);
        }   
      // Escribe el fichero de salida
        FileOutputStream fileOut = new FileOutputStream(basePath + "\\" + project + "\\" + study + "\\" + "Preguntas.xlsx");
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
    }
	
	@PostMapping("/stimuli/{project}/{study}")
    public void exportStimulusExcelFile(@PathVariable String project, @PathVariable String study) throws Exception {
	    String[] columns = {"Id", "Estimulo"};
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet worksheet = workbook.createSheet("Estimulos");

     // Crea fila
        Row headerRow = worksheet.createRow(0);
     // Escribe cabecera
        for(int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }     
      //Escribe valores
        int index=1;
        List<Stimulus> stimulusList = stimulusRepository.getStimulusList();
        for (Stimulus stimulus: stimulusList) {	
                Row row = worksheet.createRow(index++);      
                row.createCell(0).setCellValue(stimulus.getId());
                row.createCell(1).setCellValue(stimulus.getName());
        }
		// Resize de todas las columnas
        for(int i = 0; i < columns.length; i++) {
        	worksheet.autoSizeColumn(i);
        }   

        // Escribe el fichero de salida
        FileOutputStream fileOut = new FileOutputStream(basePath + "\\" + project + "\\" + study + "\\" + "Estimulos.xlsx");
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
    }
	
}