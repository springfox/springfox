/*
 *
 *  Copyright 2015-2018 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */
package springfox.documentation.spring.web.readers.operation;

import com.fasterxml.classmate.ResolvedType;
import com.google.common.base.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelReference;
import springfox.documentation.schema.TypeNameExtractor;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.contexts.ModelContext;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;

import java.util.List;

import static com.google.common.collect.Sets.*;
import static springfox.documentation.schema.ResolvedTypes.*;
import static springfox.documentation.schema.Types.*;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ResponseMessagesReader implements OperationBuilderPlugin {

  private final TypeNameExtractor typeNameExtractor;

  @Autowired
  public ResponseMessagesReader(TypeNameExtractor typeNameExtractor) {
    this.typeNameExtractor = typeNameExtractor;
  }

  @Override
  public void apply(OperationContext context) {
    List<ResponseMessage> responseMessages = context.getGlobalResponseMessages(context.httpMethod().toString());
    context.operationBuilder().responseMessages(newHashSet(responseMessages));
    applyReturnTypeOverride(context);
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

  private void applyReturnTypeOverride(OperationContext context) {

    ResolvedType returnType = context.alternateFor(context.getReturnType());
    int httpStatusCode = httpStatusCode(context);
    String message = message(context);
    ModelReference modelRef = null;
    if (!isVoid(returnType)) {
      ModelContext modelContext = ModelContext.returnValue(
          context.getGroupName(),
          returnType,
          context.getDocumentationType(),
          context.getAlternateTypeProvider(),
          context.getGenericsNamingStrategy(),
          context.getIgnorableParameterTypes());
      modelRef = modelRefFactory(modelContext, typeNameExtractor).apply(returnType);
    }
    ResponseMessage built = new ResponseMessageBuilder()
        .code(httpStatusCode)
        .message(message)
        .responseModel(modelRef)
        .build();
    context.operationBuilder().responseMessages(newHashSet(built));
  }


  public static int httpStatusCode(OperationContext context) {
    Optional<ResponseStatus> responseStatus = context.findAnnotation(ResponseStatus.class);
    int httpStatusCode = HttpStatus.OK.value();
    if (responseStatus.isPresent()) {
      httpStatusCode = responseStatus.get().value().value();
    }
    return httpStatusCode;
  }

  public static String message(OperationContext context) {
    Optional<ResponseStatus> responseStatus = context.findAnnotation(ResponseStatus.class);
    String reasonPhrase = HttpStatus.OK.getReasonPhrase();
    if (responseStatus.isPresent()) {
      reasonPhrase = responseStatus.get().reason();
      if (reasonPhrase.isEmpty()) {
        reasonPhrase = responseStatus.get().value().getReasonPhrase();
      }
    }
    return reasonPhrase;
  }

}
