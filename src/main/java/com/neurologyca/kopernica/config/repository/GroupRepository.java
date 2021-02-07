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
import com.neurologyca.kopernica.config.model.GroupList;
import com.neurologyca.kopernica.config.model.ProtocolGroupList;
import com.neurologyca.kopernica.config.model.Question;
import com.neurologyca.kopernica.config.model.Stimulus;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;



@Repository
public class GroupRepository {
	
	public void createTableGroups(Connection conn) throws Exception {
		String createTableQuery = "CREATE TABLE IF NOT EXISTS groups ("
				+ "id integer PRIMARY KEY, name TEXT NOT NULL )";
		String insertSql = "INSERT OR REPLACE INTO groups(id, name) " + "VALUES(1,'Todos')";

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
	
	public Integer getGroupId(Integer groupId, String groupName, Integer participantId) throws Exception{
		String selectIdSql = "SELECT coalesce(max(name), '') name FROM groups WHERE id = ?";
		String oldGroupName;
		Integer groupIdExists = 0;
		
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
            		// Revisar si existe el nombre de ese grupo en otro participante
            		if ((groupIdExists = existsGroupName(groupName)) == 0) {
            		    // Si no existe el nombre del grupo, comprobamos si hay algún participante
            			if (numCountParticipants(groupId, participantId) == 0) {
            				// Si no hay otro participante, actualizamos
            				Group group = new Group(groupId, groupName);
                    		insertGroup(conn, group);
                    		groupId = group.getId();
                			//System.out.println("Actualizado grupo "+ groupId);
                		} else { // Si lo hay, creamos grupo
	            			Group group = new Group(0, groupName);
	                		insertGroup(conn, group);
	                		groupId = group.getId();
	                		//System.out.println("Creado grupo "+ groupId);
                		}
            		} else {			
            		    // Si existe actualizar participante
            			groupId = groupIdExists;
            			
                		//System.out.println("Cambia de grupo "+ groupId);
            		}
            	}
            	
            	
            	//System.out.println("Group Id "+ groupId);
            } catch (SQLException e) {
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
	
	public Integer existsGroupName(String groupName) throws Exception{
		String selectSql = "SELECT coalesce(max(id),0) id FROM groups WHERE name = ?";
		Integer groupId = 0;
		
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
            
            // Si existe el nombre del grupo devuelve el Id
            try (PreparedStatement stmt = conn.prepareStatement(selectSql)) {
            	stmt.setString(1, groupName);
            	ResultSet rs = stmt.executeQuery();
    	    
            	groupId = rs.getInt("id");
            } catch (SQLException e) {
            	throw new Exception(e.getMessage());
            }
            
			conn.close();
        } catch (SQLException e) {
        	throw new Exception(e.getMessage());
        }

		return groupId;
	}
	
	public Integer numCountParticipants(Integer groupId, Integer participantId) throws Exception{
		String selectSql = "SELECT count(1) contador FROM participants WHERE group_id = ? and id != ?";
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
            
            try (PreparedStatement stmt = conn.prepareStatement(selectSql)) {
            	stmt.setInt(1, groupId);
            	stmt.setInt(2, participantId);
            	ResultSet rs = stmt.executeQuery();
    	    
            	count = rs.getInt("contador");
            } catch (SQLException e) {
            	throw new Exception(e.getMessage());
            }
            
			conn.close();
        } catch (SQLException e) {
        	throw new Exception(e.getMessage());
        }

		return count;
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
            	//System.out.println("Group Name "+ groupName);
            } catch (SQLException e) {
            	throw new Exception(e.getMessage());
            }
            
			conn.close();
        } catch (SQLException e) {
        	throw new Exception(e.getMessage());
        }

		return groupName;
	}

	public List<Group> getGroups() throws Exception{
	    String getGroupSql = "SELECT id, name FROM groups";
	    Group group;
	    List<Group> groupList = new ArrayList<Group>();
	    
	    
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
            
            PreparedStatement pstmt = conn.prepareStatement(getGroupSql);

            ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				group = new Group();
				group.setId(rs.getInt("id"));
				group.setName(rs.getString("name"));

				groupList.add(group);
			}

        } catch (SQLException e) {
        	throw new Exception(e.getMessage());
        }
		
		return groupList;
		
	}
	
	public void deleteGroupProtocol(Integer protocolId, Integer groupId) throws Exception{
	    String deleteSql = "DELETE FROM group_list where group_id = ? and protocol_id= ?";
	    
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
            pstmt.setInt(1, groupId);
            pstmt.setInt(2, protocolId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
        	throw new Exception(e.getMessage());
        }
		
	}
	
	
	public void saveGroupProtocol (GroupList groupList, Integer protocolId) throws Exception{
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
            
			insertGroupList(conn, groupList, protocolId);

        } catch (SQLException e) {
        	throw new Exception(e.getMessage());
        }
	}
	
	private Integer selectMaxGroupListId(Connection conn) throws Exception {
		String selectMaxIdSql = "SELECT MAX(id) id FROM group_list";
		ResultSet rs;

		try (PreparedStatement pstmt = conn.prepareStatement(selectMaxIdSql)) {
			Statement stmt = conn.createStatement();
			rs = stmt.executeQuery(selectMaxIdSql);
		} catch (SQLException e) {
			throw new Exception(e.getMessage());
		}
		return rs.getInt("id");
	}
	
	private void insertGroupList(Connection conn, GroupList groupList, Integer protocolId) throws Exception {
   	 String insertSql = "INSERT OR REPLACE INTO group_list(id, group_id, protocol_id) "
   	 		+ "VALUES(?,?,?)";
   	 
        try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {      	 
       	 if (groupList.getId()==null || groupList.getId()==0) {
       		 groupList.setId(selectMaxGroupListId(conn)+1);
       	 }
      	 
            pstmt.setInt(1, groupList.getId());
            pstmt.setInt(2, groupList.getGroup().getId());
            pstmt.setInt(3, protocolId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
       	 throw new Exception(e.getMessage());
        }
   }
	
	public List<ProtocolGroupList> getProtocolGroupList() throws Exception{
	    String getGroupListSql = "SELECT id, group_id, protocol_id FROM group_list";
	    ProtocolGroupList group;
	    List<ProtocolGroupList> groupList = new ArrayList<ProtocolGroupList>();
	    
	    
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
            
            PreparedStatement pstmt = conn.prepareStatement(getGroupListSql);

            ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				group = new ProtocolGroupList();
				group.setId(rs.getInt("id"));
				group.setGroupId(rs.getInt("group_id"));
				group.setProtocolId(rs.getInt("protocol_id"));
				//System.out.println(group.getGroupId());

				groupList.add(group);
			}

        } catch (SQLException e) {
        	throw new Exception(e.getMessage());
        }
		
		return groupList;
		
	}
}