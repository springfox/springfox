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

package springfox.documentation.swagger.ui.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableWebMvc
public class SwaggerUiConfiguration extends WebMvcConfigurerAdapter {
  private static final String[] SERVLET_RESOURCE_LOCATIONS = { "/" };

  private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
      "classpath:/META-INF/resources/", "classpath:/resources/",
      "classpath:/static/", "classpath:/public/" };

  private static final String[] RESOURCE_LOCATIONS;
  static {
    RESOURCE_LOCATIONS = new String[CLASSPATH_RESOURCE_LOCATIONS.length
        + SERVLET_RESOURCE_LOCATIONS.length];
    System.arraycopy(SERVLET_RESOURCE_LOCATIONS, 0, RESOURCE_LOCATIONS, 0,
        SERVLET_RESOURCE_LOCATIONS.length);
    System.arraycopy(CLASSPATH_RESOURCE_LOCATIONS, 0, RESOURCE_LOCATIONS,
        SERVLET_RESOURCE_LOCATIONS.length, CLASSPATH_RESOURCE_LOCATIONS.length);
  }

  private static final String[] STATIC_INDEX_HTML_RESOURCES;
  static {
    STATIC_INDEX_HTML_RESOURCES = new String[RESOURCE_LOCATIONS.length];
    for (int i = 0; i < STATIC_INDEX_HTML_RESOURCES.length; i++) {
      STATIC_INDEX_HTML_RESOURCES[i] = RESOURCE_LOCATIONS[i] + "index.html";
    }
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    if (!registry.hasMappingForPattern("/webjars/**")) {
      registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
    if (!registry.hasMappingForPattern("/**")) {
      registry.addResourceHandler("/**").addResourceLocations(RESOURCE_LOCATIONS);
    }
  }

}
