package com.mangofactory.documentation.swagger.readers.parameter;

import com.google.common.base.Splitter;
import com.mangofactory.documentation.spi.DocumentationType;
import com.mangofactory.documentation.schema.Enums;
import com.mangofactory.documentation.service.model.AllowableListValues;
import com.mangofactory.documentation.service.model.AllowableRangeValues;
import com.mangofactory.documentation.service.model.AllowableValues;
import com.mangofactory.documentation.spi.service.ParameterBuilderPlugin;
import com.mangofactory.documentation.spi.service.contexts.ParameterContext;
import com.wordnik.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.Lists.*;
import static org.springframework.util.StringUtils.*;

@Component("swaggerParameterAllowableReader")
public class ParameterAllowableReader implements ParameterBuilderPlugin {
  private static final Logger log = LoggerFactory.getLogger(ParameterAllowableReader.class);

  @Override
  public void apply(ParameterContext context) {
    MethodParameter methodParameter = context.methodParameter();
    AllowableValues allowableValues = null;
    String allowableValueString = findAnnotatedAllowableValues(methodParameter);
    if (allowableValueString != null && !"".equals(allowableValueString)) {
      allowableValues = ParameterAllowableReader.allowableValueFromString(allowableValueString);
    } else {
      if (methodParameter.getParameterType().isEnum()) {
        allowableValues = Enums.allowableValues(methodParameter.getParameterType());
      }
      if (methodParameter.getParameterType().isArray()) {
        allowableValues = Enums.allowableValues(methodParameter.getParameterType().getComponentType());
      }
    }
    context.parameterBuilder().allowableValues(allowableValues);
  }

  public static AllowableValues allowableValueFromString(String allowableValueString) {
    AllowableValues allowableValues = null;
    allowableValueString = allowableValueString.trim().replaceAll(" ", "");
    if (allowableValueString.startsWith("range[")) {
      allowableValueString = allowableValueString.replaceAll("range\\[", "").replaceAll("]", "");
      Iterable<String> split = Splitter.on(',').trimResults().omitEmptyStrings().split(allowableValueString);
      List<String> ranges = newArrayList(split);
      allowableValues = new AllowableRangeValues(ranges.get(0), ranges.get(1));
    } else if (allowableValueString.contains(",")) {
      Iterable<String> split = Splitter.on(',').trimResults().omitEmptyStrings().split(allowableValueString);
      allowableValues = new AllowableListValues(newArrayList(split), "LIST");
    } else if (hasText(allowableValueString)) {
      List<String> singleVal = Arrays.asList(allowableValueString.trim());
      allowableValues = new AllowableListValues(singleVal, "LIST");
    }
    return allowableValues;
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

  private String findAnnotatedAllowableValues(MethodParameter methodParameter) {
    Annotation[] methodAnnotations = methodParameter.getParameterAnnotations();
    if (null != methodAnnotations) {
      for (Annotation annotation : methodAnnotations) {
        if (annotation instanceof ApiParam) {
          return ((ApiParam) annotation).allowableValues();
        }
      }
    }
    return null;
  }
}
