package springdox.documentation.swagger.readers.operation;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Optional;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import springdox.documentation.spi.DocumentationType;
import springdox.documentation.spi.service.OperationModelsProviderPlugin;
import springdox.documentation.spi.service.contexts.RequestMappingContext;
import springdox.documentation.swagger.annotations.Annotations;

import static springdox.documentation.swagger.common.SwaggerPluginSupport.*;

@Component
public class SwaggerOperationModelsProvider implements OperationModelsProviderPlugin {

  private static final Logger log = LoggerFactory.getLogger(SwaggerOperationModelsProvider.class);
  private final TypeResolver typeResolver;

  @Autowired
  public SwaggerOperationModelsProvider(TypeResolver typeResolver) {
    this.typeResolver = typeResolver;
  }

  @Override
  public void apply(RequestMappingContext context) {
    collectFromApiOperation(context);
    collectApiResponses(context);
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return pluginDoesApply(delimiter);
  }

  private void collectFromApiOperation(RequestMappingContext context) {
    HandlerMethod handlerMethod = context.getHandlerMethod();
    ApiOperation apiOperationAnnotation = handlerMethod.getMethodAnnotation(ApiOperation.class);
    ResolvedType modelType;
    if (null != apiOperationAnnotation && Void.class != apiOperationAnnotation.response()) {
      modelType = typeResolver.resolve(apiOperationAnnotation.response());
      context.operationModelsBuilder().addReturn(modelType);
    }
  }

  private void collectApiResponses(RequestMappingContext context) {

    HandlerMethod handlerMethod = context.getHandlerMethod();
    Optional<ApiResponses> apiResponses = Annotations.findApiResponsesAnnotations(handlerMethod.getMethod());

    log.debug("Reading parameters models for handlerMethod |{}|", handlerMethod.getMethod().getName());
    if (!apiResponses.isPresent()) {
      return;
    }

    for (ApiResponse response : apiResponses.get().value()) {
      ResolvedType modelType = context.alternateFor(typeResolver.resolve(response.response()));
      context.operationModelsBuilder().addReturn(modelType);
    }
    log.debug("Finished reading parameters models for handlerMethod |{}|", handlerMethod.getMethod().getName());
  }
}
