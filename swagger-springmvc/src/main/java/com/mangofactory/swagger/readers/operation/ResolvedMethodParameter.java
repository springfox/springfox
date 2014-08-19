package com.mangofactory.swagger.readers.operation;

import com.fasterxml.classmate.ResolvedType;
import org.springframework.core.MethodParameter;

public class ResolvedMethodParameter {
  private final MethodParameter methodParameter;
  private final ResolvedType resolvedParameterType;

  public ResolvedMethodParameter(MethodParameter methodParameter, ResolvedType resolvedParameterType) {

    this.methodParameter = methodParameter;
    this.resolvedParameterType = resolvedParameterType;
  }

  public MethodParameter getMethodParameter() {
    return methodParameter;
  }

  public ResolvedType getResolvedParameterType() {
    return resolvedParameterType;
  }
}
