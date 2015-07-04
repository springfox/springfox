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

package springfox.documentation.swagger.readers.operation;

import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import io.swagger.annotations.Extension;
import io.swagger.annotations.ExtensionProperty;
import java.util.HashMap;
import java.util.Map;

@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
public class ExtensionsReader implements OperationBuilderPlugin {

  private static final Logger LOG = LoggerFactory.getLogger(ExtensionsReader.class);
  @Override
  public void apply(OperationContext context) {

    HandlerMethod handlerMethod = context.getHandlerMethod();
    ApiOperation apiOperationAnnotation = handlerMethod.getMethodAnnotation(ApiOperation.class);

    Map<String, Object> extensions = new HashMap<String, Object>();
    if (null != apiOperationAnnotation && null != apiOperationAnnotation.extensions()) {
      Extension[] extensionsAnnotations = apiOperationAnnotation.extensions();
      if (extensionsAnnotations != null && extensionsAnnotations.length > 0) {
        addExtensionProperties(extensionsAnnotations, extensions);
      }
    }
    LOG.debug("Extension count {} for method {}", extensions.size(), handlerMethod.getMethod().getName());
    context.operationBuilder().extensions(extensions);
  }
  
  private void addExtensionProperties(Extension[] extensions, Map<String, Object> map) {
    for (Extension extension : extensions) {
      String name = extension.name();
      if (name.length() > 0) {
        if (!name.startsWith("x-")) {
          name = "x-" + name;
        }
        if (!map.containsKey(name)) {
          map.put(name, new HashMap<String, Object>());
        }
        map = (Map<String, Object>) map.get(name);
      }
      for (ExtensionProperty property : extension.properties()) {
        if (!property.name().isEmpty() && !property.value().isEmpty()) {
          String propertyName = property.name();
          if (name.isEmpty() && !propertyName.startsWith("x-")) {
            propertyName = "x-" + propertyName;
          }
          map.put(propertyName, property.value());
        }
      }
    }
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return SwaggerPluginSupport.pluginDoesApply(delimiter);
  }
}
