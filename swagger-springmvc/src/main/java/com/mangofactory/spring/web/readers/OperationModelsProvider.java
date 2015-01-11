package com.mangofactory.spring.web.readers;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.mangofactory.schema.alternates.AlternateTypeProvider;
import com.mangofactory.schema.plugins.DocumentationType;
import com.mangofactory.spring.web.plugins.OperationModelsProviderPlugin;
import com.mangofactory.spring.web.readers.operation.HandlerMethodResolver;
import com.mangofactory.spring.web.readers.operation.ResolvedMethodParameter;
import com.mangofactory.spring.web.scanners.RequestMappingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.method.HandlerMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import static com.mangofactory.spring.web.HandlerMethodReturnTypes.*;

@Component
public class OperationModelsProvider implements OperationModelsProviderPlugin {

  private static final Logger log = LoggerFactory.getLogger(OperationModelsProvider.class);
  private final TypeResolver typeResolver;
  private final AlternateTypeProvider alternateTypeProvider;

  @Autowired
  public OperationModelsProvider(TypeResolver typeResolver, AlternateTypeProvider alternateTypeProvider) {
    this.typeResolver = typeResolver;
    this.alternateTypeProvider = alternateTypeProvider;
  }

  @Override
  public void apply(RequestMappingContext context) {
    collectFromReturnType(context);
    collectParameters(context);
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

  private void collectFromReturnType(RequestMappingContext context) {
    ResolvedType modelType = handlerReturnType(typeResolver, context.getHandlerMethod());
    modelType = alternateTypeProvider.alternateFor(modelType);
    context.operationModelsBuilder().addReturn(modelType);
  }

  private void collectParameters(RequestMappingContext context) {

    HandlerMethod handlerMethod = context.getHandlerMethod();
    Method method = handlerMethod.getMethod();

    log.debug("Reading parameters models for handlerMethod |{}|", handlerMethod.getMethod().getName());

    HandlerMethodResolver handlerMethodResolver = new HandlerMethodResolver(typeResolver);
    List<ResolvedMethodParameter> parameterTypes = handlerMethodResolver.methodParameters(handlerMethod);
    Annotation[][] annotations = method.getParameterAnnotations();

    for (int i = 0; i < annotations.length; i++) {
      Annotation[] pAnnotations = annotations[i];
      for (Annotation annotation : pAnnotations) {
        if (annotation instanceof RequestBody) {
          ResolvedMethodParameter pType = parameterTypes.get(i);
          ResolvedType modelType = alternateTypeProvider.alternateFor(pType.getResolvedParameterType());
          context.operationModelsBuilder().addInputParam(modelType);
        }
      }
    }
    log.debug("Finished reading parameters models for handlerMethod |{}|", handlerMethod.getMethod().getName());
  }
}
