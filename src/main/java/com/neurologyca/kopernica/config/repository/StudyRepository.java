package com.neurologyca.kopernica.config.repository;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.neurologyca.kopernica.config.controller.AppController;
import com.neurologyca.kopernica.config.model.Study;

import java.sql.Statement;

@Repository
public class StudyRepository {
	
	@Autowired
	private ProtocolRepository protocolRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private StimulusRepository stimulusRepository;
    
	private void createFolders(String project, String study) throws Exception {
		 
		String subFolder = AppController.fullBasePath + System.getProperty("file.separator") + project + System.getProperty("file.separator") + study + System.getProperty("file.separator") + "csvs" + System.getProperty("file.separator");
		String subFolderFaces = AppController.fullBasePath + System.getProperty("file.separator") + project + System.getProperty("file.separator") + study + System.getProperty("file.separator") + "faces" + System.getProperty("file.separator");
		String subFolderAudios = AppController.fullBasePath + System.getProperty("file.separator") + project + System.getProperty("file.separator") + study + System.getProperty("file.separator") + "audios" + System.getProperty("file.separator");
		
		File projectFolder = new File(subFolder);
    		
    	if (projectFolder.mkdirs()) {
    		   System.out.println("Se ha creado el directorio del proyecto.");
    	} else if (projectFolder.isDirectory()) {
    		 System.out.println("Ya existe el proyecto " + project);	
    	} else {
    		    throw new Exception("No se ha podido crear el directorio del proyecto " + project);
    	}	
    	
    	projectFolder = new File(subFolderFaces);
    	if (projectFolder.mkdirs()) {
 		   System.out.println("Se ha creado el directorio \"faces\" del proyecto.");
    	} else if (projectFolder.isDirectory()) {
 		 System.out.println("Ya existe la carpeta \"faces\" proyecto " + project);	
    	} else {
 		    throw new Exception("No se ha podido crear el directorio \"faces\" del proyecto " + project);
    	}	
    	
    	projectFolder = new File(subFolderAudios);
    	if (projectFolder.mkdirs()) {
 		   System.out.println("Se ha creado el directorio \"audios\" del proyecto.");
    	} else if (projectFolder.isDirectory()) {
 		 System.out.println("Ya existe la carpeta \"audios\" del proyecto " + project);	
    	} else {
 		    throw new Exception("No se ha podido crear el directorio \"audios\" del proyecto " + project);
    	}	
	}
	
    private void createTableStudies(Connection conn) throws Exception{
		String createTableQuery = "CREATE TABLE IF NOT EXISTS studies (id integer PRIMARY KEY, project TEXT NOT NULL, study TEXT NOT NULL, type TEXT NOT NULL, initDate DATE, endDate DATE)";
		
		try {
			Statement stmt = conn.createStatement();
			stmt.execute(createTableQuery);	
		} catch (SQLException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		 }
	 }
    
    private void saveStudies(Connection conn, Study study) throws Exception {
    	 String insertSql = "INSERT OR REPLACE INTO studies(id, project, study, type, initDate, endDate) VALUES(1,?,?,?,?,?)";
    	 
    	 createTableStudies(conn);
    	 
         try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
             pstmt.setString(1, study.getProject());
             pstmt.setString(2, study.getStudy());
             pstmt.setString(3, study.getType());
             pstmt.setDate(4, study.getInitDate());
             pstmt.setDate(5, study.getEndDate());
             pstmt.executeUpdate();
         } catch (SQLException e) {
        	 throw new Exception(e.getMessage());
         }
    }
	
    public Study getStudy() throws Exception{
    	String getStudy = "SELECT type, initDate, endDate FROM studies";

		if (AppController.fullDatabaseUrl==null) {
			throw new Exception("Debe estar seleccionado un proyecto y un estudio");
		}
		try (Connection conn = DriverManager.getConnection(AppController.fullDatabaseUrl)) {
            if (conn != null) {
            	// Si no existe se crea la bbdd
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                //System.out.println("A new database has been created.");
            }
           
            PreparedStatement pstmt = conn.prepareStatement(getStudy);

            ResultSet rs = pstmt.executeQuery();

			rs.next();
			
			Study study = new Study();
	        
			study.setType(rs.getString("type"));
			study.setInitDate(rs.getDate("initDate"));
			study.setEndDate(rs.getDate("endDate"));
			
			return study;

        } catch (SQLException e) {
        	throw new Exception(e.getMessage());
        }
		
    }
    
	public Integer createStudy(Study study) throws Exception{
		
		if (AppController.fullDatabaseUrl==null) {
			throw new Exception("Debe estar seleccionado un proyecto y un estudio");
		}
		
		createFolders(study.getProject(), study.getStudy());
		
		try (Connection conn = DriverManager.getConnection(AppController.fullDatabaseUrl)) {
            if (conn != null) {
            	// Si no existe se crea la bbdd
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                //System.out.println("A new database has been created.");
            }
            
            ParticipantRepository participantRepository = new ParticipantRepository();         
            participantRepository.createTableParticipants(conn);           
			questionRepository.createTableQuestions(conn);			
			stimulusRepository.createTableStimulus(conn);		
			protocolRepository.createProtocolTables(conn);		
			saveStudies(conn, study);
		

        } catch (SQLException e) {
        	throw new Exception(e.getMessage());
        }
		
		

		return 1;
	}
	
	public Integer saveStudy(Study study) throws Exception{
		
		if (AppController.fullDatabaseUrl==null) {
			throw new Exception("Debe estar seleccionado un proyecto y un estudio");
		}
		
		try (Connection conn = DriverManager.getConnection(AppController.fullDatabaseUrl)) {
            if (conn != null) {
            	// Si no existe se crea la bbdd
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                //System.out.println("A new database has been created.");
            }
            		
			saveStudies(conn, study);
		
        } catch (SQLException e) {
        	throw new Exception(e.getMessage());
        }
		
		return 1;
	}
}