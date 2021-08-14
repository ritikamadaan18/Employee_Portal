package com.gfg.jbdl12employee.portal.manager;


import com.gfg.jbdl12employee.portal.ForbiddenException;
import com.gfg.jbdl12employee.portal.model.EmployeeCreationRequest;
import com.gfg.jbdl12employee.portal.model.Employee;
import com.gfg.jbdl12employee.portal.model.HR;
import com.gfg.jbdl12employee.portal.model.Manager;
import com.gfg.jbdl12employee.portal.model.Roles;
import com.gfg.jbdl12employee.portal.repositories.EmployeeRepository;
import com.gfg.jbdl12employee.portal.repositories.ManagerRepository;
import com.gfg.jbdl12employee.portal.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@Service
public class EmployeeDetailsServiceImpl implements EmployeeManager {

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override    //whenever user will do a login ,spring will first call loadUserByUsername
    public UserDetails loadUserByUsername(String employeeId) throws UsernameNotFoundException {

        //here we have to return obj of UserDetails(repository of user)
        return employeeRepository
                .findByEmployeeId(employeeId)
                .orElseThrow(()->new UsernameNotFoundException("employee is not found"));//this will check if employee is
                                                                                //present or not and if present it will
                                                                                //check password and do required validation


    }

    @Override
    public EmployeeCreationResponse createEmployee(EmployeeCreationRequest employeeCreationRequest) {
        switch (employeeCreationRequest.getType()){
            case HR:
                Optional<Roles> rolesOptional=roleRepository.findByRole("hr");
                Roles roles=null;
             if(!rolesOptional.isPresent()){
                roles=Roles.builder().role("hr").build();
            }
            else{
                roles=rolesOptional.get();
            }
                HR hr=HR.builder()
                        .employeeId(employeeCreationRequest.getFirstName()
                                .concat(".").concat(employeeCreationRequest.getLastName()))
                        .password(passwordEncoder.encode("password"))
                        .firstName(employeeCreationRequest.getFirstName())
                        .lastName(employeeCreationRequest.getLastName())
                        .roles(Arrays.asList(roles))
                        .accountNonExpired(true)
                        .accountNonLocked(true)
                        .credentialsNonExpired(true)
                        .enabled(true)
                        .build();
                Employee employee=employeeRepository.save(hr);
                return EmployeeCreationResponse.builder()
                        .employeeId(employee.getEmployeeId())
                        .password("password")
                        .build();

            case MANAGER:
                rolesOptional=roleRepository.findByRole("manager");
                roles=null;
                if(!rolesOptional.isPresent()){
                    roles=Roles.builder().role("manager").build();
                }
                else{
                    roles=rolesOptional.get();
                }
                Manager manager=Manager.builder()
                        .employeeId(employeeCreationRequest.getFirstName()
                                .concat(".".concat(employeeCreationRequest.getLastName())))
                        .password(passwordEncoder.encode("password"))
                        .firstName(employeeCreationRequest.getFirstName())
                        .lastName(employeeCreationRequest.getLastName())
                        .roles(Arrays.asList(roles))
                        .accountNonExpired(true)
                        .accountNonLocked(true)
                        .credentialsNonExpired(true)
                        .enabled(true)
                        .build();
                employee=employeeRepository.save(manager);
                return EmployeeCreationResponse.builder()
                        .employeeId(employee.getEmployeeId())
                        .password("password")
                        .build();

            case EMPLOYEE:
                rolesOptional=roleRepository.findByRole("employee");
                roles=null;
                if(!rolesOptional.isPresent()){
                    roles=Roles.builder().role("employee").build();
                }
                else{
                    roles=rolesOptional.get();
                }
                 employee=Employee.builder()
                        .employeeId(employeeCreationRequest.getFirstName()
                                .concat(".".concat(employeeCreationRequest.getLastName())))
                        .password(passwordEncoder.encode("password"))
                         .firstName(employeeCreationRequest.getFirstName())
                         .lastName(employeeCreationRequest.getLastName())
                        .roles(Arrays.asList(roles))
                        .accountNonExpired(true)
                        .accountNonLocked(true)
                        .credentialsNonExpired(true)
                        .enabled(true)
                        .build();
                employee=employeeRepository.save(employee);
                return EmployeeCreationResponse.builder()
                        .employeeId(employee.getEmployeeId())
                        .password("password")
                        .build();
        }
        return null;
    }

    @Override
    public void addSubOrdinates(List<String> subOrdinateIds, String managerId) throws Exception {
        Manager manager =  managerRepository.findByEmployeeId(managerId)
                .orElseThrow(
                        ()->new ForbiddenException("manager is not present."));
        List<Employee> subOrdinates = manager.getSubordinates();
        if(subOrdinates == null){
            subOrdinates = new ArrayList();
        }

        for(String employeeId : subOrdinateIds){
            Employee employee = employeeRepository.findByEmployeeId(employeeId).orElse(null);
            if(employee == null) {

                continue;//if employee is not present ,we will not add it as subOrdinate
            }
                subOrdinates.add(employee);
        }
        managerRepository.save(manager);

    }

    @Override
    public void provideRating(String employeeId,Float rating, UsernamePasswordAuthenticationToken loggedInUser) throws ForbiddenException {
        //loggedInUser ->will give you username ,password(credentials) etc. of the user trying to login(i.e. user whose
        // access token is being used)
        boolean valid=false;


  // Here we are checking that if a manager is assigning the rating,then the subOrdinate must be under that manager only
        for(GrantedAuthority authority: loggedInUser.getAuthorities()){
           if( authority.getAuthority().equalsIgnoreCase("manager")){
              Manager manager= managerRepository.findByEmployeeId(loggedInUser.getName())
                      .orElseThrow(()->new UsernameNotFoundException("manager is not found"));

              for(Employee employee:manager.getSubordinates()){
                  if(employee.getEmployeeId().equalsIgnoreCase(employeeId)){

                      valid=true;
                  }
              }
           }else if( authority.getAuthority().equalsIgnoreCase("hr")){
               //an hr shouldn't be able to give rating to himself or another hr
               Employee employee1=employeeRepository.findByEmployeeId(employeeId)
                              .orElseThrow(()->new UsernameNotFoundException("employee is not found"));
                      for(GrantedAuthority authority1: employee1.getAuthorities()) {
                          if (authority1.getAuthority().equalsIgnoreCase("hr")) {
                              throw new ForbiddenException("an HR can't give rating to an HR");
                          }
                      }
               valid=true;
           }
           else if( authority.getAuthority().equalsIgnoreCase("admin")){
               valid=true;
           }
        }
        if(!valid){
            throw new ForbiddenException("employee is not a subOrdinate of manager");
        }
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                                .orElseThrow(()->new UsernameNotFoundException("employee is not found"));
        employee.setRating(rating);
        employeeRepository.save(employee);

    }

    @Override
    public Float getRating(String employeeId) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(()->new UsernameNotFoundException("employee is not found"));
        return employee.getRating();

    }

}


//    @Override
//    public void signUp(UserRequest userRequest) throws Exception {
//
//        if(userRepository.findByUsername(userRequest.getUsername()).isPresent()){
//            throw new Exception("user is present");
//        }
//        //checking if role(e.g. user) is already present in table
//        //if it is already present we will not make the entry again in table
//        Optional<Roles> rolesOptional=roleRepository.findByRole("user");
//        Roles roles=null;
//        if(!rolesOptional.isPresent()){
//            roles=Roles.builder().role("user").build();
//        }
//        else{
//            roles=rolesOptional.get();
//        }
//        User user=User.builder()
//                .username(userRequest.getUsername())
//                .password(passwordEncoder.encode(userRequest.getPassword()))
//                .roles(Arrays.asList(roles))
//                .accountNonExpired(true)
//                .accountNonLocked(true)
//                .credentialsNonExpired(true)
//                .enabled(true)
//                .build();//any user should be able to do signup and will be assigned the role of user
//
//        userRepository.save(user);
//    }

