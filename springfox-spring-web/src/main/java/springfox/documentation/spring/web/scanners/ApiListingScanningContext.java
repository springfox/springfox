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

package springfox.documentation.spring.web.scanners;

import com.google.common.collect.Ordering;
import springfox.documentation.service.ApiDescription;
import springfox.documentation.service.ResourceGroup;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.DocumentationContext;
import springfox.documentation.spi.service.contexts.RequestMappingContext;

import java.util.List;
import java.util.Map;

public class ApiListingScanningContext {
  private final DocumentationContext documentationContext;
  private final Map<ResourceGroup, List<RequestMappingContext>> requestMappingsByResourceGroup;

  public ApiListingScanningContext(
      DocumentationContext documentationContext,
      Map<ResourceGroup, List<RequestMappingContext>>  requestMappingsByResourceGroup) {
    this.documentationContext = documentationContext;
    this.requestMappingsByResourceGroup = requestMappingsByResourceGroup;
  }

  public Map<ResourceGroup, List<RequestMappingContext>> getRequestMappingsByResourceGroup() {
    return requestMappingsByResourceGroup;
  }

  public DocumentationContext getDocumentationContext() {
    return documentationContext;
  }

  public Ordering<ApiDescription> apiDescriptionOrdering() {
    return documentationContext.getApiDescriptionOrdering();
  }

  public DocumentationType getDocumentationType() {
    return documentationContext.getDocumentationType();
  }
}
