package com.neurologyca.kopernica.config.repository;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.neurologyca.kopernica.config.model.Study;

import java.sql.Statement;


@Repository
public class StudyRepository {

	@Value("${database.url}")
	private String databaseUrl;
	
	@Value("${base.path}")
	private String basePath;
	
	private void createFolders(String project, String study) throws Exception {
		 
		String subFolder = basePath + "\\" + project + "\\" + study + "\\csvs";
		File projectFolder = new File(subFolder);
    		
    	if (projectFolder.mkdirs()) {
    		   System.out.println("Se ha creado el directorio del proyecto.");
    	} else if (projectFolder.isDirectory()) {
    		 System.out.println("Ya existe el proyecto " + project);	
    	} else {
    		    throw new Exception("No se ha podido crear el directorio del proyecto " + project);
    	}			
	}
    private void createTableStudies(Connection conn) throws Exception{
		String createTableQuery = "CREATE TABLE IF NOT EXISTS studies (id integer PRIMARY KEY, project TEXT NOT NULL, study TEXT NOT NULL, type TEXT NOT NULL)";
		
		try {
			Statement stmt = conn.createStatement();
			stmt.execute(createTableQuery);	
		} catch (SQLException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		 }
	 }
    
    private void insertStudies(Connection conn, Study study) throws Exception {
    	 String insertSql = "INSERT OR REPLACE INTO studies(id, project, study, type) VALUES(1,?,?,?)";
    	 
         try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
             pstmt.setString(1, study.getProject());
             pstmt.setString(2, study.getStudy());
             pstmt.setString(3, study.getType());
             pstmt.executeUpdate();
         } catch (SQLException e) {
             System.out.println(e.getMessage());
         }
    }
	
	public Integer save(Study study) throws Exception{
		String fullDatabaseUrl = databaseUrl + study.getProject() + "\\" + study.getStudy() + "\\csvs\\database";
		
		createFolders(study.getProject(), study.getStudy());
		
		try (Connection conn = DriverManager.getConnection(fullDatabaseUrl)) {
            if (conn != null) {
            	// Si no existe se crea la bbdd
                DatabaseMetaData meta = conn.getMetaData();
                //System.out.println("The driver name is " + meta.getDriverName());
                //System.out.println("A new database has been created.");
            }
            
			createTableStudies(conn);
			insertStudies(conn, study);

        } catch (SQLException e) {
        	throw new Exception(e.getMessage());
        }
		
		

		return 1;
	}
}