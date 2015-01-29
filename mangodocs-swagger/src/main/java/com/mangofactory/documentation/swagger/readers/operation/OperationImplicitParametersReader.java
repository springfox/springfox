package com.mangofactory.documentation.swagger.readers.operation;

import com.google.common.collect.Lists;
import com.mangofactory.documentation.spi.DocumentationType;
import com.mangofactory.documentation.service.model.Parameter;
import com.mangofactory.documentation.spi.service.OperationBuilderPlugin;
import com.mangofactory.documentation.spi.service.contexts.OperationContext;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;
import java.util.List;

import static com.mangofactory.documentation.swagger.common.SwaggerPluginSupport.*;

@Component
public class OperationImplicitParametersReader implements OperationBuilderPlugin {

  @Override
  public void apply(OperationContext context) {
    context.operationBuilder().parameters(readParameters(context));
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return pluginDoesApply(delimiter);
  }

  protected List<Parameter> readParameters(OperationContext context) {
    HandlerMethod handlerMethod = context.getHandlerMethod();
    Method method = handlerMethod.getMethod();
    ApiImplicitParams annotation = AnnotationUtils.findAnnotation(method, ApiImplicitParams.class);

    List<Parameter> parameters = Lists.newArrayList();
    if (null != annotation) {
      for (ApiImplicitParam param : annotation.value()) {
        parameters.add(OperationImplicitParameterReader.getImplicitParameter(param));
      }
    }

    return parameters;
  }
}
