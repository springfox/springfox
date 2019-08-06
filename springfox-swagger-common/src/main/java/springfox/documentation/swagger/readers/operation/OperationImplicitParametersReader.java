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

package springfox.documentation.swagger.readers.operation;


import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spring.web.DescriptionResolver;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import java.util.ArrayList;
import java.util.List;

import static springfox.documentation.swagger.common.SwaggerPluginSupport.*;

@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
public class OperationImplicitParametersReader implements OperationBuilderPlugin {
  private final DescriptionResolver descriptions;

  @Autowired
  public OperationImplicitParametersReader(DescriptionResolver descriptions) {
    this.descriptions = descriptions;
  }

  @Override
  public void apply(OperationContext context) {
    context.operationBuilder().parameters(readParameters(context));
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return pluginDoesApply(delimiter);
  }

  private List<Parameter> readParameters(OperationContext context) {
    List<ApiImplicitParams> annotations = context.findAllAnnotations(ApiImplicitParams.class);

    List<Parameter> parameters = new ArrayList<>();
    if (!annotations.isEmpty()) {
      for (ApiImplicitParams annotation : annotations) {
          for (ApiImplicitParam param : annotation.value()) {
            parameters.add(OperationImplicitParameterReader.implicitParameter(descriptions, param));
          }
      }
    }

    return parameters;
  }
}
