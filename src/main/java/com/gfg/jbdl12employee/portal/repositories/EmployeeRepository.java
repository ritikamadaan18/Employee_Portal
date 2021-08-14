package com.gfg.jbdl12employee.portal.repositories;

import com.gfg.jbdl12employee.portal.model.Employee;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface EmployeeRepository extends CrudRepository<Employee,Long> {

    Optional<Employee> findByEmployeeId(String employeeId);
}
