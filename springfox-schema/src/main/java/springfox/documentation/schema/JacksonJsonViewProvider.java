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

import static springfox.documentation.schema.ResolvedTypes.resolvedTypeSignature;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.base.Optional;

import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ViewProviderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spi.service.contexts.RequestMappingContext;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class JacksonJsonViewProvider implements ViewProviderPlugin {
  
  private static final Logger LOG = LoggerFactory.getLogger(JacksonJsonViewProvider.class);

  private final TypeResolver typeResolver;

  @Autowired
  public JacksonJsonViewProvider(TypeResolver typeResolver) {
    this.typeResolver = typeResolver;
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

  @Override
  public Optional<ResolvedType> viewFor(ResolvedType type, ResolvedMethodParameter parameter) {
    return viewFor(type, parameter.findAnnotation(JsonView.class));
  }
  
  @Override
  public Optional<ResolvedType> viewFor(ResolvedType type, RequestMappingContext context) {
    return viewFor(type, context.findAnnotation(JsonView.class));
  }
  
  @Override
  public Optional<ResolvedType> viewFor(ResolvedType type, OperationContext context) {
    return viewFor(type, context.findAnnotation(JsonView.class));
  }
  
  private Optional<ResolvedType> viewFor(ResolvedType type, Optional<JsonView> annotation) {
    Optional<ResolvedType> view = Optional.absent();
    if (annotation.isPresent()) {
      Class<?>[] views = ((JsonView)(annotation.get())).value();
      view = Optional.of(typeResolver.resolve(views[0]));
      LOG.debug("Found view {} for type {}", resolvedTypeSignature(view.get()).or("<null>"), resolvedTypeSignature(type).or("<null>"));
    }
    return view;
  }
}