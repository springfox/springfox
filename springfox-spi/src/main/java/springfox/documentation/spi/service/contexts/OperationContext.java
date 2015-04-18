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

package springfox.documentation.spi.service.contexts;

import com.fasterxml.classmate.ResolvedType;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import springfox.documentation.builders.OperationBuilder;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.AlternateTypeProvider;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.*;

public class OperationContext {
  private final OperationBuilder operationBuilder;
  private final RequestMethod requestMethod;
  private final HandlerMethod handlerMethod;
  private final int operationIndex;
  private final RequestMappingInfo requestMappingInfo;
  private final DocumentationContext documentationContext;
  private final String requestMappingPattern;

  public OperationContext(OperationBuilder operationBuilder, RequestMethod requestMethod, HandlerMethod
          handlerMethod, int operationIndex, RequestMappingInfo requestMappingInfo,
                          DocumentationContext documentationContext, String requestMappingPattern) {
    this.operationBuilder = operationBuilder;
    this.requestMethod = requestMethod;
    this.handlerMethod = handlerMethod;
    this.operationIndex = operationIndex;
    this.requestMappingInfo = requestMappingInfo;
    this.documentationContext = documentationContext;
    this.requestMappingPattern = requestMappingPattern;
  }

  public OperationBuilder operationBuilder() {
    return operationBuilder;
  }

  public HttpMethod httpMethod() {
    return HttpMethod.valueOf(requestMethod.toString());
  }

  public HandlerMethod getHandlerMethod() {
    return handlerMethod;
  }

  public int operationIndex() {
    return operationIndex;
  }


  public List<ResponseMessage> getGlobalResponseMessages(String forHttpMethod) {
    if (documentationContext.getGlobalResponseMessages().containsKey(RequestMethod.valueOf(forHttpMethod))) {
      return documentationContext.getGlobalResponseMessages().get(RequestMethod.valueOf(forHttpMethod));
    }
    return newArrayList();
  }

  public SecurityContext securityContext() {
    return documentationContext.getSecurityContext();
  }

  public String requestMappingPattern() {
    return requestMappingPattern;
  }

  public RequestMappingInfo getRequestMappingInfo() {
    return requestMappingInfo;
  }

  public DocumentationContext getDocumentationContext() {
    return documentationContext;
  }

  public DocumentationType getDocumentationType() {
    return documentationContext.getDocumentationType();
  }

  public AlternateTypeProvider getAlternateTypeProvider() {
    return documentationContext.getAlternateTypeProvider();
  }

  public ResolvedType alternateFor(ResolvedType resolved) {
    return getAlternateTypeProvider().alternateFor(resolved);
  }

  public Set<MediaType> produces() {
    return requestMappingInfo.getProducesCondition().getProducibleMediaTypes();
  }

  public Set<MediaType> consumes() {
    return requestMappingInfo.getConsumesCondition().getConsumableMediaTypes();
  }
}
