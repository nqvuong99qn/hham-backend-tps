package com.tpssoft.hham.config;

import com.tpssoft.hham.security.WebSecurityConfig;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

public class AppSecurityConfig  extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        try{
            System.out.println("inside");
            http.csrf().disable()
                    .authorizeRequests().antMatchers("/login").permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .formLogin().disable();
        }
        catch (Exception e){
            System.out.println(e);
        }
    }
}
