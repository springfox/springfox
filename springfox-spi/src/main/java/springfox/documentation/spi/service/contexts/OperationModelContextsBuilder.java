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
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.AlternateTypeProvider;
import springfox.documentation.spi.schema.GenericTypeNamingStrategy;
import springfox.documentation.spi.schema.contexts.ModelContext;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

public class OperationModelContextsBuilder {
  private final String group;
  private final DocumentationType documentationType;
  private final String requestMappingId;
  private final AlternateTypeProvider alternateTypeProvider;
  private final GenericTypeNamingStrategy genericsNamingStrategy;
  private final Set<Class> ignorableTypes;
  private final Set<ModelContext> contexts = new HashSet<>();

  private int parameterIndex = 0;

  public OperationModelContextsBuilder(
      String group,
      DocumentationType documentationType,
      String requestMappingId,
      AlternateTypeProvider alternateTypeProvider,
      GenericTypeNamingStrategy genericsNamingStrategy,
      Set<Class> ignorableParameterTypes) {
    this.group = group;
    this.documentationType = documentationType;
    this.requestMappingId = requestMappingId;
    this.alternateTypeProvider = alternateTypeProvider;
    this.genericsNamingStrategy = genericsNamingStrategy;
    ignorableTypes = ignorableParameterTypes;
  }

  public ModelContext addReturn(ResolvedType type) {
    return addReturn(type, Optional.empty());
  }

  public ModelContext addReturn(ResolvedType type, Optional<ResolvedType> view) {
    ModelContext returnValue = ModelContext.returnValue(
        String.format("%s_%s", requestMappingId, parameterIndex),
        group,
        type,
        view,
        documentationType,
        alternateTypeProvider,
        genericsNamingStrategy,
        ignorableTypes);
    if (this.contexts.add(returnValue)) {
      ++parameterIndex;
      return returnValue;
    }

    return contexts.stream()
        .filter(context -> context.equals(returnValue))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("Expecting at least one matching model context"));
  }

  public ModelContext addInputParam(ResolvedType type) {
    return addInputParam(type, Optional.empty(), new HashSet<>());
  }

  public ModelContext addInputParam(
      ResolvedType type,
      Optional<ResolvedType> view,
      Set<ResolvedType> validationGroups) {
    validationGroups = new HashSet<>(validationGroups);
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
    if (this.contexts.add(inputParam)) {
      ++parameterIndex;
      return inputParam;
    }

    return contexts.stream()
        .filter(context -> context.equals(inputParam))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("Expecting at least one matching model context"));
  }

  public Set<ModelContext> build() {
    Comparator<ModelContext> byParameterId = Comparator.comparing(ModelContext::getParameterId);

    Supplier<TreeSet<ModelContext>> supplier = () -> new TreeSet<>(byParameterId);

    return contexts.stream()
        .map(ModelContext::copy)
        .collect(collectingAndThen(Collectors.toCollection(supplier), Collections::unmodifiableSet));
  }
}
