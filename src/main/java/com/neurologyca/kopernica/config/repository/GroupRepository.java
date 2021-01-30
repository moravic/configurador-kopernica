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
import com.neurologyca.kopernica.config.model.Group;
import java.sql.Statement;



@Repository
public class GroupRepository {
	
    public void createTableGroups(Connection conn) throws Exception{
		String createTableQuery = "CREATE TABLE IF NOT EXISTS groups ("
				+ "id integer PRIMARY KEY, name TEXT NOT NULL )";
		String insertSql = "INSERT OR REPLACE INTO groups(id, name) "
	    	 		+ "VALUES(1,'Todos')";
		
		try {
			Statement stmt = conn.createStatement();
			stmt.execute(createTableQuery);	
			
			try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
				pstmt.executeUpdate();
			 } catch (SQLException e) {
	        	 throw new Exception(e.getMessage());	
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		 }
	 }

    private Integer selectMaxId(Connection conn) throws Exception {
   	 String selectMaxIdSql = "SELECT MAX(id) id FROM groups";
   	 ResultSet rs;
   	 
        try (PreparedStatement pstmt = conn.prepareStatement(selectMaxIdSql)) {
        	Statement stmt = conn.createStatement();
        	rs = stmt.executeQuery(selectMaxIdSql);
        } catch (SQLException e) {
       	 throw new Exception(e.getMessage());
        }
        return rs.getInt("id");
   }
	
    private void insertGroup(Connection conn, Group group) throws Exception {
    	 String insertSql = "INSERT OR REPLACE INTO groups(id, name) "
    	 		+ "VALUES(?,?)";
    	 
         try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {      	 
        	 if (group.getId()==0) {
        		 group.setId(selectMaxId(conn)+1);
        	 }
       	 
             pstmt.setInt(1, group.getId());
             pstmt.setString(2, group.getName());
             pstmt.executeUpdate();
         } catch (SQLException e) {
        	 throw new Exception(e.getMessage());
         }
    }
	
	public Integer save(Group group) throws Exception{
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
            
			createTableGroups(conn);
			insertGroup(conn, group);
            
			conn.close();
        } catch (SQLException e) {
        	throw new Exception(e.getMessage());
        }

		return group.getId();
	}
	
	public Integer getGroupId(Integer groupId, String groupName) throws Exception{
		String selectIdSql = "SELECT name FROM groups WHERE id = ?";
		String oldGroupName;
		
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
    
            try (PreparedStatement stmt = conn.prepareStatement(selectIdSql)) {
            	
            	stmt.setInt(1, groupId);
            	ResultSet rs = stmt.executeQuery();
    	    
            	oldGroupName = rs.getString("name");
            	System.out.println("Nombre grupo actual/bbdd "+ groupName + " "+ oldGroupName);
            	
            	if (!oldGroupName.equals(groupName)) {
            		// Revisar si algun participante tiene ese Id
            		if (numGroupParticipants(groupId) == 0) {
            		// Si no hay ninguno cambiar el nombre del grupo
            			Group group = new Group(groupId, groupName);
                		insertGroup(conn, group);
                		groupId = group.getId();
                		System.out.println("Cambiar nombre grupo "+ groupId);
            		} else {			
            		// Si hay alguno, es un grupo nuevo
            			Group group = new Group(0, groupName);
                		insertGroup(conn, group);
                		groupId = group.getId();
                		System.out.println("Nuevo grupo "+ groupId);
            		}
            	}
            	
            	
            	System.out.println("Group Id "+ groupId);
            } catch (SQLException e) {
            	if (!groupName.equals("")) {
            		Group group = new Group(0, groupName);
            		createTableGroups(conn);
            		insertGroup(conn, group);
            		groupId = group.getId();
            		System.out.println("Nuevo grupo "+ groupId);
            	}
            }
           
			conn.close();
        } catch (SQLException e) {
        	throw new Exception(e.getMessage());
        }

		return groupId;
	}
	
	public Integer getGroupId(String groupName) throws Exception{
		String selectIdSql = "SELECT id FROM groups WHERE name = ?";
		int groupId = 0;
		
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
    
            try (PreparedStatement stmt = conn.prepareStatement(selectIdSql)) {
            	
            	stmt.setString(1, groupName);
            	ResultSet rs = stmt.executeQuery();
            	groupId = rs.getInt("id");
            	conn.close();
            } catch (SQLException e) {
        	groupId = 0;
          }
		}

		return groupId;
	}
	
	public Integer numGroupParticipants(Integer groupId) throws Exception{
		String selectSql = "SELECT count(1) contador FROM participants WHERE group_id = ?";
		Integer numParticipants = 0;
		
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
            
            // Si existe el nombre del grupo devuelve el Id y si no existe se crea el grupo y devuelve el id
            try (PreparedStatement stmt = conn.prepareStatement(selectSql)) {
            	stmt.setInt(1, groupId);
            	ResultSet rs = stmt.executeQuery();
    	    
            	numParticipants = rs.getInt("contador");
            } catch (SQLException e) {
            	throw new Exception(e.getMessage());
            }
            
			conn.close();
        } catch (SQLException e) {
        	throw new Exception(e.getMessage());
        }

		return numParticipants;
	}
	
	public String getGroupName(Integer groupId) throws Exception{
		String selectSql = "SELECT name FROM groups WHERE id = ?";
		String groupName;
		
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
            
            // Si existe el nombre del grupo devuelve el Id y si no existe se crea el grupo y devuelve el id
            try (PreparedStatement stmt = conn.prepareStatement(selectSql)) {
            	stmt.setInt(1, groupId);
            	ResultSet rs = stmt.executeQuery();
    	    
            	groupName = rs.getString("name");
            	System.out.println("Group Name "+ groupName);
            } catch (SQLException e) {
            	throw new Exception(e.getMessage());
            }
            
			conn.close();
        } catch (SQLException e) {
        	throw new Exception(e.getMessage());
        }

		return groupName;
	}

	/*
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
	*/
}