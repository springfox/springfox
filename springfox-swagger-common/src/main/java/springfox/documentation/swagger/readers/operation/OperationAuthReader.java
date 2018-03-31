/*
 *
 *  Copyright 2015-2016 the original author or authors.
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

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import io.swagger.annotations.AuthorizationScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.AuthorizationScopeBuilder;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Strings.*;
import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.newHashMap;

@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
public class OperationAuthReader implements OperationBuilderPlugin {

  private static final Logger LOG = LoggerFactory.getLogger(OperationAuthReader.class);

  @Override
  public void apply(OperationContext context) {

    List<SecurityContext> securityContexts = context.securityContext();

    Map<String, SecurityReference> securityReferences = newHashMap();

    for (SecurityContext each : securityContexts) {
      securityReferences.putAll(
          FluentIterable.from(each.securityForOperation(context))
          .uniqueIndex(byReferenceName()));
    }

    Optional<ApiOperation> apiOperationAnnotation = context.findAnnotation(ApiOperation.class);

    if (apiOperationAnnotation.isPresent()) {

      List<SecurityReference> securityReferenceOverrides = newArrayList();
      for (Authorization authorization : authorizationReferences(apiOperationAnnotation.get())) {
        String value = authorization.value();
        AuthorizationScope[] scopes = authorization.scopes();
        List<springfox.documentation.service.AuthorizationScope> authorizationScopeList = newArrayList();
        for (AuthorizationScope authorizationScope : scopes) {
          String description = authorizationScope.description();
          String scope = authorizationScope.scope();
          // @Authorization has a default blank authorization scope, which we need to
          // ignore in the case of api keys.
          if (!isNullOrEmpty(scope)) {
            authorizationScopeList.add(
                new AuthorizationScopeBuilder()
                    .scope(scope)
                    .description(description)
                    .build());
          }
        }
        springfox.documentation.service.AuthorizationScope[] authorizationScopes
            = authorizationScopeList
            .toArray(new springfox.documentation.service.AuthorizationScope[0]);
        SecurityReference securityReference =
            SecurityReference.builder()
                .reference(value)
                .scopes(authorizationScopes)
                .build();
        securityReferenceOverrides.add(securityReference);
      }
      securityReferences.putAll(FluentIterable.from(securityReferenceOverrides)
          .uniqueIndex(byReferenceName()));
    }
    LOG.debug("Authorization count {} for method {}", securityReferences.size(), context.getName());
    context.operationBuilder().authorizations(securityReferences.values());
  }

  private Function<SecurityReference, String> byReferenceName() {
    return new Function<SecurityReference, String>() {
      @Override
      public String apply(SecurityReference input) {
        return input.getReference();
      }
    };
  }

  private Iterable<Authorization> authorizationReferences(ApiOperation apiOperationAnnotation) {
    return FluentIterable.from(apiOperationAnnotation.authorizations())
        .filter(new Predicate<Authorization>() {
          @Override
          public boolean apply(Authorization input) {
            return !Strings.isNullOrEmpty(input.value());
          }
        });
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return SwaggerPluginSupport.pluginDoesApply(delimiter);
  }
}
