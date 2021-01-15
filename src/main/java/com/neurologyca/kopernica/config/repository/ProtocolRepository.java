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
import com.neurologyca.kopernica.config.model.Protocol;
import com.neurologyca.kopernica.config.model.Segment;

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

			for (Segment segment : protocol.getSegmentList().getSegmentArray()) {
				if (segment.getId() == 0) {
					//segment.setId(selectSegmentMaxId(conn) + 1);
				}
				pstmt.setInt(1, segment.getId());
				pstmt.setString(2, segment.getType());
				pstmt.setInt(3, segment.getValueAgeMin());
				pstmt.setInt(4, segment.getValueAgeMax());
				pstmt.setString(5, segment.getValueGender());
				pstmt.setString(6, segment.getValueProfile());

			}

			pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new Exception(e.getMessage());
		}
	}

	public List<Protocol> getProtocols() throws Exception {
		List<Protocol> protocols = new ArrayList<Protocol>();

		String getTypeStudy = "SELECT type FROM studies";

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

			PreparedStatement pstmt = conn.prepareStatement(getTypeStudy);

			ResultSet rs = pstmt.executeQuery();

			rs.next();

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