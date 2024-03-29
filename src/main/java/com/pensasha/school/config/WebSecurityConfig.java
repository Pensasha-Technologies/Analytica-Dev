package com.pensasha.school.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Autowired
    CustomSuccessHandler customSuccessHandler;

    /*~~(Migrate manually based on https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter)~~>*/@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication().dataSource(dataSource)
                .usersByUsernameQuery(
                        "select username as principal, password as credentials, true from user where username = ?")
                .authoritiesByUsernameQuery(
                        "select username as pricipal, role_id as role from user where username = ? ")
                .passwordEncoder(passwordencoder()).rolePrefix("ROLE_");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/","/error" ,"/index","/changePassword" , "/css/**", "/js/**", "/img/**", "/schBanner/**", "/schImg/**", "/studImg/**", "/vendor/**").permitAll()
                .anyRequest().authenticated().and().formLogin().loginPage("/login").usernameParameter("username")
                .passwordParameter("password").permitAll().successHandler(customSuccessHandler).and().logout()
                .logoutUrl("/logout").logoutSuccessUrl("/login?logout").permitAll().and().exceptionHandling()
                .accessDeniedPage("/403").and().csrf().disable();
    }

    @Bean(name = "passordEncoder")
    public PasswordEncoder passwordencoder() {
        return new BCryptPasswordEncoder();
    }

}