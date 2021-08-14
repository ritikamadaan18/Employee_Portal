package com.gfg.jbdl12employee.portal.manager;


import com.gfg.jbdl12employee.portal.ForbiddenException;
import com.gfg.jbdl12employee.portal.model.EmployeeCreationRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface EmployeeManager extends UserDetailsService {
    EmployeeCreationResponse createEmployee(EmployeeCreationRequest employeeCreationRequest);
    void addSubOrdinates(List<String> subOrdinateIds,String managerId) throws Exception;
    void provideRating(String subOrdinate,Float rating,UsernamePasswordAuthenticationToken managerId) throws ForbiddenException;
    Float getRating(String employee_id);

}
