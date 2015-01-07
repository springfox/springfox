package com.mangofactory.swagger.readers.operation.parameter;

import com.fasterxml.classmate.ResolvedType;
import com.mangofactory.documentation.plugins.DocumentationType;
import com.mangofactory.schema.alternates.AlternateTypeProvider;
import com.mangofactory.springmvc.plugins.ParameterBuilderPlugin;
import com.mangofactory.springmvc.plugins.ParameterContext;
import com.mangofactory.swagger.readers.operation.ResolvedMethodParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.lang.annotation.Annotation;

@Component
public class ParameterTypeReader implements ParameterBuilderPlugin {
  private final AlternateTypeProvider alternateTypeProvider;

  @Autowired
  public ParameterTypeReader(AlternateTypeProvider alternateTypeProvider) {
    this.alternateTypeProvider = alternateTypeProvider;
  }

  @Override
  public void apply(ParameterContext context) {
    MethodParameter methodParameter = context.methodParameter();
    ResolvedMethodParameter resolvedMethodParameter = context.resolvedMethodParameter();
    ResolvedType parameterType = resolvedMethodParameter.getResolvedParameterType();
    parameterType = alternateTypeProvider.alternateFor(parameterType);
    context.parameterBuilder().parameterType(findParameterType(methodParameter, parameterType));
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

  private String findParameterType(MethodParameter methodParameter, ResolvedType parameterType) {
    //Multi-part file trumps any other annotations
    if (MultipartFile.class.isAssignableFrom(parameterType.getErasedType())) {
      return "form";
    }
    Annotation[] methodAnnotations = methodParameter.getParameterAnnotations();
    if (null != methodAnnotations) {
      for (Annotation annotation : methodAnnotations) {
        if (annotation instanceof PathVariable) {
          return "path";
        } else if (annotation instanceof ModelAttribute) {
          return "body";
        } else if (annotation instanceof RequestBody) {
          return "body";
        } else if (annotation instanceof RequestParam) {
          return "query";
        } else if (annotation instanceof RequestHeader) {
          return "header";
        }
      }
    }
    return "body";
  }
}
