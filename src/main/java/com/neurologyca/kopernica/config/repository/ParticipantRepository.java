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
	
	static final String PARTICIPANTE_DUPLICADO = "ERROR: Ya existe un participante con el mismo nombre y edad";
	
    public void createTableParticipants(Connection conn) throws Exception{
		String createTableQuery = "CREATE TABLE IF NOT EXISTS participants ("
				+ "id integer PRIMARY KEY, name TEXT NOT NULL, gender TEXT NOT NULL, age integer NOT NULL, "
				+ "profile TEXT NOT NULL, email TEXT, group_id integer, study_id integer NOT NULL, locked INT NULL, "
				+ "FOREIGN KEY(study_id) REFERENCES studies(id), FOREIGN KEY(group_id) REFERENCES groups(id),  UNIQUE(name, age))";
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
    
    
    private Integer existsParticipant(Connection conn, Participant participant) throws Exception {
      	 String selectSql = "SELECT COUNT(1) contador FROM participants WHERE name = ? AND age = ? AND id <> ?";
      	

           try (PreparedStatement pstmt = conn.prepareStatement(selectSql)) {
           	pstmt.setString(1, participant.getName());
           	pstmt.setInt(2, participant.getAge());
           	pstmt.setInt(3, participant.getId());
           	ResultSet rs = pstmt.executeQuery();
           	Integer resultado = rs.getInt("contador");

           	return resultado;
           	
           } catch (SQLException e) {
          	 throw new Exception(e.getMessage());
           }
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
     
	public Integer numBlockedParticipants() throws Exception{
		String selectSql = "SELECT count(1) contador FROM participants WHERE ifnull(locked,0) = 1";
		Integer count = 0;
		
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
                
            PreparedStatement stmt = conn.prepareStatement(selectSql);
            ResultSet rs = stmt.executeQuery();
    	    
            count = rs.getInt("contador");
           } catch (SQLException e) {
            throw new Exception(e.getMessage());
          }
            
		return count;
	}
	
	public Integer numParticipants(Connection conn) throws Exception{
		String selectSql = "SELECT count(1) contador FROM participants";
		Integer count = 0;
		
        try (PreparedStatement stmt = conn.prepareStatement(selectSql)) {
            ResultSet rs = stmt.executeQuery();
    	    
            count = rs.getInt("contador");
           } catch (SQLException e) {
            throw new Exception(e.getMessage());
          }
            
		return count;
	}
	
	public Integer isBlockedParticipant(Integer participantId) throws Exception{
		String selectSql = "SELECT ifnull(locked, 0) bloqueado FROM participants WHERE id = ?";
		Integer bloqueado = 0;
		
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
                
            PreparedStatement stmt = conn.prepareStatement(selectSql);
            stmt.setInt(1, participantId);
            ResultSet rs = stmt.executeQuery();
    	    
            bloqueado = rs.getInt("bloqueado");
           } catch (SQLException e) {
            throw new Exception(e.getMessage());
          }
            
		return bloqueado;
	}
	
	public Integer isBlockedParticipant(Connection conn, String participantName, Integer participantAge) throws Exception{
		String selectSql = "SELECT ifnull(locked, 0) bloqueado FROM participants WHERE name = ? AND age = ?";
		Integer bloqueado = 0;
		

      try {     
            PreparedStatement stmt = conn.prepareStatement(selectSql);
            stmt.setString(1, participantName);
            stmt.setInt(2, participantAge);
            ResultSet rs = stmt.executeQuery();
    	    
            bloqueado = rs.getInt("bloqueado");
           } catch (SQLException e) {
            bloqueado = 0;
          }
            
		return bloqueado;
	}
	
    private Participant insertParticipant(Connection conn, Participant participant) throws Exception {
    	 String insertSql = "INSERT OR REPLACE INTO participants(id, name, gender, age, profile, email, group_id, study_id) "
    	 		+ "VALUES(?,?,?,?,?,?,?,1)";
    	 Integer beforeGroupId;
    	 
    	 if (participant.getGroupId() == null)
    		 beforeGroupId = 0;
    	 else
    		 beforeGroupId = participant.getGroupId();
    		 
    	 // Revisamos si el estudio es grupal
    	 // No es grupal....group_id=0
    	 // Es grupal ... buscar el group_id en grupos (se crea e inserta si no existe)
    	 //System.out.println("Tipo Estudio: " + studyRepository.getTypeStudy());
    	 if (studyRepository.getTypeStudy().equals(GROUP_INTEVIEW)) {	 	
    		 participant.setGroupId(groupRepository.getGroupId(participant.getGroupId(), participant.getGroup(), participant.getId()));
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
         			System.out.println("ParticipantRepository.insertParticipant");
        			deleteGroup(conn, beforeGroupId);
        		}
             }
             
         } catch (SQLException e) {
        	 throw new Exception(e.getMessage());
         }
         

			
         return participant;
    }
	
    public void deleteGroup(Connection conn, Integer groupId) throws Exception{
	    String deleteGroup = "DELETE FROM groups where id = ?";
	    
	    // El grupo 1 Todos...es un grupo especial y no se puede borrar
	    if (groupId != 1) {
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
	}
    
    public void deleteGroupList(Connection conn, Integer groupId) throws Exception{
	    String deleteGroup = "DELETE FROM group_list where id = ?";
	    
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
    
    public void deleteAllGroup(Connection conn) throws Exception{
	    String deleteGroup = "DELETE FROM groups WHERE id <> 1 AND id NOT IN (SELECT DISTINCT(group_id) FROM participants )";
	    
		if (AppController.fullDatabaseUrl==null) {
			throw new Exception("Debe estar seleccionado un proyecto y un estudio");
		}
        
		try (PreparedStatement pstmt = conn.prepareStatement(deleteGroup)){
	        pstmt.executeUpdate();
        } catch (SQLException e) {
        	throw new Exception(e.getMessage());
        }
		
	}
    
    public void deleteAllGroupList(Connection conn) throws Exception{
	    String deleteGroup = "DELETE FROM group_list WHERE group_id NOT IN (SELECT DISTINCT(group_id) FROM participants )";
	    
		if (AppController.fullDatabaseUrl==null) {
			throw new Exception("Debe estar seleccionado un proyecto y un estudio");
		}
        
		try (PreparedStatement pstmt = conn.prepareStatement(deleteGroup)){
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
			
			if (participant.getName()=="" || existsParticipant(conn, participant) == 0) {
				participant = insertParticipant(conn, participant);
			} else {
				throw new Exception(PARTICIPANTE_DUPLICADO);
			}


        } catch (SQLException e) {
        	throw new Exception(e.getMessage());
        }
		
		

		return participant;
	}
	
	public Participant importParticipant(Participant participant) throws Exception{
		
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
            
            // Si el usuario existe y esta bloqueado no se modifica
            if (isBlockedParticipant(conn, participant.getName(), participant.getAge())==0)
            	participant = insertParticipant(conn, participant);
            
          conn.close();  

        } catch (SQLException e) {
        	throw new Exception(e.getMessage());
        }
		
		

		return participant;
	}
	
	public Integer deleteAll() throws Exception{
	    String deleteAllSql = "DELETE FROM participants WHERE ifnull(locked,0) <> 1";
  
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
        
            ProtocolParticipantRepository protocolParticipantRepository = new ProtocolParticipantRepository();
            protocolParticipantRepository.deleteParticipantConfiguration(conn);

            PreparedStatement pstmt = conn.prepareStatement(deleteAllSql);
            pstmt.executeUpdate();
            
       	    deleteAllGroupList(conn);
       	    deleteAllGroup(conn);
           
            return numParticipants(conn);

        } catch (SQLException e) {
        	throw new Exception(e.getMessage());
        }
		
	}
	
	public void deleteParticipant(Integer id) throws Exception{
	    String deleteSql = "DELETE FROM participants WHERE ifnull(locked,0) = 0 AND id = " + id;
	    String selectSql = "SELECT count(1) contador, group_id FROM participants WHERE group_id = "
	    		+ "( SELECT group_id FROM participants WHERE id = ? )";
	    Integer contador = 0;
	    Integer group_id = 0;
	    
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

            ProtocolParticipantRepository protocolParticipantRepository = new ProtocolParticipantRepository();
            protocolParticipantRepository.deleteParticipantConfiguration(conn, id);
            
            // Revisar si el grupo al que pertenece se ha quedado huerfano
            if (studyRepository.getTypeStudy().equals(GROUP_INTEVIEW)) {
            	PreparedStatement pstmt = conn.prepareStatement(selectSql);
            	pstmt.setInt(1, id);
            	ResultSet rs = pstmt.executeQuery();
            	contador = rs.getInt("contador");
            	group_id = rs.getInt("group_id");
            }
                   
            PreparedStatement pstmt = conn.prepareStatement(deleteSql);
            pstmt.executeUpdate();
            
            // Revisar si el grupo al que pertenece se ha quedado huerfano
            if ((studyRepository.getTypeStudy().equals(GROUP_INTEVIEW)) && (contador == 1)) {
            	 deleteGroupList(conn, group_id);
            	 deleteGroup(conn, group_id);
	    		 //System.out.println("Nombre Grupo: " + participant.getGroup());
	    	 }

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
		    		 //System.out.println("Nombre Grupo: " + participant.getGroup());
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
	
	public List<Participant> getParticipantNonBlockedList() throws Exception{
	    String getParticipantsSql = "SELECT id, name, gender, age, profile, email, group_id FROM participants WHERE ifnull(locked,0) <> 1";
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
		    		 //System.out.println("Nombre Grupo: " + participant.getGroup());
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