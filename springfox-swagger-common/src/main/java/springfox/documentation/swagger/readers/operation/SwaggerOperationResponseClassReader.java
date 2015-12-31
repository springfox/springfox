/*
 *
 *  Copyright 2015 the original author or authors.
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

package springfox.documentation.swagger.readers.operation;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import springfox.documentation.schema.TypeNameExtractor;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.contexts.ModelContext;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import static springfox.documentation.schema.ResolvedTypes.modelRefFactory;
import static springfox.documentation.spi.schema.contexts.ModelContext.*;
import static springfox.documentation.swagger.annotations.Annotations.*;

@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
public class SwaggerOperationResponseClassReader implements OperationBuilderPlugin {
  private static Logger log = LoggerFactory.getLogger(SwaggerOperationResponseClassReader.class);
  private final TypeResolver typeResolver;
  private final TypeNameExtractor nameExtractor;

  @Autowired
  public SwaggerOperationResponseClassReader(TypeResolver typeResolver,
                                             TypeNameExtractor nameExtractor) {
    this.typeResolver = typeResolver;
    this.nameExtractor = nameExtractor;
  }

  @Override
  public void apply(OperationContext context) {

    HandlerMethod handlerMethod = context.getHandlerMethod();
    ResolvedType returnType = new HandlerMethodResolver(typeResolver).methodReturnType(handlerMethod);
    returnType = context.alternateFor(returnType);
    returnType = findApiOperationAnnotation(handlerMethod.getMethod())
        .transform(resolvedTypeFromOperation(typeResolver, returnType))
        .or(returnType);
    if (canSkip(context, returnType)) {
      return;
    }
    ModelContext modelContext = returnValue(returnType,
        context.getDocumentationType(),
        context.getAlternateTypeProvider(),
        context.getDocumentationContext().getGenericsNamingStrategy());

    String responseTypeName = nameExtractor.typeName(modelContext);
    log.debug("Setting response class to:" + responseTypeName);

    context.operationBuilder()
            .responseModel(modelRefFactory(modelContext, nameExtractor).apply(returnType));
  }

  private boolean canSkip(OperationContext context, ResolvedType returnType) {
    return context.getDocumentationContext().getIgnorableParameterTypes().contains(returnType);
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return SwaggerPluginSupport.pluginDoesApply(delimiter);
  }
}
