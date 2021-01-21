package com.neurologyca.kopernica.config.repository;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.neurologyca.kopernica.config.controller.AppController;
import com.neurologyca.kopernica.config.model.Block;
import com.neurologyca.kopernica.config.model.BlockElement;
import com.neurologyca.kopernica.config.model.BlockElementList;
import com.neurologyca.kopernica.config.model.BlockList;
import com.neurologyca.kopernica.config.model.Protocol;
import com.neurologyca.kopernica.config.model.Question;
import com.neurologyca.kopernica.config.model.Segment;
import com.neurologyca.kopernica.config.model.SegmentList;
import com.neurologyca.kopernica.config.model.Stimulus;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProtocolRepository {

	@Value("${database.url}")
	private String databaseUrl;

	@Value("${base.path}")
	private String basePath;

	private void createProtocolTables(Connection conn) throws Exception {
		String create_SEGMENTS = "CREATE TABLE IF NOT EXISTS segments (id INTEGER PRIMARY KEY, type TEXT NOT NULL, value_age_min INTEGER NULL, value_age_max INTEGER NULL, value_gender TEXT NULL, value_profile TEXT NULL)";
		String create_SEGMENT_LIST = "CREATE TABLE IF NOT EXISTS segment_list (id INTEGER, segment_id INTEGER NOT NULL, protocol_id INTEGER NOT NULL, FOREIGN KEY(segment_id) REFERENCES segments(id), FOREIGN KEY(protocol_id) REFERENCES protocols(id), PRIMARY KEY(id))";
		String create_BLOCKELEMENT = "CREATE TABLE IF NOT EXISTS blockelement (id INTEGER PRIMARY KEY, question_id INTEGER NULL, stimulus_id INTEGER NULL, FOREIGN KEY(stimulus_id) REFERENCES stimulus(id), FOREIGN KEY(question_id) REFERENCES questions(id))";
		String create_BLOCKELEMENT_LIST = "CREATE TABLE IF NOT EXISTS blockelement_list (id INTEGER, block_id INTEGER NOT NULL, blockElement_id INTEGER NOT NULL, FOREIGN KEY(block_id) REFERENCES blocks(id), FOREIGN KEY(blockElement_id) REFERENCES blockElement(id), PRIMARY KEY(id))";
		String create_BLOCKS = "CREATE TABLE IF NOT EXISTS blocks (id INTEGER PRIMARY KEY, block_name TEXT NOT NULL)";
		String create_BLOCK_LIST = "CREATE TABLE IF NOT EXISTS block_list (id INTEGER, block_id INTEGER NOT NULL, protocol_id INTEGER NOT NULL, FOREIGN KEY(block_id) REFERENCES blocks(id), FOREIGN KEY(protocol_id) REFERENCES protocols(id), PRIMARY KEY(id))";
		String create_PROTOCOLS = "CREATE TABLE IF NOT EXISTS protocols (id INTEGER PRIMARY KEY, name TEXT NOT NULL)";
		String create_PARTICIPANT_PROTOCOL = "CREATE TABLE IF NOT EXISTS participant_protocol (id INTEGER PRIMARY KEY, participant_id INTEGER NOT NULL, protocol_id INTEGER NOT NULL, FOREIGN KEY(participant_id) REFERENCES participants(id), FOREIGN KEY(protocol_id) REFERENCES protocols(id))";
		String create_PARTICIPANT_PROTOCOL_ORDER = "CREATE TABLE IF NOT EXISTS participant_protocol_order (id INTEGER PRIMARY KEY, participant_protocol_id INTEGER NOT NULL, type_blockElement TEXT NOT NULL, blockElement_id INTEGER NOT NULL, no_order INTEGER NOT NULL, FOREIGN KEY(participant_protocol_id) REFERENCES participant_protocol(id), FOREIGN KEY(blockElement_id) REFERENCES blockElement(id))";
		
		/*
		 * 
insert into participant_protocol_order
select row_number() over (order by pp.id) id, pp.id, bel.block_id, bel.blockElement_id, rank() over (partition by pp.participant_id, bel.block_id order by bel.id)
from participant_protocol pp
join protocols p on (pp.protocol_id = p.id)
join block_list bl on (p.id=bl.protocol_id)
join blockelement_list bel on (bl.block_id=bel.block_id)
join blockelement be on (bel.blockElement_id = be.id)

select pr.name, p.name, b.block_name, coalesce(q.question, s.name) text_element, no_order
from participant_protocol_order ppo
join participant_protocol pp on (ppo.participant_protocol_id=pp.id)
join participants p on (pp.participant_id=p.id)
join protocols pr on (pp.protocol_id=pr.id)
join blocks b on (b.id=ppo.block_id)
join blockelement be on (be.id=ppo.blockElement_id)
left join questions q on (q.id=be.question_id)
left join stimulus s on (s.id=be.stimulus_id)
order by pr.id, p.id, b.id, no_order

		 */
		try {
			Statement stmt = conn.createStatement();
			stmt.execute(create_PARTICIPANT_PROTOCOL_ORDER);
			stmt.execute(create_PARTICIPANT_PROTOCOL);
			stmt.execute(create_PROTOCOLS);
			stmt.execute(create_SEGMENT_LIST);
			stmt.execute(create_SEGMENTS);
			stmt.execute(create_BLOCK_LIST);
			stmt.execute(create_BLOCKS);
			stmt.execute(create_BLOCKELEMENT_LIST);
			stmt.execute(create_BLOCKELEMENT);

		} catch (SQLException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	public List<Protocol> getProtocols() throws Exception {
		List<Protocol> protocols = new ArrayList<Protocol>();

		/*String getAll = "SELECT\r\n"
				+ "CASE\r\n"
				+ "  WHEN pr.id = LAG(pr.id) OVER (ORDER BY pr.id) THEN null\r\n"
				+ "  ELSE pr.id\r\n"
				+ "END AS protocol_id,\r\n"
				+ "pr.name protocol_name,\r\n"
				+ "CASE\r\n"
				+ "  WHEN bl.id = LAG(bl.id) OVER (ORDER BY pr.id, bl.id) THEN null\r\n"
				+ "  ELSE bl.id\r\n"
				+ "END AS blocklist_id,\r\n"
				+ "CASE\r\n"
				+ "  WHEN b.id = LAG(b.id) OVER (ORDER BY pr.id, bl.id, b.id) THEN null\r\n"
				+ "  ELSE b.id\r\n"
				+ "END AS block_id,\r\n"
				+ "b.block_name,\r\n"
				+ "bel.id blockElementList_id,\r\n"
				+ "be.id blockElement_id,\r\n"
				+ "be.question_id, be.stimulus_id, q.question, s.name stimulus\r\n"
				+ "FROM protocols pr\r\n"
				+ "join block_list bl on (pr.id=bl.protocol_id)\r\n"
				+ "join blocks b on (b.id=bl.block_id)\r\n"
				+ "join blockelement_list bel on (bel.block_id=b.id)\r\n"
				+ "join blockelement be on (be.id=bel.blockElement_id)\r\n"
				+ "left join questions q on (q.id=be.question_id)\r\n"
				+ "left join stimulus s on (s.id=be.stimulus_id)\r\n"
				+ "order by pr.id, bl.block_id, bel.id";*/
        
		String getProtocols = "select id, name FROM protocols order by id";
		String getSegment = "select sl.id segmentlist_id, s.id, s.type, s.value_age_min, s.value_age_max, s.value_gender, s.value_profile "
				+ "FROM segment_list sl JOIN segments s on (sl.segment_id=s.id) "
				+ "WHERE protocol_id=? "
				+ "order by s.id";
		String getBlock = "select bl.id blocklist_id, bl.block_id, b.block_name "
				+ "FROM block_list bl JOIN blocks b on (b.id=bl.block_id) "
				+ "WHERE protocol_id=? "
				+ "order by bl.block_id";
		String getBlockElement = " SELECT bel.id blockElementList_id, "
				+ "be.id blockElement_id, "
				+ "be.question_id, "
				+ "be.stimulus_id, "
				+ "q.question, "
				+ "s.name stimulus "
				+ "FROM blockelement_list bel "
				+ "join blockelement be on (be.id=bel.blockElement_id) "
				+ "left join questions q on (q.id=be.question_id) "
				+ "left join stimulus s on (s.id=be.stimulus_id) "
				+ "where bel.block_id = ?"
				+ "order by bel.id";
		
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

			PreparedStatement pstmtProtocol = conn.prepareStatement(getProtocols);

			ResultSet rsProtocol = pstmtProtocol.executeQuery();

			while (rsProtocol.next()) {
				Protocol protocol = new Protocol();
				protocol.setId(rsProtocol.getInt("id"));
				protocol.setName(rsProtocol.getString("name"));
				
				PreparedStatement pstmtSegment = conn.prepareStatement(getSegment);
				pstmtSegment.setInt(1, protocol.getId());
				
				ResultSet rsSegment = pstmtSegment.executeQuery();
				
				List<SegmentList> segmentListArray = new ArrayList<SegmentList>();
				
				while (rsSegment.next()) {
					SegmentList segmentList = new SegmentList();
					Segment segment = new Segment();
					
					segmentList.setId(rsSegment.getInt("segmentlist_id"));
					segment.setId(rsSegment.getInt("id"));
					segment.setType(rsSegment.getString("type"));
					segment.setValueAgeMin(rsSegment.getInt("value_age_min"));
					segment.setValueAgeMax(rsSegment.getInt("value_age_max"));
					segment.setValueGender(rsSegment.getString("value_gender"));
					segment.setValueProfile(rsSegment.getString("value_profile"));
					segmentList.setSegment(segment);
					segmentListArray.add(segmentList);
				}
				
				protocol.setSegmentListArray(segmentListArray);
				
				PreparedStatement pstmtBlock = conn.prepareStatement(getBlock);
				pstmtBlock.setInt(1, protocol.getId());
				
				ResultSet rsBlock = pstmtBlock.executeQuery();
				
				List<BlockList> blockListArray = new ArrayList<BlockList>();
				
				while (rsBlock.next()) {
					
					BlockList blockList = new BlockList();
					Block block = new Block();
					blockList.setId(rsBlock.getInt("blocklist_id"));
					block.setId(rsBlock.getInt("block_id"));
					block.setName(rsBlock.getString("block_name"));
					blockList.setBlock(block);
	
					PreparedStatement pstmtBlockElement = conn.prepareStatement(getBlockElement);
					pstmtBlockElement.setInt(1, block.getId());
					
					ResultSet rsBlockElement = pstmtBlockElement.executeQuery();
					
					List<BlockElementList> blockElementListArray = new ArrayList<BlockElementList>();
					
					while (rsBlockElement.next()) {
						BlockElementList blockElementList = new BlockElementList();
						blockElementList.setId(rsBlockElement.getInt("blockElementList_id"));

						BlockElement blockElement = new BlockElement();
						blockElement.setId(rsBlockElement.getInt("blockElement_id"));
						
						Question question = new Question();
						if (rsBlockElement.getInt("question_id")!=0) {
							question.setId(rsBlockElement.getInt("question_id"));
							question.setQuestion(rsBlockElement.getString("question"));
						}
						
						Stimulus stimulus = new Stimulus();
						if (rsBlockElement.getInt("stimulus_id")!=0) {
							stimulus.setId(rsBlockElement.getInt("stimulus_id"));
							stimulus.setName(rsBlockElement.getString("stimulus"));
						}
							
						blockElement.setQuestion(question);
						blockElement.setStimulus(stimulus);
						blockElementList.setBlockElement(blockElement);
						blockElementListArray.add(blockElementList);
					}
					
					blockListArray.add(blockList);
					block.setBlockElementListArray(blockElementListArray);
				}
				
				protocol.setBlockListArray(blockListArray);
				protocols.add(protocol);
			}

			return protocols;

		} catch (SQLException e) {
			throw new Exception(e.getMessage());
		}

	}

	public List<BlockElementList> getBlockElementsList() throws Exception {
		
		String getBlockElement = "SELECT bel.id blockElementList_id, "
				+ "be.id blockElement_id, "
				+ "be.question_id, "
				+ "be.stimulus_id, "
				+ "q.question, "
				+ "s.name stimulus "
				+ "FROM blockelement_list bel "
				+ "join blockelement be on (be.id=bel.blockElement_id) "
				+ "left join questions q on (q.id=be.question_id) "
				+ "left join stimulus s on (s.id=be.stimulus_id) "
				+ "order by stimulus_id, question_id";
		
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

			PreparedStatement pstmtBlockElement = conn.prepareStatement(getBlockElement);
			
			ResultSet rsBlockElement = pstmtBlockElement.executeQuery();
			
			List<BlockElementList> blockElementListArray = new ArrayList<BlockElementList>();
			
			while (rsBlockElement.next()) {
				BlockElementList blockElementList = new BlockElementList();
				blockElementList.setId(rsBlockElement.getInt("blockElementList_id"));

				BlockElement blockElement = new BlockElement();
				blockElement.setId(rsBlockElement.getInt("blockElement_id"));
				
				Question question = new Question();
				if (rsBlockElement.getInt("question_id")!=0) {
					question.setId(rsBlockElement.getInt("question_id"));
					question.setQuestion(rsBlockElement.getString("question"));
				}
				
				Stimulus stimulus = new Stimulus();
				if (rsBlockElement.getInt("stimulus_id")!=0) {
					stimulus.setId(rsBlockElement.getInt("stimulus_id"));
					stimulus.setName(rsBlockElement.getString("stimulus"));
				}
					
				blockElement.setQuestion(question);
				blockElement.setStimulus(stimulus);
				blockElementList.setBlockElement(blockElement);
				blockElementListArray.add(blockElementList);
			}

			return blockElementListArray;

		} catch (SQLException e) {
			throw new Exception(e.getMessage());
		}

	}
	
	public List<Block> getBlocks() throws Exception {
		
		String getBlock = "select bl.id blocklist_id, bl.block_id, b.block_name "
				+ "FROM block_list bl JOIN blocks b on (b.id=bl.block_id) "
				+ "order by bl.block_id";
		String getBlockElement = " SELECT bel.id blockElementList_id, "
				+ "be.id blockElement_id, "
				+ "be.question_id, "
				+ "be.stimulus_id, "
				+ "q.question, "
				+ "s.name stimulus "
				+ "FROM blockelement_list bel "
				+ "join blockelement be on (be.id=bel.blockElement_id) "
				+ "left join questions q on (q.id=be.question_id) "
				+ "left join stimulus s on (s.id=be.stimulus_id) "
				+ "where bel.block_id = ?"
				+ "order by bel.id";
		
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

			PreparedStatement pstmtBlock = conn.prepareStatement(getBlock);
			
			ResultSet rsBlock = pstmtBlock.executeQuery();
			
			List<Block> blockArray = new ArrayList<Block>();
			
			while (rsBlock.next()) {
				
				Block block = new Block();
				block.setId(rsBlock.getInt("block_id"));
				block.setName(rsBlock.getString("block_name"));
				
				PreparedStatement pstmtBlockElement = conn.prepareStatement(getBlockElement);
				pstmtBlockElement.setInt(1, block.getId());
				
				ResultSet rsBlockElement = pstmtBlockElement.executeQuery();
				
				List<BlockElementList> blockElementListArray = new ArrayList<BlockElementList>();
				
				while (rsBlockElement.next()) {
					BlockElementList blockElementList = new BlockElementList();
					blockElementList.setId(rsBlockElement.getInt("blockElementList_id"));

					BlockElement blockElement = new BlockElement();
					blockElement.setId(rsBlockElement.getInt("blockElement_id"));
					
					Question question = new Question();
					if (rsBlockElement.getInt("question_id")!=0) {
						question.setId(rsBlockElement.getInt("question_id"));
						question.setQuestion(rsBlockElement.getString("question"));
					}
					
					Stimulus stimulus = new Stimulus();
					if (rsBlockElement.getInt("stimulus_id")!=0) {
						stimulus.setId(rsBlockElement.getInt("stimulus_id"));
						stimulus.setName(rsBlockElement.getString("stimulus"));
					}
						
					blockElement.setQuestion(question);
					blockElement.setStimulus(stimulus);
					blockElementList.setBlockElement(blockElement);
					blockElementListArray.add(blockElementList);
				}
				
				block.setBlockElementListArray(blockElementListArray);
				blockArray.add(block);
			}

			return blockArray;

		} catch (SQLException e) {
			throw new Exception(e.getMessage());
		}

	}
    private Integer selectMaxSegmentListId(Connection conn) throws Exception {
     	 String selectMaxIdSql = "SELECT MAX(id) id FROM SEGMENT_LIST";
     	 ResultSet rs;
     	 
          try (PreparedStatement pstmt = conn.prepareStatement(selectMaxIdSql)) {
          	Statement stmt = conn.createStatement();
          	rs = stmt.executeQuery(selectMaxIdSql);
          } catch (SQLException e) {
         	 throw new Exception(e.getMessage());
          }
          return rs.getInt("id");
    }
    
    private Integer selectMaxBlockListId(Connection conn) throws Exception {
    	 String selectMaxIdSql = "SELECT MAX(id) id FROM BLOCK_LIST";
    	 ResultSet rs;
    	 
         try (PreparedStatement pstmt = conn.prepareStatement(selectMaxIdSql)) {
         	Statement stmt = conn.createStatement();
         	rs = stmt.executeQuery(selectMaxIdSql);
         } catch (SQLException e) {
        	 throw new Exception(e.getMessage());
         }
         return rs.getInt("id");
    }
    
    private Integer selectMaxBlockElementListId(Connection conn) throws Exception {
   	 String selectMaxIdSql = "SELECT MAX(id) id FROM BLOCKELEMENT_LIST";
   	 ResultSet rs;
   	 
        try (PreparedStatement pstmt = conn.prepareStatement(selectMaxIdSql)) {
        	Statement stmt = conn.createStatement();
        	rs = stmt.executeQuery(selectMaxIdSql);
        } catch (SQLException e) {
       	 throw new Exception(e.getMessage());
        }
        return rs.getInt("id");
   }
    
    public void deleteSegments(Integer protocolId) throws Exception{
		String deleteAllSegmentsSql = "DELETE FROM SEGMENTS WHERE ID IN (SELECT SEGMENT_ID FROM SEGMENT_LIST WHERE protocol_id = ?)";
		
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
            
            PreparedStatement pstmt = conn.prepareStatement(deleteAllSegmentsSql);
            pstmt.setInt(1, protocolId);
			
            pstmt.executeUpdate();

        } catch (SQLException e) {
        	throw new Exception(e.getMessage());
        }
		
	}
	
	public void deleteSegmentList(Integer protocolId) throws Exception{
		String deleteAllSegmentListSql = "DELETE FROM SEGMENT_LIST WHERE protocol_id = ?";
		
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
            
            PreparedStatement pstmt = conn.prepareStatement(deleteAllSegmentListSql);
            pstmt.setInt(1, protocolId);
			
            pstmt.executeUpdate();

        } catch (SQLException e) {
        	throw new Exception(e.getMessage());
        }
		
	}
	
	public void deleteBlockElement(Integer blockId) throws Exception{
		String deleteAllBlockElementSql = "DELETE FROM BLOCK_ELEMENT WHERE ID IN (SELECT BLOCKELEMENT_ID FROM BLOCKELEMENT_LIST WHERE block_id = ?)";
		
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
            
            PreparedStatement pstmt = conn.prepareStatement(deleteAllBlockElementSql);
            pstmt.setInt(1, blockId);
			
            pstmt.executeUpdate();

        } catch (SQLException e) {
        	throw new Exception(e.getMessage());
        }
		
	}
	
	public void deleteBlockElementList(Integer blockId) throws Exception{
		String deleteAllBlockElementListSql = "DELETE FROM BLOCKELEMENT_LIST WHERE block_id = ?";
		
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
            
            PreparedStatement pstmt = conn.prepareStatement(deleteAllBlockElementListSql);
            pstmt.setInt(1, blockId);
			
            pstmt.executeUpdate();

        } catch (SQLException e) {
        	throw new Exception(e.getMessage());
        }
		
	}
	
	public void deleteBlock(Integer blockId) throws Exception{
		String deleteBlockSql = "DELETE FROM BLOCKS WHERE ID IN (SELECT BLOCK_ID FROM BLOCK_LIST WHERE block_id = ?)";
		
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
            
            PreparedStatement pstmt = conn.prepareStatement(deleteBlockSql);
            pstmt.setInt(1, blockId);
			
            pstmt.executeUpdate();

        } catch (SQLException e) {
        	throw new Exception(e.getMessage());
        }
		
	}
	
	public void deleteBlockList(Integer blockId) throws Exception{
		String deleteBlockListSql = "DELETE FROM BLOCK_LIST WHERE block_id = ?";
		
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
            
            PreparedStatement pstmt = conn.prepareStatement(deleteBlockListSql);
            pstmt.setInt(1, blockId);
			
            pstmt.executeUpdate();

        } catch (SQLException e) {
        	throw new Exception(e.getMessage());
        }
		
	}
	
	public void saveSegmentList(Integer protocolId, String protocolName, List<SegmentList> segmentListArray) throws Exception {

		String insertProtocolSql = "INSERT OR REPLACE INTO PROTOCOLS (id, name) "
				+ "VALUES(?,?)";
		
		String insertSegmentListSql = "INSERT OR REPLACE INTO SEGMENT_LIST(id, segment_id, protocol_id) "
				+ "VALUES(?,?,?)";
		
		String insertSegmentsSql = "INSERT OR REPLACE INTO SEGMENTS(id, type, value_age_min, value_age_max, value_gender, value_profile) "
				+ "VALUES(?,?,?,?,?,?)";
		
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

			createProtocolTables(conn);

			try (PreparedStatement pstmt = conn.prepareStatement(insertProtocolSql)) {
				
				pstmt.setInt(1, protocolId);
				pstmt.setString(2, protocolName);

				pstmt.executeUpdate();
				
				deleteSegments(protocolId);
				deleteSegmentList(protocolId);
				
				int index=selectMaxSegmentListId(conn)+1;
				for (SegmentList segmentlist : segmentListArray) {
					try (PreparedStatement pstmtSegment = conn.prepareStatement(insertSegmentsSql)) {
						
						Segment segment = segmentlist.getSegment();
						
						pstmtSegment.setInt(1, index);
						pstmtSegment.setString(2, segment.getType());
						pstmtSegment.setObject(3, segment.getValueAgeMin(), java.sql.Types.INTEGER);
						pstmtSegment.setObject(4, segment.getValueAgeMax(), java.sql.Types.INTEGER);
						pstmtSegment.setString(5, segment.getValueGender());
						pstmtSegment.setString(6, segment.getValueProfile());
						
						pstmtSegment.executeUpdate();
						
					} catch (SQLException e) {
						throw new Exception(e.getMessage());
					}
					
					try (PreparedStatement pstmtSegmentlist = conn.prepareStatement(insertSegmentListSql)) {
						pstmtSegmentlist.setInt(1, index);
						pstmtSegmentlist.setInt(2, index++);
						pstmtSegmentlist.setInt(3, protocolId);
						
						pstmtSegmentlist.executeUpdate();
					} catch (SQLException e) {
						throw new Exception(e.getMessage());
					}
				
				}
			} catch (SQLException e) {
				throw new Exception(e.getMessage());
			}
		}
	}
	
	public void blockSegmentList(Integer protocolId, String protocolName, BlockList blockList) throws Exception {

		String insertProtocolSql = "INSERT OR REPLACE INTO PROTOCOLS (id, name) "
				+ "VALUES(?,?)";
		
		String insertBlockListSql = "INSERT OR REPLACE INTO BLOCK_LIST(id, block_id, protocol_id) "
				+ "VALUES(?,?,?)";
		
		String insertBlocksSql = "INSERT OR REPLACE INTO BLOCKS(id, name) "
				+ "VALUES(?,?)";
		
		String insertBlockElementListSql = "INSERT OR REPLACE INTO BLOCKELEMENT_LIST(id, block_id, blockElement_id) "
				+ "VALUES(?,?,?)";
		
		String insertBlockElementSql = "INSERT OR REPLACE INTO BLOCK_ELEMENT(id, question_id, stimulus_id) "
				+ "VALUES(?,?,?)";
		
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

			createProtocolTables(conn);

			try (PreparedStatement pstmt = conn.prepareStatement(insertProtocolSql)) {
				
				pstmt.setInt(1, protocolId);
				pstmt.setString(2, protocolName);

				pstmt.executeUpdate();

				
				int index=selectMaxBlockListId(conn)+1;

				deleteBlockElement(blockList.getBlock().getId());
				deleteBlockElementList(blockList.getBlock().getId());
				deleteBlock(blockList.getBlock().getId());
				deleteBlockList(blockList.getBlock().getId());
				
				try (PreparedStatement pstmtBlock = conn.prepareStatement(insertBlocksSql)) {
					
					Block block = blockList.getBlock();
					
					pstmtBlock.setInt(1, index);
					pstmtBlock.setString(2, block.getName());
					
					pstmtBlock.executeUpdate();
					
				} catch (SQLException e) {
					throw new Exception(e.getMessage());
				}
				
				try (PreparedStatement pstmtBlocklist = conn.prepareStatement(insertBlockListSql)) {
					pstmtBlocklist.setInt(1, index);
					pstmtBlocklist.setInt(2, index);
					pstmtBlocklist.setInt(3, protocolId);
					
					pstmtBlocklist.executeUpdate();
				} catch (SQLException e) {
					throw new Exception(e.getMessage());
				}
				
				int indexBlockElement=selectMaxBlockElementListId(conn)+1;
				for (BlockElementList blockElementlist : blockList.getBlock().getBlockElementListArray()) {
					try (PreparedStatement pstmtBlockElement = conn.prepareStatement(insertBlockElementSql)) {
						
						BlockElement blockElement = blockElementlist.getBlockElement();
						
						pstmtBlockElement.setInt(1, indexBlockElement);
						pstmtBlockElement.setObject(2, blockElement.getQuestion().getId(), java.sql.Types.INTEGER);
						pstmtBlockElement.setObject(3, blockElement.getQuestion().getId(), java.sql.Types.INTEGER);
						
						pstmtBlockElement.executeUpdate();
						
					} catch (SQLException e) {
						throw new Exception(e.getMessage());
					}
					
					try (PreparedStatement pstmtBlockElementlist = conn.prepareStatement(insertBlockElementListSql)) {
						pstmtBlockElementlist.setInt(1, indexBlockElement++);
						pstmtBlockElementlist.setInt(2, blockList.getBlock().getId());
						pstmtBlockElementlist.setInt(3, protocolId);
						
						pstmtBlockElementlist.executeUpdate();
					} catch (SQLException e) {
						throw new Exception(e.getMessage());
					}
				
				}
				
			} catch (SQLException e) {
				throw new Exception(e.getMessage());
			}
		}
	}
}