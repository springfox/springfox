/*
 *
 *  Copyright 2015 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package springfox.documentation.swagger.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.UriComponentsBuilder;
import springfox.documentation.spring.web.AbstractPathProvider;

import javax.servlet.ServletContext;

import static springfox.documentation.swagger.common.SwaggerPluginSupport.*;

/**
 * This provider has quite a few hardcoded values that don't make sense if we try to use this
 * generically across swagger 1.2 and swagger 2.0 the paths don't make sense. For e.g. getAppRoot
 * has a hard coded host:port. Also the DOCUMENTATION_BASE_PATH is not the same across swagger 1.2
 * and swagger 2.0. This is deprecated in favor or @see RelativePathProvider.
 */
@Deprecated
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
