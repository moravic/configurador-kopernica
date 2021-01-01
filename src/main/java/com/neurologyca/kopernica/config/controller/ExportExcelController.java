package com.neurologyca.kopernica.config.controller;

import java.io.FileOutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neurologyca.kopernica.config.model.Participant;
import com.neurologyca.kopernica.config.model.Stimulus;
import com.neurologyca.kopernica.config.model.Question;

@RestController
@RequestMapping("export-excel")
public class ExportExcelController {
	 
	@PostMapping("/participants")
    public void exportParticipantExcelFile(@RequestBody List<Participant> participantList) throws Exception {
	    String[] columns = {"Id", "Nombre", "Género", "Edad", "Tipo", "Email"};
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
        for (Participant participant: participantList) {	
                Row row = worksheet.createRow(index++);     
                row.createCell(0).setCellValue(participant.getId());
                row.createCell(1).setCellValue(participant.getName());
                row.createCell(2).setCellValue(participant.getGender());
                row.createCell(3).setCellValue(participant.getAge());
                row.createCell(4).setCellValue(participant.getType());
                row.createCell(5).setCellValue(participant.getEmail());
        }
		// Resize de todas las columnas
        for(int i = 0; i < columns.length; i++) {
        	worksheet.autoSizeColumn(i);
        }   
      // Escribe el fichero de salida
        FileOutputStream fileOut = new FileOutputStream("Participantes.xlsx");
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
    }
	
	@PostMapping("/questions")
    public void exportQuestionExcelFile(@RequestBody List<Question> questionList) throws Exception {
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
        FileOutputStream fileOut = new FileOutputStream("Preguntas.xlsx");
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
    }
	
	@PostMapping("/stimuli")
    public void exportStimulusExcelFile(@RequestBody List<Stimulus> stimulusList) throws Exception {
	    String[] columns = {"Id", "Nombre"};
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
        FileOutputStream fileOut = new FileOutputStream("Estimulos.xlsx");
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
    }
	
}