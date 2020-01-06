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

package springfox.documentation.spring.web.scanners;


import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.service.ResourceGroup;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Optional.*;

class ResourcePathProvider {
  private final ResourceGroup resourceGroup;

  ResourcePathProvider(ResourceGroup resourceGroup) {
    this.resourceGroup = resourceGroup;
  }

  public Optional<String> resourcePath() {
    return ofNullable(
        controllerClass()
            .map(resourcePathExtractor())
            .filter(((Predicate<String>) String::isEmpty).negate())
            .orElse(null));
  }

  private Function<Class<?>, String> resourcePathExtractor() {
    return input -> {
      Optional<String> path = Arrays.stream(paths(input))
          .findFirst().filter(((Predicate<String>) String::isEmpty)
              .negate());
      if (!path.isPresent()) {
        return "";
      }
      if (path.get().startsWith("/")) {
        return path.get();
      }
      return "/" + path.get();
    };
  }

  String[] paths(Class<?> controller) {
    RequestMapping annotation
        = AnnotationUtils.findAnnotation(controller, RequestMapping.class);
    if (annotation != null) {
      return annotation.path();
    }
    return new String[] {};
  }

  private Optional<? extends Class<?>> controllerClass() {
    return resourceGroup.getControllerClass();
  }
}
