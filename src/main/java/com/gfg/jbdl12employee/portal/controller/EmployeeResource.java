package com.gfg.jbdl12employee.portal.controller;


import com.gfg.jbdl12employee.portal.ForbiddenException;
import com.gfg.jbdl12employee.portal.manager.EmployeeManager;
import com.gfg.jbdl12employee.portal.model.EmployeeCreationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class EmployeeResource {
    @Autowired
    private EmployeeManager employeeManager;


    //creating an employee
    @PostMapping("/employee")
    @PreAuthorize("hasAnyAuthority('admin','hr')")
    ResponseEntity createEmployee(@RequestBody EmployeeCreationRequest employeeCreationRequest ){
        return ResponseEntity.ok(employeeManager.createEmployee(employeeCreationRequest));
    }


    //assigning subOrdinates to a manager
    @PutMapping("/manager/{manager_id}/subordinates")//this is for "employee_subordinates" table in database
    @PreAuthorize("hasAnyAuthority('admin','hr')")//only admin and hr can assign subOrdinates to manager
    ResponseEntity addSubOrdinates(@RequestBody List<String> subOrdinateIds,
                                   @PathVariable("manager_id")  String managerId){
        try {
            employeeManager.addSubOrdinates(subOrdinateIds,managerId);
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    //Giving rating to subOrdinates
    @PutMapping("/rating/{employee_id}/{rating}")
    @PreAuthorize("hasAnyAuthority('manager','hr','admin')")
    ResponseEntity provideRatingRating(@PathVariable("employee_id")  String employeeId,
                                       @PathVariable("rating") Float rating,
                                       Authentication authentication)//Authentication is to tell who is providing rating
    {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(),
                authentication.getCredentials(),authentication.getAuthorities());
        try {
            employeeManager.provideRating(employeeId,rating,usernamePasswordAuthenticationToken);
            return ResponseEntity.ok().build();
        }catch(ForbiddenException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }

        catch (Exception exception) {
            log.error(exception.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    //getting rating of subOrdinates (a subOrdinate can see only his rating,manager can see all)
    @GetMapping("/rating/{employee_id}/")
    @PreAuthorize("hasAnyAuthority('manager','hr','employee','admin')")
    ResponseEntity getRating(@PathVariable("employee_id")  String employeeId,
                                   Authentication authentication){

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(),
                authentication.getCredentials(),authentication.getAuthorities());
        //we are checking that if subOrdinate is trying to see rating of other subOrdinates,he isn't able to do so
        for(GrantedAuthority authority:usernamePasswordAuthenticationToken.getAuthorities()){
            if(authority.getAuthority().equalsIgnoreCase("employee")){
                if(!employeeId.equalsIgnoreCase(usernamePasswordAuthenticationToken.getName())){
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("employee are not eligible to get rating  for other employee");

                }
            }
        }
        return ResponseEntity.ok(employeeManager.getRating(employeeId));

    }




}
