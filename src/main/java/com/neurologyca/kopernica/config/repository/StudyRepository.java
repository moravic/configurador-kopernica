package com.neurologyca.kopernica.config.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.neurologyca.kopernica.config.model.Study;

@Repository
public interface StudyRepository extends JpaRepository<Study, Long>{
}