package springfox.documentation.swagger.readers.operation;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.wordnik.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.schema.TypeNameExtractor;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.contexts.ModelContext;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spring.web.HandlerMethodReturnTypes;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import static springfox.documentation.schema.Collections.*;
import static springfox.documentation.schema.Maps.*;

@Component("swaggerOperationClassReader")
public class OperationResponseClassReader implements OperationBuilderPlugin {
  private static Logger log = LoggerFactory.getLogger(OperationResponseClassReader.class);
  private final TypeResolver typeResolver;
  private final TypeNameExtractor nameExtractor;

  @Autowired
  public OperationResponseClassReader(TypeResolver typeResolver,
                                      TypeNameExtractor nameExtractor) {
    this.typeResolver = typeResolver;
    this.nameExtractor = nameExtractor;
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
      returnType = HandlerMethodReturnTypes.handlerReturnType(typeResolver, handlerMethod);
      returnType = context.alternateFor(returnType);
    }
    if (Void.class.equals(returnType.getErasedType()) || Void.TYPE.equals(returnType.getErasedType())) {
      context.operationBuilder()
              .responseClass("void")
              .responseModel(new ModelRef("void"));
      return;
    }
    ModelContext modelContext = ModelContext.returnValue(returnType, context.getDocumentationType(),
            context.getAlternateTypeProvider(), context.getDocumentationContext().getGenericsNamingStrategy());
    String responseTypeName = nameExtractor.typeName(modelContext);
    log.debug("Setting response class to:" + responseTypeName);
    context.operationBuilder()
            .responseClass(responseTypeName)
            .responseModel(modelRef(returnType, modelContext));
  }

  private ModelRef modelRef(ResolvedType type, ModelContext modelContext) {
    if (isContainerType(type)) {
      ResolvedType collectionElementType = collectionElementType(type);
      String elementTypeName = nameExtractor.typeName(ModelContext.fromParent(modelContext, collectionElementType));
      return new ModelRef(containerType(type), elementTypeName);
    }
    if (isMapType(type)) {
      String elementTypeName = nameExtractor.typeName(ModelContext.fromParent(modelContext, mapValueType(type)));
      return new ModelRef("Map", elementTypeName, true);
    }
    String typeName = nameExtractor.typeName(ModelContext.fromParent(modelContext, type));
    return new ModelRef(typeName);
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return SwaggerPluginSupport.pluginDoesApply(delimiter);
  }
}
