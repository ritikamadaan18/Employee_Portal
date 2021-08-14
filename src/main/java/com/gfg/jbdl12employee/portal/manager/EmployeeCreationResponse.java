package com.gfg.jbdl12employee.portal.manager;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class EmployeeCreationResponse {
    //we created this class because we want employeeId and password to be generated automatically by some means
    private String employeeId;
    private String password;

}
