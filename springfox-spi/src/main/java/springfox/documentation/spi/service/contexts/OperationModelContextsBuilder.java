/*
 *
 *  Copyright 2015-2018 the original author or authors.
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
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.AlternateTypeProvider;
import springfox.documentation.spi.schema.GenericTypeNamingStrategy;
import springfox.documentation.spi.schema.contexts.ModelContext;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Sets.*;

public class OperationModelContextsBuilder {
  private final String group;
  private final DocumentationType documentationType;
  private final String requestMappingId;
  private final AlternateTypeProvider alternateTypeProvider;
  private final GenericTypeNamingStrategy genericsNamingStrategy;
  private final ImmutableSet<Class> ignorableTypes;
  private final Set<ModelContext> contexts = newLinkedHashSet();
  
  private int parameterIndex = 0; 

  public OperationModelContextsBuilder(
      String group,
      DocumentationType documentationType,
      String requestMappingId,
      AlternateTypeProvider alternateTypeProvider,
      GenericTypeNamingStrategy genericsNamingStrategy,
      ImmutableSet<Class> ignorableParameterTypes) {
    this.group = group;
    this.documentationType = documentationType;
    this.requestMappingId = requestMappingId;
    this.alternateTypeProvider = alternateTypeProvider;
    this.genericsNamingStrategy = genericsNamingStrategy;
    ignorableTypes = ignorableParameterTypes;
  }
  
  public ModelContext addReturn(ResolvedType type) {
    return addReturn(type, Optional.<ResolvedType>absent());
  }

  public ModelContext addReturn(ResolvedType type, Optional<ResolvedType> view) {
    Optional<ModelContext> context = 
        from(contexts).filter(sameContext(type, view, new HashSet<ResolvedType>(), true)).first();

    if (context.isPresent()) {
      return context.get();
    } else {
      ModelContext returnValue = ModelContext.returnValue(
          String.format("%s_%s", requestMappingId, parameterIndex),
          group,
          type,
          view,
          documentationType,
          alternateTypeProvider,
          genericsNamingStrategy,
          ignorableTypes);
      this.contexts.add(returnValue);
      ++parameterIndex;
      return returnValue;
    }
  }

  public ModelContext addInputParam(ResolvedType type) {
    return addInputParam(type, Optional.<ResolvedType>absent(), Sets.<ResolvedType>newHashSet());
  }
  
  public ModelContext addInputParam(ResolvedType type, Optional<ResolvedType> view, Set<ResolvedType> validationGroups) {
    validationGroups = newHashSet(validationGroups);
    Optional<ModelContext> context = from(contexts).filter(sameContext(type, view, validationGroups, false)).first();

    if (context.isPresent()) {
      return context.get();
    } else {
      ModelContext inputParam = ModelContext.inputParam(
          String.format("%s_%s", requestMappingId, parameterIndex),
          group,
          type,
          view,
          validationGroups,
          documentationType,
          alternateTypeProvider,
          genericsNamingStrategy,
          ignorableTypes);
      this.contexts.add(inputParam);
      ++parameterIndex;
      return inputParam;
    }
  }

  public Set<ModelContext> build() {
    return Collections.unmodifiableSet(contexts);
  }

  private Predicate<? super ModelContext> sameContext(final ResolvedType type, final Optional<ResolvedType> view,
      final Set<ResolvedType> validationGroups, final boolean isReturnType) {
    return new Predicate<ModelContext>() {
      @Override
      public boolean apply(ModelContext context) {
        return context.getType().equals(type) && context.getView().equals(view)
            && context.getValidationGroups().equals(validationGroups) && context.isReturnType() == isReturnType;
      }
    };
  }

}
