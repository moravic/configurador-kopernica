package com.neurologyca.kopernica.config.repository;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.neurologyca.kopernica.config.controller.AppController;
import com.neurologyca.kopernica.config.model.Participant;
import com.neurologyca.kopernica.config.model.Profile;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


@Repository
public class ParticipantRepository {
	
	@Autowired
	private StudyRepository studyRepository;
	
	@Autowired
	private GroupRepository groupRepository;
	
	static final String GROUP_INTEVIEW = "0";
	static final String INDIVIDUAL_INTERVIEW = "1";
	
    public void createTableParticipants(Connection conn) throws Exception{
		String createTableQuery = "CREATE TABLE IF NOT EXISTS participants ("
				+ "id integer PRIMARY KEY, name TEXT NOT NULL, gender TEXT NOT NULL, age integer NOT NULL, "
				+ "profile TEXT NOT NULL, email TEXT, group_id integer, study_id integer NOT NULL, locked INT NULL, "
				+ "FOREIGN KEY(study_id) REFERENCES studies(id), FOREIGN KEY(group_id) REFERENCES groups(id))";
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
   
	public Integer getNewId() throws Exception{
			try {
	            Participant participant = new Participant(0, "", "", 0, "", "", 0, "");
	            save(participant);
	            return participant.getId();
			} catch (SQLException e) {
	       	 throw new Exception(e.getMessage());
	        }
		}
            
    private Participant insertParticipant(Connection conn, Participant participant) throws Exception {
    	 String insertSql = "INSERT OR REPLACE INTO participants(id, name, gender, age, profile, email, group_id, study_id) "
    	 		+ "VALUES(?,?,?,?,?,?,?,1)";
    	 
    	 Integer beforeGroupId = participant.getGroupId();
    	 // Revisamos si el estudio es grupal
    	 // No es grupal....group_id=0
    	 // Es grupal ... buscar el group_id en grupos (se crea e inserta si no existe)
    	 System.out.println("Tipo Estudio: " + studyRepository.getTypeStudy());
    	 if (studyRepository.getTypeStudy().equals(GROUP_INTEVIEW)) {	 	
    		 System.out.println("Id/grupo: " + participant.getGroupId() + " " + participant.getGroup());
    		 participant.setGroupId(groupRepository.getGroupId(participant.getGroupId(), participant.getGroup(), participant.getId()));
    		 System.out.println("Grupo Final: " + participant.getGroupId());
    	 }
    	 else {
    		 participant.setGroupId(0);
    	 }
    	 
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
             pstmt.setInt(7, participant.getGroupId());
             pstmt.executeUpdate();
             
             if (beforeGroupId!=0 && beforeGroupId!=participant.getGroupId()) {
         		if (groupRepository.numCountParticipants(beforeGroupId, participant.getId()) == 0) {
        			deleteGroup(conn, beforeGroupId);
        			System.out.println("Borrar grupo "+ beforeGroupId);
        		}
             }
             
         } catch (SQLException e) {
        	 throw new Exception(e.getMessage());
         }
         

			
         return participant;
    }
	
    public void deleteGroup(Connection conn, Integer groupId) throws Exception{
	    String deleteGroup = "DELETE FROM groups where id = ?";
	    
		if (AppController.fullDatabaseUrl==null) {
			throw new Exception("Debe estar seleccionado un proyecto y un estudio");
		}
        
		try (PreparedStatement pstmt = conn.prepareStatement(deleteGroup)){
	        pstmt.setInt(1, groupId);
	        pstmt.executeUpdate();
        } catch (SQLException e) {
        	throw new Exception(e.getMessage());
        }
		
	}
    
	public Participant save(Participant participant) throws Exception{
		
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
			participant = insertParticipant(conn, participant);

        } catch (SQLException e) {
        	throw new Exception(e.getMessage());
        }
		
		

		return participant;
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
	
	public void deleteParticipant(Integer id) throws Exception{
	    String deleteSql = "DELETE FROM participants where id=" + id;
	    
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
	
	public List<Participant> getParticipantList() throws Exception{
	    String getParticipantsSql = "SELECT id, name, gender, age, profile, email, group_id FROM participants";
	    Participant participant;
	    List<Participant> participantList = new ArrayList<Participant>();
	    StudyRepository studyRepository = new StudyRepository();
	    GroupRepository groupRepository = new GroupRepository();
	          
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
				participant.setGroupId(rs.getInt("group_id"));
				
				if (studyRepository.getTypeStudy().equals(GROUP_INTEVIEW)) {
		    		 participant.setGroup(groupRepository.getGroupName(rs.getInt("group_id")));
		    		 System.out.println("Nombre Grupo: " + participant.getGroup());
		    	 }
		    	 else {
		    		 participant.setGroup("");
		    	 }

				participantList.add(participant);
			}

        } catch (SQLException e) {
        	throw new Exception(e.getMessage());
        }
		
	    return participantList;
		
	}
	
	public List<Profile> getProfiles() throws Exception{
	    String getProfileSql = "select row_number() over (order by profile desc)  id, profile\r\n"
	    		+ "from \r\n"
	    		+ "(SELECT distinct profile FROM participants)";
	    Profile profile;
	    List<Profile> profileList = new ArrayList<Profile>();
	           
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
            
            PreparedStatement pstmt = conn.prepareStatement(getProfileSql);

            ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				profile = new Profile();
				profile.setId(rs.getInt("id"));
				profile.setType(rs.getString("profile"));

				profileList.add(profile);
			}

        } catch (SQLException e) {
        	throw new Exception(e.getMessage());
        }
		
	    return profileList;
		
	}
	
}