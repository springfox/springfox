/*
 *
 *  Copyright 2015-2019 the original author or authors.
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

package springfox.documentation.swagger.schema;

import io.swagger.annotations.ApiModel;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.schema.DefaultTypeNameProvider;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import static org.springframework.core.annotation.AnnotationUtils.*;
import static springfox.documentation.builders.BuilderDefaults.*;

@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
public class ApiModelTypeNameProvider extends DefaultTypeNameProvider {
  @Override
  public String nameFor(Class<?> type) {
    ApiModel annotation = findAnnotation(
        type,
        ApiModel.class);
    String defaultTypeName = super.nameFor(type);
    if (annotation != null) {
      String value = nullToEmpty(annotation.value());
      if (value.length() == 0) {
        return defaultTypeName;
      }
      return value;
    }
    return defaultTypeName;
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return SwaggerPluginSupport.pluginDoesApply(delimiter);
  }
}
