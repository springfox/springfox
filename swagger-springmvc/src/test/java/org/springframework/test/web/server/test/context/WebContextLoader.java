package org.springframework.test.web.server.test.context;

public class WebContextLoader extends GenericWebContextLoader {

  public WebContextLoader() {
    super("src/test", false);
  }

}