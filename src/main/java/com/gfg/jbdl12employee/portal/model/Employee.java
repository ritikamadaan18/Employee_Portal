package com.gfg.jbdl12employee.portal.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder //this is used when inheritance (multiple inheritance) is present
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)//to tell hibernate about the inheritance relation
                //this creates a single table for manager ,HR,employee (u don't have to create separate repositories)
@DiscriminatorColumn(name="user_type")//this will create a column in ur table to differentiate who is employee,HR or manager
@DiscriminatorValue(value="employee")//whenever u create employee it will get this value in table in ur database
public class Employee  implements UserDetails {
    @Column(nullable = false)
    String employeeId;
    @Column(nullable = false)
    String firstName;
    @Column(nullable = false)
    String lastName;

    Float rating;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    @Column(nullable = false)
    protected String password;
    @Column(nullable = false)
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    protected List<Roles> roles;
    @Column(nullable = false)
    protected Boolean accountNonExpired = true;
    @Column(nullable = false)
    protected Boolean accountNonLocked = true ;
    @Column(nullable = false)
    protected Boolean credentialsNonExpired = true;
    @Column(nullable = false)
    protected Boolean enabled = true;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles;
    }//this means that we are mapping roles with the authority of Spring i.e. we have to give authority based on role
    //of that employee,that is why we use roles in preAuthorize annotation in controller

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.employeeId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}