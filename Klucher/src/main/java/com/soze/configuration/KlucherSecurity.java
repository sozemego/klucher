package com.soze.configuration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import com.soze.user.dao.UserDao;

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
  
  @Autowired
  private UserDao userDao;
  
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
    .csrf().disable()
    .authorizeRequests()
      .antMatchers("/dashboard/**").authenticated()
      .antMatchers("/settings/**").authenticated()
      .antMatchers("/chat/**").authenticated()
      .antMatchers("/configprops/**").hasAuthority("ADMIN")
      .antMatchers("/logfile/**").hasAuthority("ADMIN")
      .antMatchers("/health").hasAuthority("ADMIN")
      .antMatchers("/mappings/**").hasAuthority("ADMIN")
      .antMatchers("/autoconfig").hasAuthority("ADMIN")
      .antMatchers("/metrics/**").hasAuthority("ADMIN")
      .antMatchers("/trace/**").hasAuthority("ADMIN")
      .antMatchers("/env/**").hasAuthority("ADMIN")
      .antMatchers("/health/**").hasAuthority("ADMIN")
      .antMatchers("/info/**").hasAuthority("ADMIN")
      .antMatchers("/chats/close/**").hasAuthority("ADMIN")
      .antMatchers("/chats/trigger").hasAuthority("ADMIN")
    .and()
      .formLogin()
      .defaultSuccessUrl("/dashboard", false)
      .loginPage("/login")
      .loginProcessingUrl("/login")
      .successHandler(getAuthenticationSuccessHandler())
      .permitAll()
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
  
  @Bean
  public AuthenticationProvider getAuthenticationProvider() {
    return new KlucherAuthenticationProvider(userDao, passwordEncoder);
  }
  
  @Autowired
  public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService);
    auth.authenticationProvider(getAuthenticationProvider());
  }
  
  @Bean
  public AuthenticationSuccessHandler getAuthenticationSuccessHandler() {
    SimpleUrlAuthenticationSuccessHandler handler = new SimpleUrlAuthenticationSuccessHandler();
    handler.setUseReferer(true);
    return handler;
  }
  
}
