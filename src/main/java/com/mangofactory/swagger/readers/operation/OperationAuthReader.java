package com.mangofactory.swagger.readers.operation;

import com.mangofactory.swagger.authorization.AuthorizationContext;
import com.mangofactory.swagger.readers.Command;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.Authorization;
import com.wordnik.swagger.annotations.AuthorizationScope;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.method.HandlerMethod;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class OperationAuthReader implements Command<RequestMappingContext> {

   @Override
   public void execute(RequestMappingContext context) {
      AuthorizationContext authorizationContext = (AuthorizationContext) context.get("authorizationContext");

      HandlerMethod handlerMethod = context.getHandlerMethod();
      String requestMappingPattern = (String) context.get("requestMappingPattern");
      List<com.wordnik.swagger.model.Authorization> authorizations = newArrayList();

      if (null != authorizationContext) {
         authorizations = authorizationContext.getAuthorizationsForPath(requestMappingPattern);
      }

      ApiOperation apiOperationAnnotation = handlerMethod.getMethodAnnotation(ApiOperation.class);

      if (null != apiOperationAnnotation && null != apiOperationAnnotation.authorizations()) {
         Authorization[] authorizationAnnotations = apiOperationAnnotation.authorizations();
         if (authorizationAnnotations != null
                 && authorizationAnnotations.length > 0
                 && !StringUtils.isBlank(authorizationAnnotations[0].value())) {

            authorizations = newArrayList();
            for (Authorization authorization : authorizationAnnotations) {
               String value = authorization.value();
               AuthorizationScope[] scopes = authorization.scopes();
               List<com.wordnik.swagger.model.AuthorizationScope> authorizationScopeList = newArrayList();
               for (AuthorizationScope authorizationScope : scopes) {
                  String description = authorizationScope.description();
                  String scope = authorizationScope.scope();
                  authorizationScopeList.add(new com.wordnik.swagger.model.AuthorizationScope(scope, description));
               }
               com.wordnik.swagger.model.AuthorizationScope[] authorizationScopes = authorizationScopeList
                       .toArray(new com.wordnik.swagger.model.AuthorizationScope[authorizationScopeList.size()]);
               com.wordnik.swagger.model.Authorization authorizationModel =
                       new com.wordnik.swagger.model.Authorization(value, authorizationScopes);
               authorizations.add(authorizationModel);
            }
         }
      }
      context.put("authorizations", authorizations);
   }
}
