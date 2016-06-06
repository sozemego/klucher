package com.soze.configuration;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import com.soze.utils.RestUtils;

/**
 * From <a
 * href=http://stackoverflow.com/questions/23901950/spring-security-ajax-session
 * -timeout-issue> here</a>.
 * 
 * @author alessandro ferrucii
 *
 */
public class AjaxAwareAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

  public AjaxAwareAuthenticationEntryPoint(String loginUrl) {
    super(loginUrl);
  }

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
      throws IOException, ServletException {
    boolean isAjax = RestUtils.isAjaxRequest(request);
    if (isAjax) {
      response.sendError(HttpServletResponse.SC_FORBIDDEN, "Session Expired");
    } else {
      super.commence(request, response, authException);
    }
  }
}
