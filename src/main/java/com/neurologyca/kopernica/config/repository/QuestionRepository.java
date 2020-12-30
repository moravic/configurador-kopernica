package com.neurologyca.kopernica.config.repository;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.stereotype.Repository;

import com.neurologyca.kopernica.config.controller.AppController;
import com.neurologyca.kopernica.config.model.Question;

import java.sql.Statement;


@Repository
public class QuestionRepository {
	
    private void createTableQuestions(Connection conn) throws Exception{
		String createTableQuery = "CREATE TABLE IF NOT EXISTS questions ("
				+ "id integer PRIMARY KEY, question TEXT NOT NULL, "
				+ "study_id integer NOT NULL, "
				+ "FOREIGN KEY(study_id) REFERENCES studies(id))";
		
		try {
			Statement stmt = conn.createStatement();
			stmt.execute(createTableQuery);	
		} catch (SQLException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		 }
	 }

    private Integer selectMaxId(Connection conn) throws Exception {
   	 String selectMaxIdSql = "SELECT MAX(id) id FROM questions";
   	 ResultSet rs;
   	 
        try (PreparedStatement pstmt = conn.prepareStatement(selectMaxIdSql)) {
        	Statement stmt = conn.createStatement();
        	rs = stmt.executeQuery(selectMaxIdSql);
        } catch (SQLException e) {
       	 throw new Exception(e.getMessage());
        }
        return rs.getInt("id");
   }
	
    private void insertQuestion(Connection conn, Question question) throws Exception {
    	 String insertSql = "INSERT OR REPLACE INTO questions(id, question, study_id) "
    	 		+ "VALUES(?,?,1)";
    	 
         try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {      	 
        	 if (question.getId()==0) {
        		 question.setId(selectMaxId(conn)+1);
        	 }
       	 
             pstmt.setInt(1, question.getId());
             pstmt.setString(2, question.getQuestion());
             pstmt.executeUpdate();
         } catch (SQLException e) {
        	 throw new Exception(e.getMessage());
         }
    }
	
	public Integer save(Question question) throws Exception{
		if (AppController.fullDatabaseUrl==null) {
			throw new Exception("Debe estar seleccionado un proyecto y un estudio");
		}
		try (Connection conn = DriverManager.getConnection(AppController.fullDatabaseUrl)) {
            if (conn != null) {
            	// Si no existe se crea la bbdd
                DatabaseMetaData meta = conn.getMetaData();
                //System.out.println("The driver name is " + meta.getDriverName());
                //System.out.println("A new database has been created.");
            }
            
			createTableQuestions(conn);
			insertQuestion(conn, question);

        } catch (SQLException e) {
        	throw new Exception(e.getMessage());
        }

		return question.getId();
	}
}