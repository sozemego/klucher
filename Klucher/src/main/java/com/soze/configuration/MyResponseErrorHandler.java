package com.soze.configuration;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import com.soze.utils.RestUtils;

public class MyResponseErrorHandler implements ResponseErrorHandler {

  private static final Logger log = LoggerFactory.getLogger(MyResponseErrorHandler.class);

  @Override
  public void handleError(ClientHttpResponse response) throws IOException {
    log.error("Response error: {} {}", response.getStatusCode(), response.getStatusText());
  }

  @Override
  public boolean hasError(ClientHttpResponse response) throws IOException {
    return RestUtils.isError(response.getStatusCode());
  }

}
