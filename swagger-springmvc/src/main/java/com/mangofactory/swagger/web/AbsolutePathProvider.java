package com.mangofactory.swagger.web;

import com.mangofactory.spring.web.PathProvider;
import com.mangofactory.swagger.web.DefaultSwaggerController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletContext;

@Component
public class AbsolutePathProvider extends PathProvider {

  @Autowired
  private ServletContext servletContext;

  @Override
  protected String applicationPath() {
    return getAppRoot()
            .build()
            .toString();
  }

  @Override
  protected String getDocumentationPath() {
    return getAppRoot()
            .path(DefaultSwaggerController.DOCUMENTATION_BASE_PATH)
            .build()
            .toString();
  }

  private UriComponentsBuilder getAppRoot() {
    return UriComponentsBuilder.fromHttpUrl("http://localhost:8080")
            .path(servletContext.getContextPath());
  }
}
