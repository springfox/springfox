package com.mangofactory.swagger.readers.operation.parameter;

import com.fasterxml.classmate.ResolvedType;
import com.mangofactory.swagger.configuration.SwaggerGlobalSettings;
import com.mangofactory.swagger.readers.Command;
import com.mangofactory.swagger.readers.operation.ResolvedMethodParameter;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.lang.annotation.Annotation;

public class ParameterTypeReader implements Command<RequestMappingContext> {
  private Logger log = LoggerFactory.getLogger(ParameterTypeReader.class);

  @Override
  public void execute(RequestMappingContext context) {
    MethodParameter methodParameter = (MethodParameter) context.get("methodParameter");
    ResolvedMethodParameter resolvedMethodParameter
            = (ResolvedMethodParameter) context.get("resolvedMethodParameter");
    SwaggerGlobalSettings swaggerGlobalSettings = (SwaggerGlobalSettings) context.get("swaggerGlobalSettings");
    ResolvedType parameterType = resolvedMethodParameter.getResolvedParameterType();
    parameterType = swaggerGlobalSettings.getAlternateTypeProvider().alternateFor(parameterType);
    context.put("paramType", findParameterType(methodParameter, parameterType));
  }

  private String findParameterType(MethodParameter methodParameter, ResolvedType parameterType) {
    //Multi-part file trumps any other annotations
    if (MultipartFile.class.isAssignableFrom(parameterType.getErasedType())) {
      return "formData";
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
        } else if (annotation instanceof CookieValue) {
          log.info("Found cookie parameter - not yet supported!");
          return "cookie";
        }
      }
    }
    return "body";
  }
}
