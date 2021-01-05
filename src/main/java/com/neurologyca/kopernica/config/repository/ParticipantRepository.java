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
import java.util.ArrayList;
import java.util.List;


@Repository
public class ParticipantRepository {
	
    private void createTableParticipants(Connection conn) throws Exception{
		String createTableQuery = "CREATE TABLE IF NOT EXISTS participants ("
				+ "id integer PRIMARY KEY, name TEXT NOT NULL, gender TEXT NOT NULL, age integer NOT NULL, "
				+ "profile TEXT NOT NULL, email TEXT, study_id integer NOT NULL, "
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
    	 String insertSql = "INSERT OR REPLACE INTO participants(id, name, gender, age, profile, email, study_id) "
    	 		+ "VALUES(?,?,?,?,?,?,1)";
    	 
         try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
        	 if (participant.getId()==0) {
        		 participant.setId(selectMaxId(conn)+1);
        	 }        	 
             pstmt.setInt(1, participant.getId());
             pstmt.setString(2, participant.getName());
             pstmt.setString(3, participant.getGender());
             pstmt.setInt(4, participant.getAge());
             pstmt.setString(5, participant.getProfile());
             pstmt.setString(6, participant.getEmail());
             pstmt.executeUpdate();
         } catch (SQLException e) {
        	 throw new Exception(e.getMessage());
         }
    }
	
	public Integer save(Participant participant) throws Exception{
		
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
            
			createTableParticipants(conn);
			insertParticipant(conn, participant);

        } catch (SQLException e) {
        	throw new Exception(e.getMessage());
        }
		
		

		return participant.getId();
	}
	
	public void deleteAll() throws Exception{
	    String deleteAllSql = "DELETE FROM participants";
	    
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
            
            PreparedStatement pstmt = conn.prepareStatement(deleteAllSql);
            pstmt.executeUpdate();

        } catch (SQLException e) {
        	throw new Exception(e.getMessage());
        }
		
	}
	
	public List<Participant> getParticipantList() throws Exception{
	    String getParticipantsSql = "SELECT id, name, gender, age, profile, email FROM participants";
	    Participant participant;
	    List<Participant> participantList = new ArrayList<Participant>();
	           
		if (AppController.fullDatabaseUrl==null) {
			throw new Exception("Debe estar seleccionado un proyecto y un estudio");
		}
		try (Connection conn = DriverManager.getConnection(AppController.fullDatabaseUrl)) {
            if (conn != null) {
            	// Si no existe se crea la bbdd
                DatabaseMetaData meta = conn.getMetaData();
                createTableParticipants(conn);
                //System.out.println("The driver name is " + meta.getDriverName());
                //System.out.println("A new database has been created.");
            }
            
            PreparedStatement pstmt = conn.prepareStatement(getParticipantsSql);

            ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				participant = new Participant();
				participant.setId(rs.getInt("id"));
				participant.setName(rs.getString("name"));
				participant.setAge(rs.getInt("age"));
				participant.setGender(rs.getString("gender"));
				participant.setEmail(rs.getString("email"));
				participant.setProfile(rs.getString("profile"));

				participantList.add(participant);
			}

        } catch (SQLException e) {
        	throw new Exception(e.getMessage());
        }
		
	    return participantList;
		
	}
}