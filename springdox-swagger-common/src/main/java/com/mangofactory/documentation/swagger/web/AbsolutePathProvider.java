package com.mangofactory.documentation.swagger.web;

import com.mangofactory.documentation.spring.web.AbstractPathProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletContext;

import static com.mangofactory.documentation.swagger.common.SwaggerPluginSupport.*;

@Component
public class AbsolutePathProvider extends AbstractPathProvider {

  private final ServletContext servletContext;

  @Autowired
  public AbsolutePathProvider(ServletContext servletContext) {
    this.servletContext = servletContext;
  }

  @Override
  protected String applicationPath() {
    return getAppRoot()
            .build()
            .toString();
  }

  @Override
  protected String getDocumentationPath() {
    return getAppRoot()
            .path(DOCUMENTATION_BASE_PATH)
            .build()
            .toString();
  }

  private UriComponentsBuilder getAppRoot() {
    return UriComponentsBuilder.fromHttpUrl("http://localhost:8080")
            .path(servletContext.getContextPath());
  }
}
