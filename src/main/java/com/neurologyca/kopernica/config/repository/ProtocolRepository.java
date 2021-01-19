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

	private void insertProtocol(Connection conn, Protocol protocol) throws Exception {
		String insertSql = "INSERT OR REPLACE INTO studies(id, project, study, type) VALUES(1,?,?,?)";

		String create_SEGMENTS = "CREATE TABLE IF NOT EXISTS segments (id INTEGER PRIMARY KEY, type TEXT NOT NULL, value_age_min INTEGER NULL, value_age_max INTEGER NULL, value_gender TEXT NULL, value_profile TEXT NULL)";
		String create_SEGMENT_LIST = "CREATE TABLE IF NOT EXISTS segment_list (id INTEGER, segment_id INTEGER NOT NULL, protocol_id INTEGER NOT NULL, FOREIGN KEY(segment_id) REFERENCES segments(id), FOREIGN KEY(protocol_id) REFERENCES protocols(id), PRIMARY KEY(id))";
		String create_BLOCKELEMENT = "CREATE TABLE IF NOT EXISTS blockelement (id INTEGER PRIMARY KEY, question_id INTEGER NULL, stimulus_id INTEGER NULL, FOREIGN KEY(stimulus_id) REFERENCES stimulus(id), FOREIGN KEY(question_id) REFERENCES questions(id))";
		String create_BLOCKELEMENT_LIST = "CREATE TABLE IF NOT EXISTS blockelement_list (id INTEGER, block_id INTEGER NOT NULL, blockElement_id INTEGER NOT NULL, FOREIGN KEY(block_id) REFERENCES blocks(id), FOREIGN KEY(blockElement_id) REFERENCES blockElement(id), PRIMARY KEY(id))";
		String create_BLOCKS = "CREATE TABLE IF NOT EXISTS blocks (id INTEGER PRIMARY KEY, block_name TEXT NOT NULL)";
		String create_BLOCK_LIST = "CREATE TABLE IF NOT EXISTS block_list (id INTEGER, block_id INTEGER NOT NULL, protocol_id INTEGER NOT NULL, FOREIGN KEY(block_id) REFERENCES blocks(id), FOREIGN KEY(protocol_id) REFERENCES protocols(id), PRIMARY KEY(id))";
		String create_PROTOCOLS = "CREATE TABLE IF NOT EXISTS protocols (id INTEGER PRIMARY KEY, name TEXT NOT NULL)";
		String create_PARTICIPANT_PROTOCOL = "CREATE TABLE IF NOT EXISTS participant_protocol (id INTEGER PRIMARY KEY, participant_id INTEGER NOT NULL, protocol_id INTEGER NOT NULL, FOREIGN KEY(participant_id) REFERENCES participants(id), FOREIGN KEY(protocol_id) REFERENCES protocols(id))";
		String create_PARTICIPANT_PROTOCOL_ORDER = "CREATE TABLE IF NOT EXISTS participant_protocol_order (id INTEGER PRIMARY KEY, participant_protocol_id INTEGER NOT NULL, blockElement_id INTEGER NOT NULL, no_order INTEGER NOT NULL, FOREIGN KEY(participant_protocol_id) REFERENCES participant_protocol(id), FOREIGN KEY(blockElement_id) REFERENCES blockElement(id))";
		
		String insertSegmentsSql = "INSERT OR REPLACE INTO SEGMENTS(id, type, value_age_min, value_age_max, value_gender, value_profile) "
				+ "VALUES(?,?,?,?,?,?)";

		try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
			if (protocol.getId() == 0) {
				//protocol.setId(selectProtocolMaxId(conn) + 1);
			}

			//for (Segment segment : protocol.getSegmentList().getSegmentArray()) {
				//if (segment.getId() == 0) {
					//segment.setId(selectSegmentMaxId(conn) + 1);
				//}
		/*		pstmt.setInt(1, segment.getId());
				pstmt.setString(2, segment.getType());
				pstmt.setInt(3, segment.getValueAgeMin());
				pstmt.setInt(4, segment.getValueAgeMax());
				pstmt.setString(5, segment.getValueGender());
				pstmt.setString(6, segment.getValueProfile());*/

			//}

			pstmt.executeUpdate();
		} catch (SQLException e) {
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
				+ "FROM segment_list sl JOIN segment s on (sl.segment_id=s.id) "
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
				protocol.setId(rsProtocol.getInt("protocol_id"));
				protocol.setName(rsProtocol.getString("protocol_name"));
				
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

	public Integer saveProtocol(Protocol protocol) throws Exception {

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
			insertProtocol(conn, protocol);

		} catch (SQLException e) {
			throw new Exception(e.getMessage());
		}

		return 1;
	}
}