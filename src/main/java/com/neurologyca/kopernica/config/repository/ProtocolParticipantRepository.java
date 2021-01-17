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
import com.neurologyca.kopernica.config.model.Participant;
import com.neurologyca.kopernica.config.model.Protocol;
import com.neurologyca.kopernica.config.model.Question;
import com.neurologyca.kopernica.config.model.Segment;
import com.neurologyca.kopernica.config.repository.ParticipantRepository;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProtocolParticipantRepository {

	@Value("${database.url}")
	private String databaseUrl;

	@Value("${base.path}")
	private String basePath;


	public List<Protocol> getProtocols() throws Exception {
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
	
	public List<Segment> getSegmentList(Integer protocol_id) throws Exception {
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
	
	
	public Integer applyConditions() throws Exception {
		List<Participant> participantList = new ArrayList<Participant>();
		List<Segment> segmentList = new ArrayList<Segment>();
		List<Protocol> protocolList = new ArrayList<Protocol>();
		ParticipantRepository participantRepository = new ParticipantRepository();
		boolean meetConditions;

		System.out.println("Entrando en apply Conditions");
		
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
				System.out.println("Protocolo " + protocol.getId());
				// Obtenemos el listado de condiciones de cada protocolo
				segmentList = getSegmentList(protocol.getId());		
				System.out.println("Num segmentos: " + segmentList.size());
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
						System.out.println("Cumple " + participant.toString());
						// Insertamos en la tabla participant_protocol
						insertParticipantProtocol(conn, participant.getId(), protocol.getId());
					}
					
				}
			}
			
		} catch (SQLException e) {
			throw new Exception(e.getMessage());
		}
		
		return 1;
	}
}