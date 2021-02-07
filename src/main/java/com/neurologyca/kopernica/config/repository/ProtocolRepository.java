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
import com.neurologyca.kopernica.config.model.Group;
import com.neurologyca.kopernica.config.model.GroupList;
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

	public void createProtocolTables(Connection conn) throws Exception {
		String create_GROUPS = "CREATE TABLE IF NOT EXISTS groups (id INTEGER PRIMARY KEY, name TEXT NOT NULL)";
		String create_GROUP_LIST="CREATE TABLE IF NOT EXISTS group_list (id INTEGER PRIMARY KEY, group_id INTEGER NULL, protocol_id INTEGER NULL, FOREIGN KEY(protocol_id) REFERENCES protocols(id), FOREIGN KEY(group_id) REFERENCES groups(id))";
		String create_SEGMENTS = "CREATE TABLE IF NOT EXISTS segments (id INTEGER PRIMARY KEY, type TEXT NOT NULL, value_age_min INTEGER NULL, value_age_max INTEGER NULL, value_gender TEXT NULL, value_profile TEXT NULL)";
		String create_SEGMENT_LIST = "CREATE TABLE IF NOT EXISTS segment_list (id INTEGER, segment_id INTEGER NOT NULL, protocol_id INTEGER NOT NULL, FOREIGN KEY(segment_id) REFERENCES segments(id), FOREIGN KEY(protocol_id) REFERENCES protocols(id), PRIMARY KEY(id))";
		String create_BLOCKELEMENT = "CREATE TABLE IF NOT EXISTS blockelement (id INTEGER PRIMARY KEY, question_id INTEGER NULL, stimulus_id INTEGER NULL, FOREIGN KEY(stimulus_id) REFERENCES stimulus(id), FOREIGN KEY(question_id) REFERENCES questions(id))";
		String create_BLOCKELEMENT_LIST = "CREATE TABLE IF NOT EXISTS blockelement_list (id INTEGER, block_id INTEGER NOT NULL, blockElement_id INTEGER NOT NULL, FOREIGN KEY(block_id) REFERENCES blocks(id), FOREIGN KEY(blockElement_id) REFERENCES blockElement(id), PRIMARY KEY(id))";
		String create_BLOCKS = "CREATE TABLE IF NOT EXISTS blocks (id INTEGER PRIMARY KEY, block_name TEXT NOT NULL)";
		String create_BLOCK_LIST = "CREATE TABLE IF NOT EXISTS block_list (id INTEGER, block_id INTEGER NOT NULL, protocol_id INTEGER NOT NULL, FOREIGN KEY(block_id) REFERENCES blocks(id), FOREIGN KEY(protocol_id) REFERENCES protocols(id), PRIMARY KEY(id))";
		String create_PROTOCOLS = "CREATE TABLE IF NOT EXISTS protocols (id INTEGER PRIMARY KEY, name TEXT NOT NULL, locked INT NULL)";
		String create_PARTICIPANT_PROTOCOL = "CREATE TABLE IF NOT EXISTS participant_protocol (id INTEGER PRIMARY KEY, participant_id INTEGER NOT NULL, protocol_id INTEGER NOT NULL, FOREIGN KEY(participant_id) REFERENCES participants(id), FOREIGN KEY(protocol_id) REFERENCES protocols(id))";
		String create_PARTICIPANT_PROTOCOL_ORDER = "CREATE TABLE IF NOT EXISTS participant_protocol_order (id INTEGER PRIMARY KEY, participant_protocol_id INTEGER NOT NULL, block_id INTEGER NOT NULL, blockElement_id INTEGER NOT NULL, no_order INTEGER NOT NULL, FOREIGN KEY(participant_protocol_id) REFERENCES participant_protocol(id), FOREIGN KEY(blockElement_id) REFERENCES blockElement(id))";
		String insert_GROUP = "INSERT OR REPLACE INTO groups(id, name) " + "VALUES(1,'Todos')";
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
	//	try (Connection conn = DriverManager.getConnection(AppController.fullDatabaseUrl)) {
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
			stmt.execute(create_GROUPS);
			stmt.execute(create_GROUP_LIST);
			stmt.execute(insert_GROUP);

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
        
		String getProtocols = "select id, name, locked FROM protocols order by id";
		String getSegment = "select sl.id segmentlist_id, s.id, s.type, s.value_age_min, s.value_age_max, s.value_gender, s.value_profile "
				+ "FROM segment_list sl JOIN segments s on (sl.segment_id=s.id) "
				+ "WHERE protocol_id=? "
				+ "order by s.id";
		String getGroup = "select gl.id grouplist_id, g.id, g.name "
				+ "FROM group_list gl JOIN groups g on (gl.group_id=g.id) "
				+ "WHERE protocol_id=? "
				+ "order by g.id";
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
			
			createProtocolTables(conn);
			
			PreparedStatement pstmtProtocol = conn.prepareStatement(getProtocols);

			ResultSet rsProtocol = pstmtProtocol.executeQuery();

			while (rsProtocol.next()) {
				Protocol protocol = new Protocol();
				protocol.setId(rsProtocol.getInt("id"));
				protocol.setName(rsProtocol.getString("name"));
				protocol.setLocked(rsProtocol.getInt("locked"));
				
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
				
				PreparedStatement pstmtGroup = conn.prepareStatement(getGroup);
				pstmtGroup.setInt(1, protocol.getId());
				
				ResultSet rsGroup = pstmtGroup.executeQuery();
				
				List<GroupList> groupListArray = new ArrayList<GroupList>();
				
				while (rsGroup.next()) {
					GroupList groupList = new GroupList();
					Group group = new Group();
					
					groupList.setId(rsGroup.getInt("grouplist_id"));
					group.setId(rsGroup.getInt("id"));
					group.setName(rsGroup.getString("name"));
					groupList.setGroup(group);
					groupListArray.add(groupList);
				}
				
				protocol.setGroupListArray(groupListArray);
			
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
			
			conn.close();
            
			if (protocols.size()==0) { // Protocolo por defecto
				Protocol p = new Protocol();
				p.setId(1);
				p.setName("Protocolo 1");
				List<SegmentList> segmentListArray = new ArrayList<SegmentList>();
				p.setSegmentListArray(segmentListArray);
				List<BlockList> blockListArray = new ArrayList<BlockList>();
				p.setBlockListArray(blockListArray);
				List<GroupList> groupListArray = new ArrayList<GroupList>();
				p.setGroupListArray(groupListArray);
				protocols.add(p);
			}
				
			
			return protocols;

		} catch (SQLException e) {
			throw new Exception(e.getMessage());
		}

	}

	public List<BlockElementList> getBlockElementsList() throws Exception {
		
		String getBlockElement = "SELECT row_number() over (order by stimulus_id, question_id) blockElementList_id, "
				+ "blockElement_id, "
				+ "question_id, "
				+ "stimulus_id, "
				+ "question, "
				+ "stimulus "
				+ "FROM (SELECT null blockElementList_id, "
				+ "null blockElement_id, "
				+ "q.id question_id, "
				+ "null stimulus_id, "
				+ "q.question, "
				+ "null stimulus "
				+ "FROM questions q "
				+ "union all "
				+ "SELECT null blockElementList_id, "
				+ "null blockElement_id, "
				+ "null question_id, "
				+ "s.id stimulus_id, "
				+ "null question, "
				+ "s.name stimulus "
				+ "FROM stimulus s) "
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
    
	private Integer selectMaxSegmentListId() throws Exception {
		String selectMaxIdSql = "SELECT MAX(id) id FROM SEGMENT_LIST";
		ResultSet rs;
		try (Connection conn = DriverManager.getConnection(AppController.fullDatabaseUrl)) {

			try (Statement stmt = conn.createStatement()) {
				rs = stmt.executeQuery(selectMaxIdSql);
				
				int id = rs.getInt("id");
				
				conn.close();
				
				return id;
			} catch (SQLException e) {
				throw new Exception(e.getMessage());
			}
		} catch (SQLException e) {
			throw new Exception(e.getMessage());
		}
    }
    
	private Integer selectMaxSegmentId() throws Exception {
		String selectMaxIdSql = "SELECT MAX(id) id FROM SEGMENTS";
		ResultSet rs;
		try (Connection conn = DriverManager.getConnection(AppController.fullDatabaseUrl)) {

			try (Statement stmt = conn.createStatement()) {
				rs = stmt.executeQuery(selectMaxIdSql);
				
				int id = rs.getInt("id");
				
				conn.close();
				
				return id;
			} catch (SQLException e) {
				throw new Exception(e.getMessage());
			}
		} catch (SQLException e) {
			throw new Exception(e.getMessage());
		}
    }
    private Integer selectMaxBlockListId() throws Exception {
		String selectMaxIdSql = "SELECT MAX(id) id FROM BLOCK_LIST";
		ResultSet rs;
		try (Connection conn = DriverManager.getConnection(AppController.fullDatabaseUrl)) {

			try (Statement stmt = conn.createStatement()){
				rs = stmt.executeQuery(selectMaxIdSql);
				
				int id = rs.getInt("id");
				
				conn.close();
				
				return id;
			} catch (SQLException e) {
				throw new Exception(e.getMessage());
			}
		} catch (SQLException e) {
			throw new Exception(e.getMessage());
		}
    }
    
    private Integer selectMaxBlockId() throws Exception {
		String selectMaxIdSql = "SELECT MAX(id) id FROM BLOCKS";
		ResultSet rs;
		try (Connection conn = DriverManager.getConnection(AppController.fullDatabaseUrl)) {

			try (Statement stmt = conn.createStatement()) {
				rs = stmt.executeQuery(selectMaxIdSql);
				
				int id = rs.getInt("id");
				
				conn.close();
				
				return id;
			} catch (SQLException e) {
				throw new Exception(e.getMessage());
			}
		} catch (SQLException e) {
			throw new Exception(e.getMessage());
		}

   }
    
    private Integer selectMaxBlockElementListId() throws Exception {
		String selectMaxIdSql = "SELECT MAX(id) id FROM BLOCKELEMENT_LIST";
		ResultSet rs;
		try (Connection conn = DriverManager.getConnection(AppController.fullDatabaseUrl)) {

			try (Statement stmt = conn.createStatement()) {
				rs = stmt.executeQuery(selectMaxIdSql);
				
				return rs.getInt("id");
			} catch (SQLException e) {
				throw new Exception(e.getMessage());
			}
			
		} catch (SQLException e) {
			throw new Exception(e.getMessage());
		}
   }
    
    public void deleteSegments(Connection conn, Integer protocolId, Integer segmentId) throws Exception{
		String deleteAllSegmentsSql = "DELETE FROM SEGMENTS WHERE ID = ?";
		
		if (AppController.fullDatabaseUrl==null) {
			throw new Exception("Debe estar seleccionado un proyecto y un estudio");
		}

        PreparedStatement pstmt = conn.prepareStatement(deleteAllSegmentsSql);
        pstmt.setInt(1, segmentId);
        
        pstmt.executeUpdate();

	}
	
	public void deleteSegmentList(Connection conn, Integer protocolId, Integer segmentListId) throws Exception{
		String deleteAllSegmentListSql = "DELETE FROM SEGMENT_LIST WHERE protocol_id = ? and ID = ?";
		
		if (AppController.fullDatabaseUrl==null) {
			throw new Exception("Debe estar seleccionado un proyecto y un estudio");
		}

        PreparedStatement pstmt = conn.prepareStatement(deleteAllSegmentListSql);
        pstmt.setInt(1, protocolId);
        pstmt.setInt(2, segmentListId);
		
        pstmt.executeUpdate();
		
	}
	
	public void deleteBlockElement(Connection conn, Integer blockId) throws Exception{
		String deleteAllBlockElementSql = "DELETE FROM BLOCKELEMENT WHERE ID IN (SELECT BLOCKELEMENT_ID FROM BLOCKELEMENT_LIST WHERE block_id = ?)";
		
		if (AppController.fullDatabaseUrl==null) {
			throw new Exception("Debe estar seleccionado un proyecto y un estudio");
		}

        PreparedStatement pstmt = conn.prepareStatement(deleteAllBlockElementSql);
        pstmt.setInt(1, blockId);
		
        pstmt.executeUpdate();
	}
	
	public void deleteBlockElementId(Connection conn, Integer blockElementId) throws Exception{
		String deleteAllBlockElementSql = "DELETE FROM BLOCKELEMENT WHERE ID = ?";
		
		if (AppController.fullDatabaseUrl==null) {
			throw new Exception("Debe estar seleccionado un proyecto y un estudio");
		}
     
        PreparedStatement pstmt = conn.prepareStatement(deleteAllBlockElementSql);
        pstmt.setInt(1, blockElementId);
		
        pstmt.executeUpdate();
		
	}
	
	public void deleteBlockElementList(Connection conn, Integer blockId) throws Exception{
		String deleteAllBlockElementListSql = "DELETE FROM BLOCKELEMENT_LIST WHERE block_id = ?";
		
		if (AppController.fullDatabaseUrl==null) {
			throw new Exception("Debe estar seleccionado un proyecto y un estudio");
		}
		
        PreparedStatement pstmt = conn.prepareStatement(deleteAllBlockElementListSql);
        pstmt.setInt(1, blockId);
		
        pstmt.executeUpdate();
		
	}
	
	public void deleteBlockElementListId(Connection conn, Integer blockElementListId) throws Exception{
		String deleteAllBlockElementListSql = "DELETE FROM BLOCKELEMENT_LIST WHERE ID = ?";
		
		if (AppController.fullDatabaseUrl==null) {
			throw new Exception("Debe estar seleccionado un proyecto y un estudio");
		}

        PreparedStatement pstmt = conn.prepareStatement(deleteAllBlockElementListSql);
        pstmt.setInt(1, blockElementListId);
		
        pstmt.executeUpdate();
	
	}
	public void deleteBlock(Connection conn, Integer blockId) throws Exception{
		String deleteBlockSql = "DELETE FROM BLOCKS WHERE ID = ?";
		
		if (AppController.fullDatabaseUrl==null) {
			throw new Exception("Debe estar seleccionado un proyecto y un estudio");
		}

        PreparedStatement pstmt = conn.prepareStatement(deleteBlockSql);
        pstmt.setInt(1, blockId);
		
        pstmt.executeUpdate();

	}
	
	public void deleteBlockList(Connection conn, Integer blockId) throws Exception{
		String deleteBlockListSql = "DELETE FROM BLOCK_LIST WHERE block_id = ?";
		
		if (AppController.fullDatabaseUrl==null) {
			throw new Exception("Debe estar seleccionado un proyecto y un estudio");
		}

        PreparedStatement pstmt = conn.prepareStatement(deleteBlockListSql);
        pstmt.setInt(1, blockId);
		
        pstmt.executeUpdate();
		
	}
	
	public SegmentList saveSegmentList(Integer protocolId, String protocolName, SegmentList segmentList) throws Exception {

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

				int segmentId;
				int segmentListId;
				try {
					Connection connSegment = DriverManager.getConnection(AppController.fullDatabaseUrl);
					PreparedStatement pstmtSegment = connSegment.prepareStatement(insertSegmentsSql);

					Segment segment = segmentList.getSegment();

					if (segment.getId() == null) {
						segmentId = selectMaxSegmentId() + 1;
						segment.setId(segmentId);
					} else
						segmentId = segment.getId();

					pstmtSegment.setInt(1, segmentId);
					pstmtSegment.setString(2, segment.getType());
					pstmtSegment.setObject(3, segment.getValueAgeMin(), java.sql.Types.INTEGER);
					pstmtSegment.setObject(4, segment.getValueAgeMax(), java.sql.Types.INTEGER);
					pstmtSegment.setString(5, segment.getValueGender());
					pstmtSegment.setString(6, segment.getValueProfile());

					pstmtSegment.executeUpdate();

				} catch (SQLException e) {
					throw new Exception(e.getMessage());
				}
				try {
					Connection connSegmentList = DriverManager.getConnection(AppController.fullDatabaseUrl);
					PreparedStatement pstmtSegmentlist = connSegmentList.prepareStatement(insertSegmentListSql);
					if (segmentList.getId() == null) {
						segmentListId = selectMaxSegmentListId() + 1;
						segmentList.setId(segmentListId);
					}

					pstmtSegmentlist.setInt(1, segmentList.getId());
					pstmtSegmentlist.setInt(2, segmentId);
					pstmtSegmentlist.setInt(3, protocolId);

					pstmtSegmentlist.executeUpdate();

				} catch (SQLException e) {
					throw new Exception(e.getMessage());
				}
				return segmentList;
			}
		}
	}
	
	public GroupList saveGroupList(Integer protocolId, String protocolName, GroupList groupList) throws Exception {

		String insertProtocolSql = "INSERT OR REPLACE INTO PROTOCOLS (id, name) "
				+ "VALUES(?,?)";
		
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

				return insertGroupList(conn, groupList, protocolId);
			}
		}
	}
	
	private GroupList insertGroupList(Connection conn, GroupList groupList, Integer protocolId) throws Exception {
	   	 String insertSql = "INSERT OR REPLACE INTO group_list(id, group_id, protocol_id) "
	   	 		+ "VALUES(?,?,?)";
	   	 
	        try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {      	 
	       	 if (groupList.getId()==0) {
	       		 groupList.setId(selectMaxGroupListId(conn)+1);
	       	 }
	      	 
	            pstmt.setInt(1, groupList.getId());
	            pstmt.setInt(2, groupList.getGroup().getId());
	            pstmt.setInt(3, protocolId);
	            pstmt.executeUpdate();
	        } catch (SQLException e) {
	       	 throw new Exception(e.getMessage());
	        }
	        
	        return groupList;
	 }
	
	private Integer selectMaxGroupListId(Connection conn) throws Exception {
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
	
	public void deleteSegmentList(Integer protocolId, String protocolName, Integer segmentListId, Integer segmentId) throws Exception {
		
		if (AppController.fullDatabaseUrl == null) {
			throw new Exception("Debe estar seleccionado un proyecto y un estudio");
		}
		
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
            
            createProtocolTables(conn);
            deleteSegmentList(conn, protocolId, segmentListId);
            deleteSegments(conn, protocolId, segmentId);
            
			conn.close();
		} catch (SQLException e) {
			throw new Exception(e.getMessage());
		}
	}
	
	public void deleteBlockList(Integer protocolId, String protocolName, Integer blockListId, Integer blockId) throws Exception {
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
			deleteBlockElement(conn, blockId);
			deleteBlockElementList(conn, blockId);
			deleteBlockList(conn, blockListId);	
			deleteBlock(conn, blockId);
			
			conn.close();
		} catch (SQLException e) {
			throw new Exception(e.getMessage());
		} 
	}
	
	public void deleteBlockElementList(Integer protocolId, String protocolName, Integer blockElementListId, Integer blockElementId) throws Exception {
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
			deleteBlockElementListId(conn, blockElementListId);
			deleteBlockElementId(conn, blockElementId);
			conn.close();
		} catch (SQLException e) {
			throw new Exception(e.getMessage());
		} 
	}
	
	public BlockList saveBlockList(Integer protocolId, String protocolName, BlockList blockList) throws Exception {

		String insertProtocolSql = "INSERT OR REPLACE INTO PROTOCOLS (id, name) "
				+ "VALUES(?,?)";
		
		String insertBlockListSql = "INSERT OR REPLACE INTO BLOCK_LIST(id, block_id, protocol_id) "
				+ "VALUES(?,?,?)";
		
		String insertBlocksSql = "INSERT OR REPLACE INTO BLOCKS(id, block_name) "
				+ "VALUES(?,?)";
		
		String insertBlockElementListSql = "INSERT OR REPLACE INTO BLOCKELEMENT_LIST(id, block_id, blockElement_id) "
				+ "VALUES(?,?,?)";
		
		String insertBlockElementSql = "INSERT OR REPLACE INTO BLOCKELEMENT(id, question_id, stimulus_id) "
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
				
				if (blockList.getBlock().getBlockElementListArray()==null || blockList.getBlock().getBlockElementListArray().size()==0)
					return blockList;
				
				int index;
				int indexBlock;
				if (blockList.getId()==null) {
				   index=selectMaxBlockListId()+1;
				   indexBlock=selectMaxBlockId()+1;
				}else  {
					index=blockList.getId();
					indexBlock=blockList.getBlock().getId();
				};
				
				try ( Connection connBlock = DriverManager.getConnection(AppController.fullDatabaseUrl)) {
					
					try (PreparedStatement pstmtBlock = connBlock.prepareStatement(insertBlocksSql)) {
					
					Block block = blockList.getBlock();
					
					blockList.getBlock().setId(indexBlock);
					pstmtBlock.setInt(1, indexBlock);
					pstmtBlock.setString(2, block.getName());
					
					pstmtBlock.executeUpdate();
					
					} catch (SQLException e) {
						throw new Exception(e.getMessage());
					}
				} catch (SQLException e) {
					throw new Exception(e.getMessage());
				}
				
				try ( Connection connBlockList = DriverManager.getConnection(AppController.fullDatabaseUrl)) {
					
					try (PreparedStatement pstmtBlocklist = connBlockList.prepareStatement(insertBlockListSql)) {
						blockList.setId(index);
						pstmtBlocklist.setInt(1, index);
						pstmtBlocklist.setInt(2, indexBlock);
						pstmtBlocklist.setInt(3, protocolId);
						
						pstmtBlocklist.executeUpdate();
					} catch (SQLException e) {
						throw new Exception(e.getMessage());
					}
				} catch (SQLException e) {
					throw new Exception(e.getMessage());
				}
				
				int indexBlockElement=selectMaxBlockElementListId()+1;
				for (BlockElementList blockElementlist : blockList.getBlock().getBlockElementListArray()) {
					try ( Connection connBlockElement = DriverManager.getConnection(AppController.fullDatabaseUrl)) {
						
						try (PreparedStatement pstmtBlockElement = connBlockElement.prepareStatement(insertBlockElementSql)) {
							
							BlockElement blockElement = blockElementlist.getBlockElement();
							
							blockElementlist.setId(indexBlockElement);
							blockElement.setId(indexBlockElement);
							pstmtBlockElement.setInt(1, indexBlockElement);
							pstmtBlockElement.setObject(2, blockElement.getQuestion().getId(), java.sql.Types.INTEGER);
							pstmtBlockElement.setObject(3, blockElement.getStimulus().getId(), java.sql.Types.INTEGER);
							
							pstmtBlockElement.executeUpdate();
							
						} catch (SQLException e) {
							throw new Exception(e.getMessage());
						}
					} catch (SQLException e) {
						throw new Exception(e.getMessage());
					}
					
					try ( Connection connBlockElementList = DriverManager.getConnection(AppController.fullDatabaseUrl)) {
						
						try (PreparedStatement pstmtBlockElementlist = connBlockElementList.prepareStatement(insertBlockElementListSql)) {
							pstmtBlockElementlist.setInt(1, indexBlockElement);
							pstmtBlockElementlist.setInt(2, indexBlock);
							pstmtBlockElementlist.setInt(3, indexBlockElement);
							
							pstmtBlockElementlist.executeUpdate();
						} catch (SQLException e) {
							throw new Exception(e.getMessage());
						}
					} catch (SQLException e) {
						throw new Exception(e.getMessage());
					}
				}
				
			} catch (SQLException e) {
				throw new Exception(e.getMessage());
			} 
			
			conn.close();
			return blockList;
		}
	}
	
	public void deleteProtocol(Integer protocolId) throws Exception{
		String selectSegmentListSql = "SELECT ID, SEGMENT_ID FROM SEGMENT_LIST WHERE PROTOCOL_ID = ?";
		String selectBlockElementListSql = "SELECT ID, BLOCKELEMENT_ID FROM BLOCKELEMENT_LIST WHERE BLOCK_ID = ?";
		String selectBlockListSql = "SELECT BL.ID, BL.BLOCK_ID, COALESCE((SELECT 0 FROM BLOCK_LIST BL2 WHERE BL2.BLOCK_ID = BL.BLOCK_ID AND BL2.PROTOCOL_ID!=?),1) BORRAR_BLOCK FROM BLOCK_LIST BL WHERE PROTOCOL_ID = ?";

		String deleteProtocolSql = "DELETE FROM PROTOCOLS WHERE ID = ?";

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

			PreparedStatement pstmt = conn.prepareStatement(selectSegmentListSql);
			pstmt.setInt(1, protocolId);

			ResultSet rst = pstmt.executeQuery();

			while (rst.next()) {
				deleteSegmentList(conn, protocolId, rst.getInt("ID"));
				deleteSegments(conn, protocolId, rst.getInt("SEGMENT_ID"));
			}

			conn.close();
		} catch (SQLException e) {
			throw new Exception(e.getMessage());
		}

		try (Connection conn = DriverManager.getConnection(AppController.fullDatabaseUrl)) {
			if (conn != null) {
				// Si no existe se crea la bbdd
				DatabaseMetaData meta = conn.getMetaData();
				// System.out.println("The driver name is " + meta.getDriverName());
				// System.out.println("A new database has been created.");
			}

			PreparedStatement pstmt = conn.prepareStatement(selectBlockListSql);
			pstmt.setInt(1, protocolId);
			pstmt.setInt(2, protocolId);

			ResultSet rst = pstmt.executeQuery();

			while (rst.next()) {

				Integer blockId = rst.getInt("BLOCK_ID");
				if (rst.getInt("BORRAR_BLOCK")==1) {
					deleteBlockList(conn, blockId);
				} else {
					PreparedStatement pstmt2 = conn.prepareStatement(selectBlockElementListSql);
					pstmt2.setInt(1, blockId);

					ResultSet rst2 = pstmt2.executeQuery();

					while (rst2.next()) {
						deleteBlockElementList(conn, blockId);
						deleteBlockElement(conn, blockId);
					}

					deleteBlock(conn, blockId);
				}
			}

			PreparedStatement pstmt3 = conn.prepareStatement(deleteProtocolSql);
			pstmt3.setInt(1, protocolId);

			pstmt3.executeUpdate();

			conn.close();
		} catch (SQLException e) {
			throw new Exception(e.getMessage());
		}
	}
	
	public void saveProtocol(Integer protocolId, String protocolName) throws Exception {

		String insertProtocolSql = "INSERT OR REPLACE INTO PROTOCOLS (id, name) "
				+ "VALUES(?,?)";
			
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
		
			} catch (SQLException e) {
				throw new Exception(e.getMessage());
			} 
			
			conn.close();
		}
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
}