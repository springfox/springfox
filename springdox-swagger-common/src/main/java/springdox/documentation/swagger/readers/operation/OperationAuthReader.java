package springdox.documentation.swagger.readers.operation;

import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.Authorization;
import com.wordnik.swagger.annotations.AuthorizationScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import springdox.documentation.builders.AuthorizationBuilder;
import springdox.documentation.builders.AuthorizationScopeBuilder;
import springdox.documentation.spi.DocumentationType;
import springdox.documentation.spi.service.OperationBuilderPlugin;
import springdox.documentation.spi.service.contexts.AuthorizationContext;
import springdox.documentation.spi.service.contexts.OperationContext;

import java.util.List;

import static com.google.common.base.Strings.*;
import static com.google.common.collect.Lists.*;
import static springdox.documentation.swagger.common.SwaggerPluginSupport.*;

@Component
public class OperationAuthReader implements OperationBuilderPlugin {

  private static final Logger LOG = LoggerFactory.getLogger(OperationAuthReader.class);
  @Override
  public void apply(OperationContext context) {

    AuthorizationContext authorizationContext = context.authorizationContext();

    HandlerMethod handlerMethod = context.getHandlerMethod();
    String requestMappingPattern = context.requestMappingPattern();
    List<springdox.documentation.service.Authorization> authorizations = newArrayList();

    if (null != authorizationContext) {
      authorizations = authorizationContext.getAuthorizationsForPath(requestMappingPattern);
    }

    ApiOperation apiOperationAnnotation = handlerMethod.getMethodAnnotation(ApiOperation.class);

    if (null != apiOperationAnnotation && null != apiOperationAnnotation.authorizations()) {
      Authorization[] authorizationAnnotations = apiOperationAnnotation.authorizations();
      if (authorizationAnnotations != null
              && authorizationAnnotations.length > 0
              && StringUtils.hasText(authorizationAnnotations[0].value())) {

        authorizations = newArrayList();
        for (Authorization authorization : authorizationAnnotations) {
          String value = authorization.value();
          AuthorizationScope[] scopes = authorization.scopes();
          List<springdox.documentation.service.AuthorizationScope> authorizationScopeList = newArrayList();
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
          springdox.documentation.service.AuthorizationScope[] authorizationScopes = authorizationScopeList
                  .toArray(new springdox.documentation.service.AuthorizationScope[authorizationScopeList.size()]);
          springdox.documentation.service.Authorization authorizationModel =
                  new AuthorizationBuilder()
                          .type(value)
                          .scopes(authorizationScopes)
                          .build();
          authorizations.add(authorizationModel);
        }
      }
    }
    if (authorizations != null) {
      LOG.debug("Authorization count {} for method {}", authorizations.size(), handlerMethod.getMethod().getName());
      context.operationBuilder().authorizations(authorizations);
    }
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return pluginDoesApply(delimiter);
  }
}
