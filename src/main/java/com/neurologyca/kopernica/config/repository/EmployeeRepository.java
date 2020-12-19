package com.neurologyca.kopernica.config.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.neurologyca.kopernica.config.model.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>{

}