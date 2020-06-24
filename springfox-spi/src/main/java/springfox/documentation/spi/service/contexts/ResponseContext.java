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
package springfox.documentation.spi.service.contexts;

import com.fasterxml.classmate.ResolvedType;
import springfox.documentation.builders.ResponseBuilder;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.AlternateTypeProvider;

public class ResponseContext {
  private final DocumentationContext documentationContext;
  private final OperationContext operationContext;
  private final ResponseBuilder responseBuilder = new ResponseBuilder();

  public ResponseContext(
      DocumentationContext documentationContext,
      OperationContext operationContext) {

    this.documentationContext = documentationContext;
    this.operationContext = operationContext;
  }

  public ResponseBuilder responseBuilder() {
    return responseBuilder;
  }

  public DocumentationContext getDocumentationContext() {
    return documentationContext;
  }

  public DocumentationType getDocumentationType() {
    return documentationContext.getDocumentationType();
  }

  public ResolvedType alternateFor(ResolvedType source) {
    return getAlternateTypeProvider().alternateFor(source);
  }

  private AlternateTypeProvider getAlternateTypeProvider() {
    return documentationContext.getAlternateTypeProvider();
  }

  public OperationContext getOperationContext() {
    return operationContext;
  }

  public String getGroupName() {
    return operationContext.getGroupName();
  }
}
