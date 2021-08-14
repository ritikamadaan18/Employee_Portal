package com.gfg.jbdl12employee.portal.repositories;



import com.gfg.jbdl12employee.portal.model.Roles;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends CrudRepository<Roles,Long> {
    Optional<Roles> findByRole(String role);

}
