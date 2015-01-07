package com.mangofactory.swagger.readers.operation;

import com.google.common.collect.Lists;
import com.mangofactory.service.model.Parameter;
import com.mangofactory.service.model.builder.ParameterBuilder;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import static com.mangofactory.swagger.plugins.operation.parameter.ParameterAllowableReader.*;

public class OperationImplicitParameterReader extends SwaggerParameterReader {

  @Override
  protected Collection<? extends Parameter> readParameters(RequestMappingContext context) {
    HandlerMethod handlerMethod = context.getHandlerMethod();
    Method method = handlerMethod.getMethod();
    ApiImplicitParam annotation = AnnotationUtils.findAnnotation(method, ApiImplicitParam.class);
    List<Parameter> parameters = Lists.newArrayList();
    if (null != annotation) {
      parameters.add(OperationImplicitParameterReader.getImplicitParameter(annotation));
    }
    return parameters;
  }

  public static Parameter getImplicitParameter(ApiImplicitParam param) {
    return new ParameterBuilder().name(param.name()).description(param.value()).defaultValue(param.defaultValue())
            .required(param.required()).allowMultiple(param.allowMultiple()).dataType(param.dataType())
            .allowableValues(allowableValueFromString(param.allowableValues()))
            .parameterType(param.paramType()).parameterAccess(param.access()).build();
  }

}

