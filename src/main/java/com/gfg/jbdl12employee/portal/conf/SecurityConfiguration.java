package com.gfg.jbdl12employee.portal.conf;

import com.gfg.jbdl12employee.portal.manager.EmployeeManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)//to prevent writing every authorization in this page we can directly write it with
                                                //with APIs in Controller class using preAuthorize annotation
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return  new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        //AuthenticationManager->this is an IF and is a method in WebSecurityConfigurerAdapter class
        return super.authenticationManager();
    }

    @Autowired
    public EmployeeManager employeeManager;



    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(employeeManager)
                .passwordEncoder(passwordEncoder())
                .and()
                .inMemoryAuthentication()
                .passwordEncoder(passwordEncoder())
                .withUser("admin")
                .password(passwordEncoder().encode("password"))
                .authorities("admin");//this is the admin for ur application which is in memory.Rest of the users
                                //will be in UserDetailsService
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //httpBasic is used so that we can pass our APIs from Postman now
        http
                .csrf()
                .disable()//we have to disable csrf to do testing in postman
                .formLogin()
                .disable();//disabling form login
    }
}
