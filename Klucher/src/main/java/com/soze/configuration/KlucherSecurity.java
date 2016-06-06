package com.soze.configuration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class KlucherSecurity extends WebSecurityConfigurerAdapter {

  @Autowired
  private KlucherUserDetailsService userDetailsService;
  
  @Autowired
  private PasswordEncoder passwordEncoder;
  
  @Autowired
  private DataSource dataSource;
  
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
    .csrf().disable()
    .authorizeRequests()
      .antMatchers("/user/**").authenticated()
      .antMatchers("/configprops/**").hasRole("ADMIN")
      .antMatchers("/logfile/**").hasRole("ADMIN")
      .antMatchers("/health").hasRole("ADMIN")
      .antMatchers("/mappings/**").hasRole("ADMIN")
      .antMatchers("/autoconfig").hasRole("ADMIN")
      .antMatchers("/metrics/**").hasRole("ADMIN")
      .antMatchers("/trace/**").hasRole("ADMIN")
      .antMatchers("/env/**").hasRole("ADMIN")
      .antMatchers("/health/**").hasRole("ADMIN")
      .antMatchers("/info/**").hasRole("ADMIN")
    .and()
      .formLogin()
      .defaultSuccessUrl("/user", true)
      .loginPage("/login")
      .loginProcessingUrl("/login").permitAll()
    .and()
      .rememberMe()
      .rememberMeParameter("remember-me")
      .tokenRepository(persistentTokenRepository())
      .tokenValiditySeconds(86400)
     .and()
     .logout().permitAll()
     .and()
       .exceptionHandling()
       .authenticationEntryPoint(new AjaxAwareAuthenticationEntryPoint("/login"))
     .and()                                                               
       .headers().frameOptions().disable();
  }
  
  @Bean
  public PersistentTokenRepository persistentTokenRepository() {
    JdbcTokenRepositoryImpl tokenRepositoryImpl = new JdbcTokenRepositoryImpl();
    tokenRepositoryImpl.setDataSource(dataSource);
    return tokenRepositoryImpl;
  }
  
  @Autowired
  public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService);
  }
  
}
