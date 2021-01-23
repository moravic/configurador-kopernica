package com.neurologyca.kopernica.config.repository;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.stereotype.Repository;

import com.neurologyca.kopernica.config.controller.AppController;
import com.neurologyca.kopernica.config.model.Stimulus;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


@Repository
public class StimulusRepository {
	
    private void createTableStimulus(Connection conn) throws Exception{
		String createTableQuery = "CREATE TABLE IF NOT EXISTS stimulus ("
				+ "id integer PRIMARY KEY, name TEXT NOT NULL, "
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
   	 String selectMaxIdSql = "SELECT MAX(id) id FROM stimulus";
   	 ResultSet rs;
   	 
        try (PreparedStatement pstmt = conn.prepareStatement(selectMaxIdSql)) {
        	Statement stmt = conn.createStatement();
        	rs = stmt.executeQuery(selectMaxIdSql);
        } catch (SQLException e) {
       	 throw new Exception(e.getMessage());
        }
        return rs.getInt("id");
   }
    
	public Integer getNewId() throws Exception{
		
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
            return selectMaxId(conn)+1;
		} catch (SQLException e) {
       	 throw new Exception(e.getMessage());
        }
	}
	
    private void insertStimulus(Connection conn, Stimulus stimulus) throws Exception {
    	 String insertSql = "INSERT OR REPLACE INTO stimulus(id, name, study_id) "
    	 		+ "VALUES(?,?,1)";
    	 
         try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {      	 
        	 if (stimulus.getId()==0) {
        		 stimulus.setId(selectMaxId(conn)+1);
        	 }
       	 
             pstmt.setInt(1, stimulus.getId());
             pstmt.setString(2, stimulus.getName());
             pstmt.executeUpdate();
         } catch (SQLException e) {
        	 throw new Exception(e.getMessage());
         }
    }
	
	public Integer save(Stimulus stimulus) throws Exception{
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
            
			createTableStimulus(conn);
			insertStimulus(conn, stimulus);

        } catch (SQLException e) {
        	throw new Exception(e.getMessage());
        }

		return stimulus.getId();
	}
	
	public List<Stimulus> getStimulusList() throws Exception{
	    String getStimulusSql = "SELECT id, name FROM stimulus";
	    Stimulus stimulus;
	    List<Stimulus> stimulusList = new ArrayList<Stimulus>();
	    
	    
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
            
            PreparedStatement pstmt = conn.prepareStatement(getStimulusSql);

            ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				stimulus = new Stimulus();
				stimulus.setId(rs.getInt("id"));
				stimulus.setName(rs.getString("name"));

				stimulusList.add(stimulus);
			}

        } catch (SQLException e) {
        	throw new Exception(e.getMessage());
        }
		
		return stimulusList;
		
	}
	
	public void deleteAll() throws Exception{
	    String deleteAllSql = "DELETE FROM stimulus";
	    
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
	
	public void deleteStimulus(Integer id) throws Exception{
	    String deleteSql = "DELETE FROM stimulus where id=" + id;
	    
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
            
            PreparedStatement pstmt = conn.prepareStatement(deleteSql);
            pstmt.executeUpdate();

        } catch (SQLException e) {
        	throw new Exception(e.getMessage());
        }
		
	}
}