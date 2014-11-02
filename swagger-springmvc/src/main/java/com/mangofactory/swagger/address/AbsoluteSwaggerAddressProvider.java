package com.mangofactory.swagger.address;

import com.google.common.base.Joiner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;

import static com.google.common.base.Strings.isNullOrEmpty;

@Component
public class AbsoluteSwaggerAddressProvider extends SwaggerAddressProvider {

  @Autowired
  protected AbsoluteSwaggerAddressProvider(ServletContext servletContext) {
    super(servletContext);
  }

  /**
   * hostname and optional port.
   * NOTE: Never the scheme or a forward slash
   * @return the swagger host
   */
  @Override
  protected String host() {
    return "localhost:8080";
  }

  /**
   * Overrides the base path
   * Uses a combination of the servlet context and base path to derive the swagger base path
   * @return the swagger basePath
   */
  @Override
  public String getBasePath() {
    String contextPath = servletContext.getContextPath();
    if (!isNullOrEmpty(contextPath) && !contextPath.trim().equals("/")) {
      return Joiner.on("").skipNulls().join(maybePrefix(contextPath), super.getBasePath());
    }
    return super.getBasePath();
  }

  private String maybePrefix(String contextPath) {
    if (!contextPath.startsWith("/")) {
      return Joiner.on("").join("/", contextPath);
    }
    return contextPath;
  }
}
