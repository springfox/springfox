/*
 *
 *  Copyright 2017-2019 the original author or authors.
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
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.AlternateTypeProvider;
import springfox.documentation.spi.service.contexts.DocumentationContext;
import springfox.documentation.spi.service.contexts.OperationContext;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


public class ExpansionContext {
  private final String parentName;
  private final ResolvedType paramType;
  private final OperationContext operationContext;
  private final Set<ResolvedType> seenTypes;

  public ExpansionContext(
      String parentName,
      ResolvedType paramType,
      OperationContext operationContext) {
    this(parentName, paramType, operationContext, new HashSet<>());
  }

  private ExpansionContext(
      String parentName,
      ResolvedType paramType,
      OperationContext operationContext,
      Set<ResolvedType> seenTypes) {
    this.parentName = parentName;
    this.paramType = paramType;
    this.operationContext = operationContext;
    this.seenTypes = new HashSet<>(seenTypes);
  }


  public String getParentName() {
    return parentName;
  }

  public ResolvedType getParamType() {
    return paramType;
  }

  public OperationContext getOperationContext() {
    return operationContext;
  }

  public DocumentationContext getDocumentationContext() {
    return operationContext.getDocumentationContext();
  }

  public boolean hasSeenType(ResolvedType type) {
    return seenTypes.contains(type)
        || Objects.equals(type, paramType);
  }

  public ExpansionContext childContext(
      String parentName,
      ResolvedType childType,
      OperationContext operationContext) {
    Set<ResolvedType> childSeenTypes = new HashSet<>(seenTypes);
    childSeenTypes.add(childType);
    return new ExpansionContext(parentName, childType, operationContext, childSeenTypes);
  }

  public AlternateTypeProvider getAlternateTypeProvider() {
    return operationContext.getAlternateTypeProvider();
  }

  public DocumentationType getDocumentationType() {
    return operationContext.getDocumentationType();
  }

  public Collection<Class> ignorableTypes() {
    return operationContext.getIgnorableParameterTypes();
  }
}
