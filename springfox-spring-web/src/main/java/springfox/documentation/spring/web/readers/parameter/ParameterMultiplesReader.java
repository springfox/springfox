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
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterContext;

import static springfox.documentation.schema.Collections.*;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ParameterMultiplesReader implements ParameterBuilderPlugin {
  @Override
  public void apply(ParameterContext context) {
    ResolvedType parameterType = context.resolvedMethodParameter().getParameterType();
    context.parameterBuilder().allowMultiple(isCollectionType(parameterType));
  }

  private boolean isCollectionType(ResolvedType parameterType) {
    return isContainerType(parameterType) || Iterable.class.isAssignableFrom(parameterType
    .getErasedType());
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }
}
