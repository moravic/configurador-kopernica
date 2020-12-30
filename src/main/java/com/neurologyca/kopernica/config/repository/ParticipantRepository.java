package com.neurologyca.kopernica.config.repository;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.stereotype.Repository;

import com.neurologyca.kopernica.config.controller.AppController;
import com.neurologyca.kopernica.config.model.Participant;

import java.sql.Statement;


@Repository
public class ParticipantRepository {
	
    private void createTableParticipants(Connection conn) throws Exception{
		String createTableQuery = "CREATE TABLE IF NOT EXISTS participants ("
				+ "id integer PRIMARY KEY, name TEXT NOT NULL, gender TEXT NOT NULL, age integer NOT NULL, "
				+ "type TEXT NOT NULL, email TEXT, study_id integer NOT NULL, "
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
      	 String selectMaxIdSql = "SELECT MAX(id) id FROM participants";
      	 ResultSet rs;
      	 
           try (PreparedStatement pstmt = conn.prepareStatement(selectMaxIdSql)) {
           	Statement stmt = conn.createStatement();
           	rs = stmt.executeQuery(selectMaxIdSql);
           } catch (SQLException e) {
          	 throw new Exception(e.getMessage());
           }
           return rs.getInt("id");
      }
    
    private void insertParticipant(Connection conn, Participant participant) throws Exception {
    	 String insertSql = "INSERT OR REPLACE INTO participants(id, name, gender, age, type, email, study_id) "
    	 		+ "VALUES(?,?,?,?,?,?,1)";
    	 
         try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
        	 if (participant.getId()==0) {
        		 participant.setId(selectMaxId(conn)+1);
        	 }        	 
             pstmt.setInt(1, participant.getId());
             pstmt.setString(2, participant.getName());
             pstmt.setString(3, participant.getGender());
             pstmt.setInt(4, participant.getAge());
             pstmt.setString(5, participant.getType());
             pstmt.setString(6, participant.getEmail());
             pstmt.executeUpdate();
         } catch (SQLException e) {
        	 throw new Exception(e.getMessage());
         }
    }
	
	public Integer save(Participant participant) throws Exception{
		
		try (Connection conn = DriverManager.getConnection(AppController.fullDatabaseUrl)) {
            if (conn != null) {
            	// Si no existe se crea la bbdd
                DatabaseMetaData meta = conn.getMetaData();
                //System.out.println("The driver name is " + meta.getDriverName());
                //System.out.println("A new database has been created.");
            }
            
			createTableParticipants(conn);
			insertParticipant(conn, participant);

        } catch (SQLException e) {
        	throw new Exception(e.getMessage());
        }
		
		

		return participant.getId();
	}
}