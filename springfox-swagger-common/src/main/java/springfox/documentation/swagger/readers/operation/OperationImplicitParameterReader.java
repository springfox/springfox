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

import com.google.common.collect.Lists;
import io.swagger.annotations.ApiImplicitParam;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import java.lang.reflect.Method;
import java.util.List;

import static springfox.documentation.schema.Types.*;
import static springfox.documentation.swagger.schema.ApiModelProperties.*;


@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
public class OperationImplicitParameterReader implements OperationBuilderPlugin {
  private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(OperationImplicitParameterReader.class);

  @Override
  public void apply(OperationContext context) {
    context.operationBuilder().parameters(readParameters(context));
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return SwaggerPluginSupport.pluginDoesApply(delimiter);
  }

  protected List<Parameter> readParameters(OperationContext context) {
    HandlerMethod handlerMethod = context.getHandlerMethod();
    Method method = handlerMethod.getMethod();
    ApiImplicitParam annotation = AnnotationUtils.findAnnotation(method, ApiImplicitParam.class);
    List<Parameter> parameters = Lists.newArrayList();
    if (null != annotation) {
      parameters.add(OperationImplicitParameterReader.implicitParameter(annotation));
    }
    return parameters;
  }

  public static Parameter implicitParameter(ApiImplicitParam param) {
    ModelRef modelRef = maybeGetModelRef(param);
    return new ParameterBuilder()
        .name(param.name())
        .description(param.value())
        .defaultValue(param.defaultValue())
        .required(param.required())
        .allowMultiple(param.allowMultiple())
        .modelRef(modelRef)
        .allowableValues(allowableValueFromString(param.allowableValues()))
        .parameterType(param.paramType())
        .parameterAccess(param.access())
        .build();
  }

  static ModelRef maybeGetModelRef(ApiImplicitParam param) {
    String baseType = param.dataType();
    if (!isBaseType(param.dataType())) {
      LOGGER.warn("Coercing to be of type string. This may not even be a scalar type in actuality");
      baseType = "string";
    }
    return new ModelRef(baseType, allowableValueFromString(param.allowableValues()));
  }

}

