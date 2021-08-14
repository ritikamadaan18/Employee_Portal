package com.gfg.jbdl12employee.portal.conf;


import com.gfg.jbdl12employee.portal.manager.EmployeeManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import javax.sql.DataSource;

@Configuration
@EnableAuthorizationServer
public class AuthServerConf extends AuthorizationServerConfigurerAdapter {
    //AuthorizationServerConfigurerAdapter is for OAuth configuration like WebSecurityConfigurerAdapter for Spring Security

    @Autowired
     DataSource dataSource;//what we have to provide to jdbc in ClientDetailsServiceConfigurer
                            //dataSource->this we have provided in our application properties

    @Bean
    public TokenStore getTokenStore(){
        return new JdbcTokenStore(dataSource);//we r creating a tokenStore with mentioned dataSource only
    }

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    EmployeeManager employeeManager;

    @Autowired
     PasswordEncoder passwordEncoder;//we have created this bean in SecurityConfiguration

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {

        //who can come to Authorization Server-we will specify any authenticated usr can come to Authorization Server
        security.checkTokenAccess("isAuthenticated()").tokenKeyAccess("permitAll()");//this means any authenticated user can
                                //get Access Token
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        //to tell who are my authenticated clients//where the details are present
        //  HERE WE ARE STORING CLIENT DETAILS
        clients.jdbc(dataSource).passwordEncoder(passwordEncoder);//jdbc means we are providing some dataStore(MySql store) of our client details
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        //AuthorizationServerEndpointsConfigurer->This configures token store( to store access,refresh tokens)
        //this requires jdbc Token store
        //to validate all users we have to use authenticationManager and userDetailsService
        //HERE WE ARE STORING ACCESS AND REFRESH TOKENS

        endpoints.tokenStore(getTokenStore())
                .authenticationManager(authenticationManager)   //to authenticate user
                .userDetailsService(employeeManager);
    }
}
//WE HAVE TO CREATE TABLES IN DATABASE FOR ALL THIS WE NEED TO STORE (TOKENS ETC.)
//FOR THIS WE USE SHORTCUT ,WHEN SPRING STARTS IT LOOKS FOR SQL FILE IN resources,AND ACCORDINGLY IT MAKES THE TABLE FOR YOU
