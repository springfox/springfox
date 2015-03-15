package springdox.documentation.spring.web.readers.operation;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.method.HandlerMethod;
import springdox.documentation.service.ResolvedMethodParameter;
import springdox.documentation.spi.DocumentationType;
import springdox.documentation.spi.service.OperationModelsProviderPlugin;
import springdox.documentation.spi.service.contexts.RequestMappingContext;
import springdox.documentation.spring.web.HandlerMethodReturnTypes;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

@Component
public class OperationModelsProvider implements OperationModelsProviderPlugin {

  private static final Logger log = LoggerFactory.getLogger(OperationModelsProvider.class);
  private final TypeResolver typeResolver;

  @Autowired
  public OperationModelsProvider(TypeResolver typeResolver) {
    this.typeResolver = typeResolver;
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
    ResolvedType modelType = HandlerMethodReturnTypes.handlerReturnType(typeResolver, context.getHandlerMethod());
    modelType = context.alternateFor(modelType);
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
          ResolvedType modelType = context.alternateFor(pType.getResolvedParameterType());
          context.operationModelsBuilder().addInputParam(modelType);
        }
      }
    }
    log.debug("Finished reading parameters models for handlerMethod |{}|", handlerMethod.getMethod().getName());
  }
}
