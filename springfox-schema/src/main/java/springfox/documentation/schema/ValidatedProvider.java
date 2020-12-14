/*
 *
 *  Copyright 2017 the original author or authors.
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

package springfox.documentation.schema;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ValidatedProviderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spi.service.contexts.RequestMappingContext;

import javax.validation.Valid;
import javax.validation.groups.Default;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Order
public class ValidatedProvider implements ValidatedProviderPlugin {

  private static final Logger LOG = LoggerFactory.getLogger(ValidatedProvider.class);

  private final TypeResolver typeResolver;

  @Autowired
  public ValidatedProvider(TypeResolver typeResolver) {
    this.typeResolver = typeResolver;
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

  @Override
  public Set<ResolvedType> validationFor(
      ResolvedMethodParameter parameter) {
    return validationFor(
        String.format("Parameter name: %s @ index: %s",
                      parameter.defaultName().orElse("<none>"),
                      parameter.getParameterIndex()),
        parameter.findAnnotation(Validated.class).orElse(null),
        parameter.findAnnotation(Valid.class).orElse(null)
    );
  }

  @Override
  public Set<ResolvedType> validationFor(
      RequestMappingContext context) {
    return validationFor(
        String.format("Request Mapping: %s", context.getRequestMappingPattern()),
        context.findAnnotation(Validated.class).orElse(null),
        context.findAnnotation(Valid.class).orElse(null)
    );
  }

  @Override
  public Set<ResolvedType> validationFor(
      OperationContext context) {
    return validationFor(
        String.format("Operation: %s", context.getName()),
        context.findAnnotation(Validated.class).orElse(null),
        context.findAnnotation(Valid.class).orElse(null)
    );
  }

  private Set<ResolvedType> validationFor(String context, Validated validated, Valid valid) {
    Set<ResolvedType> resolvedTypes = new HashSet<>();
    if (validated == null && valid == null) {
      return resolvedTypes;
    }

    if (validated != null) {
      Class<?>[] validatedGroups = validated.value();
      if (validatedGroups.length == 0) {
        resolvedTypes.add(typeResolver.resolve(Default.class));
      } else {
        resolvedTypes = Stream.of(validatedGroups).map(typeResolver::resolve).collect(Collectors.toSet());
      }
    } else {
      resolvedTypes.add(typeResolver.resolve(Default.class));
    }

    LOG.debug("Found validation groups {} for type {}",
              resolvedTypes.stream().map(ResolvedType::getErasedType).map(Class::toString)
                           .collect(Collectors.joining(",")),
              context);
    return resolvedTypes;
  }
}