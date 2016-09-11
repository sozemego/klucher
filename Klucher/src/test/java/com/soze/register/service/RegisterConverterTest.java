package com.soze.register.service;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.soze.register.model.RegisterForm;
import com.soze.user.model.User;
import com.soze.utils.FileUtils;

public class RegisterConverterTest {

  private RegisterConverter converter;
  
  private PasswordEncoder encoder;
  
  @Before
  public void setUp() throws Exception {
    encoder = mock(PasswordEncoder.class);
    FileUtils utils = mock(FileUtils.class);
    when(utils.readLinesFromClasspathFile("config/avatars.txt")).thenReturn(Arrays.asList("avatar_path"));
    converter = new RegisterConverter(encoder, utils);
    converter.init();
  }
  
  @Test
  public void validForm() {
    RegisterForm form = new RegisterForm();
    String username = "user";
    form.setUsername(username);
    String password = "password";
    form.setPassword(password);
    String hashedPassword = "hashedPassword";
    when(encoder.encode(password)).thenReturn(hashedPassword);
    User user = converter.convertRegisterForm(form);
    assertThat(user, notNullValue());
    assertThat(user.getUsername(), equalTo(username));
    assertThat(user.getHashedPassword(), equalTo(hashedPassword));
    assertThat(user.getAuthorities().size(), equalTo(1));
    assertThat(user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")), equalTo(true));
    assertThat(user.getAvatarPath(), equalTo("avatar_path"));
  }
  
  @Test
  public void anotherValidForm() {
    RegisterForm form = new RegisterForm();
    String username = "anotherUser";
    form.setUsername(username);
    String password = "anotherPassword";
    form.setPassword(password);
    String hashedPassword = "anotherHashedPassword";
    when(encoder.encode(password)).thenReturn(hashedPassword);
    User user = converter.convertRegisterForm(form);
    assertThat(user, notNullValue());
    assertThat(user.getUsername(), equalTo(username));
    assertThat(user.getHashedPassword(), equalTo(hashedPassword));
    assertThat(user.getAuthorities().size(), equalTo(1));
    assertThat(user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")), equalTo(true));
    assertThat(user.getAvatarPath(), equalTo("avatar_path"));
  }

}
