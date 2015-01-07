package com.mangofactory.springmvc.plugins;

import com.mangofactory.documentation.plugins.DocumentationType;
import com.mangofactory.service.model.builder.ParameterBuilder;
import com.mangofactory.swagger.readers.operation.ResolvedMethodParameter;
import org.springframework.core.MethodParameter;

public class ParameterContext {
  private final ParameterBuilder parameterBuilder;
  private final ResolvedMethodParameter resolvedMethodParameter;
  private final DocumentationContext documentationContext;

  public ParameterContext(ResolvedMethodParameter
                                  resolvedMethodParameter, ParameterBuilder parameterBuilder, DocumentationContext
          documentationContext) {
    this.parameterBuilder = parameterBuilder;
    this.resolvedMethodParameter = resolvedMethodParameter;
    this.documentationContext = documentationContext;
  }

  public ResolvedMethodParameter resolvedMethodParameter() {
    return resolvedMethodParameter;
  }

  public MethodParameter methodParameter() {
    return resolvedMethodParameter.getMethodParameter();
  }

  public ParameterBuilder parameterBuilder() {
    return parameterBuilder;
  }

  public DocumentationType getDocumentationType() {
    return documentationContext.getDocumentationType();
  }
}
