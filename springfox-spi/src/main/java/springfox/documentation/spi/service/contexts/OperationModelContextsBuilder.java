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

import com.google.common.collect.ImmutableSet;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.AlternateTypeProvider;
import springfox.documentation.spi.schema.GenericTypeNamingStrategy;
import springfox.documentation.spi.schema.contexts.ModelContext;

import java.lang.reflect.Type;
import java.util.Set;

import static com.google.common.collect.Sets.*;

public class OperationModelContextsBuilder {
  private final String group;
  private final DocumentationType documentationType;
  private final AlternateTypeProvider alternateTypeProvider;
  private final GenericTypeNamingStrategy genericsNamingStrategy;
  private final ImmutableSet<Class> ignorableTypes;
  private final Set<ModelContext> contexts = newHashSet();

  public OperationModelContextsBuilder(
      String group,
      DocumentationType documentationType,
      AlternateTypeProvider alternateTypeProvider,
      GenericTypeNamingStrategy genericsNamingStrategy,
      ImmutableSet<Class> ignorableParameterTypes) {
    this.group = group;
    this.documentationType = documentationType;
    this.alternateTypeProvider = alternateTypeProvider;
    this.genericsNamingStrategy = genericsNamingStrategy;
    ignorableTypes = ignorableParameterTypes;
  }

  public OperationModelContextsBuilder addReturn(Type type) {
    ModelContext returnValue = ModelContext.returnValue(
        group,
        type,
        documentationType,
        alternateTypeProvider,
        genericsNamingStrategy,
        ignorableTypes);
    this.contexts.add(returnValue);
    return this;
  }

  public OperationModelContextsBuilder addInputParam(Type type) {
    ModelContext inputParam = ModelContext.inputParam(
        group,
        type,
        documentationType,
        alternateTypeProvider,
        genericsNamingStrategy,
        ignorableTypes);
    this.contexts.add(inputParam);
    return this;
  }

  public Set<ModelContext> build() {
    return ImmutableSet.copyOf(contexts);
  }
}
