package com.mangofactory.swagger.readers.operation;

import com.google.common.collect.Lists;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.model.Parameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

public class OperationImplicitParametersReader extends SwaggerParameterReader {

  @Override
  protected Collection<Parameter> readParameters(RequestMappingContext context) {
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
