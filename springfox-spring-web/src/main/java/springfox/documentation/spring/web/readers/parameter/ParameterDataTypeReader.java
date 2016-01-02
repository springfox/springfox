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

package springfox.documentation.spring.web.readers.parameter;

import com.fasterxml.classmate.ResolvedType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.schema.TypeNameExtractor;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.contexts.ModelContext;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterContext;

import static springfox.documentation.schema.ResolvedTypes.*;
import static springfox.documentation.spi.schema.contexts.ModelContext.*;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ParameterDataTypeReader implements ParameterBuilderPlugin {
  private final TypeNameExtractor nameExtractor;

  @Autowired
  public ParameterDataTypeReader(TypeNameExtractor nameExtractor) {
    this.nameExtractor = nameExtractor;
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

  @Override
  public void apply(ParameterContext context) {
    ResolvedMethodParameter methodParameter = context.resolvedMethodParameter();
    ResolvedType parameterType = methodParameter.getResolvedParameterType();
    parameterType = context.alternateFor(parameterType);
    ModelContext modelContext = inputParam(parameterType,
        context.getDocumentationType(),
        context.getAlternateTypeProvider(),
        context.getGenericNamingStrategy());
    context.parameterBuilder()
            .type(parameterType)
            .modelRef(modelRefFactory(modelContext, nameExtractor).apply(parameterType));
  }
}
