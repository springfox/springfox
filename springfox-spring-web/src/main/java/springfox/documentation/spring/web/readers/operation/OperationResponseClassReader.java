/*
 *
 *  Copyright 2015-2019 the original author or authors.
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
import springfox.documentation.schema.plugins.SchemaPluginsManager;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.EnumTypeDeterminer;
import springfox.documentation.spi.schema.ViewProviderPlugin;
import springfox.documentation.spi.schema.contexts.ModelContext;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

import static springfox.documentation.schema.ResolvedTypes.*;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class OperationResponseClassReader implements OperationBuilderPlugin {
  private static Logger log = LoggerFactory.getLogger(OperationResponseClassReader.class);
  private final EnumTypeDeterminer enumTypeDeterminer;
  private final TypeNameExtractor nameExtractor;
  private final SchemaPluginsManager pluginsManager;

  @Autowired
  public OperationResponseClassReader(SchemaPluginsManager pluginsManager,
          EnumTypeDeterminer enumTypeDeterminer,
          TypeNameExtractor nameExtractor) {
    this.enumTypeDeterminer = enumTypeDeterminer;
    this.nameExtractor = nameExtractor;
    this.pluginsManager = pluginsManager;
  }

  @SuppressWarnings("deprecation")
  @Override
  public void apply(OperationContext context) {
    ResolvedType returnType = context.getReturnType();
    returnType = context.alternateFor(returnType);
    
    ViewProviderPlugin viewProvider = 
        pluginsManager.viewProvider(context.getDocumentationContext().getDocumentationType());

    ModelContext modelContext = context.operationModelsBuilder().addReturn(
        returnType,
        viewProvider.viewFor(context));

    Map<String, String> knownNames;
    knownNames = new HashMap<>();
    Optional.ofNullable(context.getKnownModels().get(modelContext.getParameterId()))
            .orElse(new HashSet<>())
        .forEach(model -> knownNames.put(model.getId(), model.getName()));

    String responseTypeName = nameExtractor.typeName(modelContext);
    log.debug("Setting spring response class to: {}", responseTypeName);

    context.operationBuilder().responseModel(
        modelRefFactory(modelContext, enumTypeDeterminer, nameExtractor, knownNames).apply(returnType));
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }
}
