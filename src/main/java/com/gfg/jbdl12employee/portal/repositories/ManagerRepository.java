package com.gfg.jbdl12employee.portal.repositories;

import com.gfg.jbdl12employee.portal.model.Manager;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ManagerRepository extends CrudRepository<Manager,Long> {
    Optional<Manager> findByEmployeeId(String employeeId);
}
