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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.schema.TypeNameExtractor;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.contexts.ModelContext;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;

import static springfox.documentation.schema.ResolvedTypes.*;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class OperationResponseClassReader implements OperationBuilderPlugin {
  private static Logger log = LoggerFactory.getLogger(OperationResponseClassReader.class);
  private final TypeNameExtractor nameExtractor;

  @Autowired
  public OperationResponseClassReader(TypeNameExtractor nameExtractor) {
    this.nameExtractor = nameExtractor;
  }

  @Override
  public void apply(OperationContext context) {
    ResolvedType returnType = context.getReturnType();
    returnType = context.alternateFor(returnType);
    ModelContext modelContext = ModelContext.returnValue(
        context.getGroupName(),
        returnType,
        context.getDocumentationType(),
        context.getAlternateTypeProvider(),
        context.getGenericsNamingStrategy(),
        context.getIgnorableParameterTypes());
    String responseTypeName = nameExtractor.typeName(modelContext);
    log.debug("Setting spring response class to: {}", responseTypeName);

    context.operationBuilder().responseModel(modelRefFactory(modelContext, nameExtractor).apply(returnType));
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }
}
