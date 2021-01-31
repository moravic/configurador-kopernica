package com.neurologyca.kopernica.config.repository;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.neurologyca.kopernica.config.controller.AppController;
import com.neurologyca.kopernica.config.model.BlockElementTableList;
import com.neurologyca.kopernica.config.model.Participant;
import com.neurologyca.kopernica.config.model.Protocol;
import com.neurologyca.kopernica.config.model.ProtocolGroupList;
import com.neurologyca.kopernica.config.model.Segment;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProtocolParticipantRepository {

	@Value("${database.url}")
	private String databaseUrl;

	@Value("${base.path}")
	private String basePath;

	static final String GROUP_INTEVIEW = "0";
	static final String INDIVIDUAL_INTERVIEW = "1";

	private List<Protocol> getProtocols() throws Exception {
		List<Protocol> protocols = new ArrayList<Protocol>();
		Protocol protocol;

		String getProtocol = "SELECT id FROM protocols";

		if (AppController.fullDatabaseUrl == null) {
			throw new Exception("Debe estar seleccionado un proyecto y un estudio");
		}
		try (Connection conn = DriverManager.getConnection(AppController.fullDatabaseUrl)) {
			if (conn != null) {
				// Si no existe se crea la bbdd
				DatabaseMetaData meta = conn.getMetaData();
				// System.out.println("The driver name is " + meta.getDriverName());
				// System.out.println("A new database has been created.");
			}

			PreparedStatement pstmt = conn.prepareStatement(getProtocol);

			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				protocol = new Protocol();
				protocol.setId(rs.getInt("id"));
				
				protocols.add(protocol);
			}

			return protocols;

		} catch (SQLException e) {
			throw new Exception(e.getMessage());
		}

	}
	
	private List<Segment> getSegmentList(Integer protocol_id) throws Exception {
		List<Segment> segmentList = new ArrayList<Segment>();
		Segment segment;

		String getConditions = "SELECT s.id, s.type, s.value_age_min, s.value_age_max, s.value_gender, s.value_profile "
				+ "FROM segment_list sl, segments s "
				+ "WHERE sl.protocol_id = ? "
				+ "AND sl.segment_id = s.id";

		if (AppController.fullDatabaseUrl == null) {
			throw new Exception("Debe estar seleccionado un proyecto y un estudio");
		}
		try (Connection conn = DriverManager.getConnection(AppController.fullDatabaseUrl)) {
			if (conn != null) {
				// Si no existe se crea la bbdd
				DatabaseMetaData meta = conn.getMetaData();
				// System.out.println("The driver name is " + meta.getDriverName());
				// System.out.println("A new database has been created.");
			}

			PreparedStatement pstmt = conn.prepareStatement(getConditions);

			pstmt.setInt(1, protocol_id);
			
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				segment = new Segment();
				segment.setId(rs.getInt("id"));
				segment.setType(rs.getString("type"));
				segment.setValueAgeMin(rs.getInt("value_age_min"));
				segment.setValueAgeMax(rs.getInt("value_age_max"));
				segment.setValueGender(rs.getString("value_gender"));
				segment.setValueProfile(rs.getString("value_profile"));

				segmentList.add(segment);
			}

			return segmentList;

		} catch (SQLException e) {
			throw new Exception(e.getMessage());
		}

	}
	
    private Integer selectMaxId(Connection conn) throws Exception {
     	 String selectMaxIdSql = "SELECT MAX(id) id FROM participant_protocol";
     	 ResultSet rs;
     	 
          try (PreparedStatement pstmt = conn.prepareStatement(selectMaxIdSql)) {
          	Statement stmt = conn.createStatement();
          	rs = stmt.executeQuery(selectMaxIdSql);
          } catch (SQLException e) {
         	 throw new Exception(e.getMessage());
          }
          return rs.getInt("id");
     }
    
    private void insertParticipantProtocol(Connection conn, Integer participant_id, Integer protocol_id) throws Exception {
   	 String insertSql = "INSERT OR REPLACE INTO participant_protocol(id, participant_id, protocol_id) "
   	 		+ "VALUES(?,?,?)";
   	 
        try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {      	    	 
            pstmt.setInt(1, selectMaxId(conn)+1);
            pstmt.setInt(2, participant_id);
            pstmt.setInt(3, protocol_id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
       	 throw new Exception(e.getMessage());
        }
   }
	
    private void insertAllParticipantProtocol(Connection conn, Integer protocolId) throws Exception {
      	 String insertSql = "INSERT OR REPLACE INTO participant_protocol(id, participant_id, protocol_id) "
      	 		+ "SELECT ROW_NUMBER() OVER (ORDER BY id ASC, group_id ASC) + (SELECT MAX(id) FROM participant_protocol), id, '" + protocolId + "' FROM participants";
      	 
           try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {      	    	 
               pstmt.executeUpdate();
           } catch (SQLException e) {
          	 throw new Exception(e.getMessage());
           }
      }
    
    private void insertGroupParticipantProtocol(Connection conn, Integer protocolId, Integer groupId) throws Exception {
     	 String insertSql = "INSERT OR REPLACE INTO participant_protocol(id, participant_id, protocol_id) "
     	 		+ "SELECT ROW_NUMBER() OVER (ORDER BY id ASC, group_id ASC) + (SELECT MAX(id) FROM participant_protocol), id, '" + protocolId + "' FROM participants WHERE group_id=?";

          try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {     
              pstmt.setInt(1, groupId);
              pstmt.executeUpdate();
          } catch (SQLException e) {
         	 throw new Exception(e.getMessage());
          }
     }
   	
	
    private Integer applyConditions() throws Exception {
		List<Participant> participantList = new ArrayList<Participant>();
		List<Segment> segmentList = new ArrayList<Segment>();
		List<Protocol> protocolList = new ArrayList<Protocol>();
		ParticipantRepository participantRepository = new ParticipantRepository();
		boolean meetConditions;

		//System.out.println("Entrando en apply Conditions");
		
		if (AppController.fullDatabaseUrl == null) {
			throw new Exception("Debe estar seleccionado un proyecto y un estudio");
		}

		try (Connection conn = DriverManager.getConnection(AppController.fullDatabaseUrl)) {
			if (conn != null) {
				// Si no existe se crea la bbdd
				DatabaseMetaData meta = conn.getMetaData();
				// System.out.println("The driver name is " + meta.getDriverName());
				// System.out.println("A new database has been created.");
			}
			
			// Obtenemos el listado de participantes
			participantList = participantRepository.getParticipantList();
			
			// Obtenemos el listado de protocolos
			protocolList = getProtocols();
			
			for (Protocol protocol: protocolList) {
				//System.out.println("Protocolo " + protocol.getId());
				// Obtenemos el listado de condiciones de cada protocolo
				segmentList = getSegmentList(protocol.getId());		
				//System.out.println("Num segmentos: " + segmentList.size());
				if (segmentList.size()>0) {
					// Revisamos si se cumplen las condiciones
					for (Participant participant: participantList) {
						//System.out.println(participant.getName());
						meetConditions = true;
						for (Segment condition: segmentList) {
							//System.out.println(condition.getType());
							switch(condition.getType()) {
							case "Edad": 
								if (!(participant.getAge() >= condition.getValueAgeMin() &&
									participant.getAge() <= condition.getValueAgeMax())) {
									meetConditions = false;
								}
								//System.out.println("Edad " + meetConditions);
								break;
							case "Género":
								if (!participant.getGender().equals(condition.getValueGender())) {
									meetConditions = false;
								}
								//System.out.println("Género " + meetConditions);
								break;
							case "Perfil":
								if (!participant.getProfile().equals(condition.getValueProfile())) {
									meetConditions = false;
								}
								//System.out.println("Perfil " + meetConditions);
								break;
							case "Todos":
								//System.out.println("Todos " + meetConditions);
								break;
							default:
								//System.out.println("Otros");
								break;
							}	
							if (!meetConditions) {
								//System.out.println("Salir de este participante: " + participant.getName());
								break;
							}
				
						}
						if (meetConditions) {
							//System.out.println("Cumple " + participant.toString());
							// Insertamos en la tabla participant_protocol
							insertParticipantProtocol(conn, participant.getId(), protocol.getId());
						}
					}
					
				}
			}
			
		} catch (SQLException e) {
			throw new Exception(e.getMessage());
		}
		
		return 1;
	}
	
	private List<Integer> getParticipantProtocolList() throws Exception {
		List<Integer> participantProtocolList = new ArrayList<Integer>();
		Integer participant;

		String getParticipants = "SELECT id FROM participant_protocol";

		if (AppController.fullDatabaseUrl == null) {
			throw new Exception("Debe estar seleccionado un proyecto y un estudio");
		}
		try (Connection conn = DriverManager.getConnection(AppController.fullDatabaseUrl)) {
			if (conn != null) {
				// Si no existe se crea la bbdd
				DatabaseMetaData meta = conn.getMetaData();
				// System.out.println("The driver name is " + meta.getDriverName());
				// System.out.println("A new database has been created.");
			}

			PreparedStatement pstmt = conn.prepareStatement(getParticipants);
			
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				participant = rs.getInt("id");
				participantProtocolList.add(participant);
			}

			return participantProtocolList;

		} catch (SQLException e) {
			throw new Exception(e.getMessage());
		}

	}
	
	private List<BlockElementTableList> getBlockElementList() throws Exception {
		List<BlockElementTableList> blockElementList = new ArrayList<BlockElementTableList>();
		BlockElementTableList blockElement;

		String getBlocksInfo = "SELECT bel.id id, bel.block_id block_id, bel.blockElement_id blockElement_id\r\n"
				+ "FROM protocols pr\r\n"
				+ "JOIN block_list bl ON (pr.id=bl.protocol_id)\r\n"
				+ "JOIN blocks b ON (b.id=bl.block_id)\r\n"
				+ "JOIN blockelement_list bel ON (bel.block_id=b.id)\r\n"
				+ "JOIN blockelement be ON (be.id=bel.blockElement_id)\r\n"
				+ "LEFT JOIN questions q ON (q.id=be.question_id)\r\n"
				+ "LEFT JOIN stimulus s ON (s.id=be.stimulus_id)\r\n"
				+ "ORDER BY pr.id, bl.block_id, bel.id";

		if (AppController.fullDatabaseUrl == null) {
			throw new Exception("Debe estar seleccionado un proyecto y un estudio");
		}
		try (Connection conn = DriverManager.getConnection(AppController.fullDatabaseUrl)) {
			if (conn != null) {
				// Si no existe se crea la bbdd
				DatabaseMetaData meta = conn.getMetaData();
				// System.out.println("The driver name is " + meta.getDriverName());
				// System.out.println("A new database has been created.");
			}

			PreparedStatement pstmt = conn.prepareStatement(getBlocksInfo);

			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				blockElement = new BlockElementTableList();
				blockElement.setId(rs.getInt("id"));
				blockElement.setBlockId(rs.getInt("block_id"));
				blockElement.setBlockElementId(rs.getInt("blockElement_id"));
				
				blockElementList.add(blockElement);
			}

			return blockElementList;

		} catch (SQLException e) {
			throw new Exception(e.getMessage());
		}

	}
	
    private Integer selectMaxIdParticipantProtocolOrder(Connection conn) throws Exception {
    	 String selectMaxIdSql = "SELECT MAX(id) id FROM participant_protocol_order";
    	 ResultSet rs;
    	 
         try (PreparedStatement pstmt = conn.prepareStatement(selectMaxIdSql)) {
         	Statement stmt = conn.createStatement();
         	rs = stmt.executeQuery(selectMaxIdSql);
         } catch (SQLException e) {
        	 throw new Exception(e.getMessage());
         }
         return rs.getInt("id");
    }
   
   private void insertParticipantProtocolOrder(Connection conn, Integer participant_id, Integer block_id, Integer blockElement_id, Integer no_order) throws Exception {
  	 String insertSql = "INSERT OR REPLACE INTO participant_protocol_order(id, participant_protocol_id, block_id, blockElement_id, no_order) "
  	 		+ "VALUES(?,?,?,?,?)";
  	 
       try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {      	    	 
           pstmt.setInt(1, selectMaxIdParticipantProtocolOrder(conn)+1);
           pstmt.setInt(2, participant_id);
           pstmt.setInt(3, block_id);
           pstmt.setInt(4, blockElement_id);
           pstmt.setInt(5, no_order);
           pstmt.executeUpdate();
       } catch (SQLException e) {
      	 throw new Exception(e.getMessage());
       }
  }
	private Integer applyBlocks() throws Exception {
		List<Integer> participantProtocolList = new ArrayList<Integer>();
		List<BlockElementTableList> blockElementList = new ArrayList<BlockElementTableList>();
		Integer no_order;

		//System.out.println("Entrando en apply Blocks");
		
		if (AppController.fullDatabaseUrl == null) {
			throw new Exception("Debe estar seleccionado un proyecto y un estudio");
		}

		try (Connection conn = DriverManager.getConnection(AppController.fullDatabaseUrl)) {
			if (conn != null) {
				// Si no existe se crea la bbdd
				DatabaseMetaData meta = conn.getMetaData();
				// System.out.println("The driver name is " + meta.getDriverName());
				// System.out.println("A new database has been created.");
			}
			
			// Obtenemos el listado de participantes que han cumplido las condiciones de cada protocolo
			participantProtocolList = getParticipantProtocolList();
			
            // Obtenemos las preguntas y estimulos de cada protocolo
			blockElementList = getBlockElementList();
			no_order = 0;
			
			for (Integer participant:participantProtocolList) {
				//System.out.println("Paricipant Id: " + participant);
			
				for (BlockElementTableList blockElement:blockElementList) {					
					// Insertamos en la tabla participant_protocolo_order
					insertParticipantProtocolOrder(conn, participant, blockElement.getBlockId(), blockElement.getBlockElementId(), no_order++);
				}
			}
			
			
		} catch (SQLException e) {
			throw new Exception(e.getMessage());
		}
		
		return 1;
	}
	
	public void deleteParticipantProtocol() throws Exception{
	    String deleteAllSql = "DELETE FROM participant_protocol";
	    
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
	
	public void deleteParticipantProtocolOrder() throws Exception{
	    String deleteAllSql = "DELETE FROM participant_protocol_order";
	    
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
	
	private Integer applyGroupConditions() throws Exception {
		List<ProtocolGroupList> groupList = new ArrayList<ProtocolGroupList>();
		GroupRepository groupRepository = new GroupRepository();


		//System.out.println("Entrando en apply Conditions");
		
		if (AppController.fullDatabaseUrl == null) {
			throw new Exception("Debe estar seleccionado un proyecto y un estudio");
		}

		try (Connection conn = DriverManager.getConnection(AppController.fullDatabaseUrl)) {
			if (conn != null) {
				// Si no existe se crea la bbdd
				DatabaseMetaData meta = conn.getMetaData();
				// System.out.println("The driver name is " + meta.getDriverName());
				// System.out.println("A new database has been created.");
			}
			
			// Obtenemos el listado de grupos de cada protocolo
			groupList = groupRepository.getProtocolGroupList();
			
			//Para cada protocolo si tiene grupo 1 se aplica a todos los participantes
			for (ProtocolGroupList protocolGroupList: groupList) {
				System.out.println(protocolGroupList.getProtocolId());
				if (protocolGroupList.getGroupId()==1) {
					insertAllParticipantProtocol(conn, protocolGroupList.getProtocolId());
				}
				else {
					insertGroupParticipantProtocol(conn, protocolGroupList.getProtocolId(), protocolGroupList.getGroupId());
				}
			}
			
		} catch (SQLException e) {
			throw new Exception(e.getMessage());
		}
		
		return 1;
	}
	
	public Integer applyConfiguration() throws Exception {
		StudyRepository studyRepository = new StudyRepository();
		try {
			deleteParticipantProtocolOrder();
			deleteParticipantProtocol();
			 if (studyRepository.getTypeStudy().equals(INDIVIDUAL_INTERVIEW)) {	
				 applyConditions();
			 }
			 else {
				 applyGroupConditions();
			 }
			applyBlocks();
		} catch (SQLException e) {
			throw new Exception(e.getMessage());
		}
		return 1;
	}
}