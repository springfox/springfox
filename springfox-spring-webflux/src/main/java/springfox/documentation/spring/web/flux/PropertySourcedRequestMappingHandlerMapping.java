/*
 *
 *  Copyright 2017-2018 the original author or authors.
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
package springfox.documentation.spring.web.flux;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.result.method.RequestMappingInfo;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;
import springfox.documentation.spring.web.PropertySourcedMapping;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class PropertySourcedRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

  private final Map<String, HandlerMethod> handlerMethods = new LinkedHashMap<String, HandlerMethod>();
  private final Environment environment;
  private final Object handler;

  public PropertySourcedRequestMappingHandlerMapping(
      Environment environment,
      Object handler) {
    this.environment = environment;
    this.handler = handler;
  }
  
  @Override
  protected void initHandlerMethods() {
    logger.debug("initialising the handler methods");
    setOrder(Ordered.HIGHEST_PRECEDENCE + 1000);
    Class<?> clazz = handler.getClass();
    if (isHandler(clazz)) {
      for (Method method : clazz.getMethods()) {
        PropertySourcedMapping mapper = AnnotationUtils.getAnnotation(method, PropertySourcedMapping.class);
        if (mapper != null) {
          RequestMappingInfo mapping = getMappingForMethod(method, clazz);
          HandlerMethod handlerMethod = createHandlerMethod(handler, method);
          String mappingPath = mappingPath(mapper);
          if (mappingPath != null) {
            logger.info(String.format("Mapped URL path [%s] onto method [%s]", mappingPath, handlerMethod.toString()));
            handlerMethods.put(mappingPath, handlerMethod);
          } else {
            for (PathPattern path : mapping.getPatternsCondition().getPatterns()) {
              logger.info(String.format("Mapped URL path [%s] onto method [%s]", path.getPatternString(), handlerMethod.toString()));
              handlerMethods.put(path.getPatternString(), handlerMethod);
            }
          }
        }
      }
    }
  }

  private String mappingPath(final PropertySourcedMapping mapper) {
    final String key = mapper.propertyKey();
    final String target = mapper.value();
    return Optional.ofNullable(environment.getProperty(key))
        .map(input -> target.replace(String.format("${%s}", key), input))
        .orElse(null);
  }

  @Override
  protected boolean isHandler(Class<?> beanType) {
    return ((AnnotationUtils.findAnnotation(beanType, Controller.class) != null) ||
                (AnnotationUtils.findAnnotation(beanType, RequestMapping.class) != null));
  }

}
