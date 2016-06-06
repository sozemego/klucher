package com.soze.utils;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;

/**
 * Various static methods related to Http protocol and RESTful architecture.
 * 
 * @author sozek
 *
 */
public class RestUtils {

  /**
   * Checks whether a given {@link HttpStatus} is an error.
   * 
   * @param status
   * @return
   */
  public static boolean isError(HttpStatus status) {
    HttpStatus.Series series = status.series();
    return (HttpStatus.Series.CLIENT_ERROR.equals(series)
        || HttpStatus.Series.SERVER_ERROR.equals(series));
  }

  public static boolean isAjaxRequest(HttpServletRequest request) {
    String ajaxHeader = request.getHeader("X-Requested-With");
    return "XMLHttpRequest".equals(ajaxHeader);
  }

}
