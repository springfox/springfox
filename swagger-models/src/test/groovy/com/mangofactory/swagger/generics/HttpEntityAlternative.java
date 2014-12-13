package com.mangofactory.swagger.generics;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

public class HttpEntityAlternative<T> {
  public static final HttpEntity EMPTY = null;
  private final HttpHeaders headers = null;
  private final T body = null;

  public static HttpEntity getEmpty() {
    return EMPTY;
  }

  public HttpHeaders getHeaders() {
    return headers;
  }

  public T getBody() {
    return body;
  }
}
