package springdox.documentation.spring.web.readers.operation;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.HandlerMethod;
import springdox.documentation.builders.ResponseMessageBuilder;
import springdox.documentation.schema.Collections;
import springdox.documentation.schema.Maps;
import springdox.documentation.schema.ModelRef;
import springdox.documentation.schema.TypeNameExtractor;
import springdox.documentation.service.ResponseMessage;
import springdox.documentation.spi.DocumentationType;
import springdox.documentation.spi.schema.contexts.ModelContext;
import springdox.documentation.spi.service.OperationBuilderPlugin;
import springdox.documentation.spi.service.contexts.OperationContext;
import springdox.documentation.spring.web.HandlerMethodReturnTypes;

import java.util.List;

import static com.google.common.base.Optional.*;
import static com.google.common.collect.Sets.*;
import static org.springframework.core.annotation.AnnotationUtils.*;
import static springdox.documentation.schema.Types.*;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ResponseMessagesReader implements OperationBuilderPlugin {

  private final TypeResolver typeResolver;
  private final TypeNameExtractor typeNameExtractor;

  @Autowired
  public ResponseMessagesReader(TypeResolver typeResolver,
                                TypeNameExtractor typeNameExtractor) {
    this.typeResolver = typeResolver;
    this.typeNameExtractor = typeNameExtractor;
  }

  @Override
  public void apply(OperationContext context) {
    List<ResponseMessage> responseMessages = context.getGlobalResponseMessages(context.httpMethod());
    context.operationBuilder().responseMessages(newHashSet(responseMessages));
    applyReturnTypeOverride(context);
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

  private void applyReturnTypeOverride(OperationContext context) {

    ResolvedType returnType = HandlerMethodReturnTypes.handlerReturnType(typeResolver, context.getHandlerMethod());
    returnType = context.alternateFor(returnType);
    int httpStatusCode = httpStatusCode(context.getHandlerMethod());
    String message = message(context.getHandlerMethod());
    ModelRef modelRef = null;
    if (!isVoid(returnType)) {
      ModelContext modelContext = ModelContext.returnValue(returnType,
              context.getDocumentationType(), context.getAlternateTypeProvider());
      modelRef = modelRef(returnType, modelContext);
    }
    ResponseMessage built = new ResponseMessageBuilder()
            .code(httpStatusCode)
            .message(message)
            .responseModel(modelRef)
            .build();
    context.operationBuilder().responseMessages(newHashSet(built));
  }


  private ModelRef modelRef(ResolvedType type, ModelContext modelContext) {
    if (Collections.isContainerType(type)) {
      ResolvedType collectionElementType = Collections.collectionElementType(type);
      String elementTypeName = typeNameExtractor.typeName(ModelContext.fromParent(modelContext, collectionElementType));
      return new ModelRef(Collections.containerType(type), elementTypeName);
    }
    if (Maps.isMapType(type)) {
      String elementTypeName = typeNameExtractor.typeName(ModelContext.fromParent(modelContext, Maps.mapValueType(type)));
      return new ModelRef("Map", elementTypeName, true);
    }
    String typeName = typeNameExtractor.typeName(ModelContext.fromParent(modelContext, type));
    return new ModelRef(typeName);
  }


  private int httpStatusCode(HandlerMethod handlerMethod) {
    Optional<ResponseStatus> responseStatus
            = fromNullable(getAnnotation(handlerMethod.getMethod(), ResponseStatus.class));
    int httpStatusCode = HttpStatus.OK.value();
    if (responseStatus.isPresent()) {
      httpStatusCode = responseStatus.get().value().value();
    }
    return httpStatusCode;
  }

  private String message(HandlerMethod handlerMethod) {
    Optional<ResponseStatus> responseStatus
            = fromNullable(getAnnotation(handlerMethod.getMethod(), ResponseStatus.class));
    String reasonPhrase = HttpStatus.OK.getReasonPhrase();
    if (responseStatus.isPresent()) {
      reasonPhrase = responseStatus.get().reason();
    }
    return reasonPhrase;
  }

}
