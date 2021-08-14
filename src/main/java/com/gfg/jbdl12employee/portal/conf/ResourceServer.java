package com.gfg.jbdl12employee.portal.conf;


import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

@Configuration
@EnableResourceServer
public class ResourceServer extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        //to tell what is your resource which client wants to access
        resources.resourceId("employee_portal");

    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        //to tell who can access your resource Server
        http
                .authorizeRequests()
                .antMatchers("/**")
                .access("#oauth2.hasAnyScope('read','write')")
        //this means whichever user has a scope of read and write can access all APIs
                .and()
                .csrf()
                .disable()
                .formLogin()
                .disable();
    }
}
//APIs ARE PART OF RESOURCE SERVER
