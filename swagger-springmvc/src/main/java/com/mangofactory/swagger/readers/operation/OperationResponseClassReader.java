package com.mangofactory.swagger.readers.operation;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.mangofactory.documentation.plugins.DocumentationType;
import com.mangofactory.schema.alternates.AlternateTypeProvider;
import com.mangofactory.springmvc.plugins.OperationBuilderPlugin;
import com.mangofactory.springmvc.plugins.OperationContext;
import com.mangofactory.swagger.core.ModelUtils;
import com.wordnik.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import static com.mangofactory.swagger.core.ModelUtils.*;

@Component
public class OperationResponseClassReader implements OperationBuilderPlugin {
  private static Logger log = LoggerFactory.getLogger(OperationResponseClassReader.class);
  private final TypeResolver typeResolver;
  private final AlternateTypeProvider alternateTypeProvider;

  @Autowired
  public OperationResponseClassReader(TypeResolver typeResolver, AlternateTypeProvider alternateTypeProvider) {
    this.typeResolver = typeResolver;
    this.alternateTypeProvider = alternateTypeProvider;
  }

  @Override
  public void apply(OperationContext context) {

    HandlerMethod handlerMethod = context.getHandlerMethod();
    ApiOperation methodAnnotation = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), ApiOperation.class);
    ResolvedType returnType;
    if (null != methodAnnotation && Void.class != methodAnnotation.response()) {
      log.debug("Overriding response class with annotated response class");
      returnType = typeResolver.resolve(methodAnnotation.response());
    } else {
      returnType = handlerReturnType(typeResolver, handlerMethod);
      returnType = alternateTypeProvider.alternateFor(returnType);
    }
    if (Void.class.equals(returnType.getErasedType()) || Void.TYPE.equals(returnType.getErasedType())) {
      context.operationBuilder().responseClass("void");
      return;
    }
    String responseTypeName = ModelUtils.getResponseClassName(returnType);
    log.debug("Setting response class to:" + responseTypeName);
    context.operationBuilder().responseClass(responseTypeName);
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }
}
