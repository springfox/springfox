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

package springfox.documentation.spi.service.contexts;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.AlternateTypeProvider;
import springfox.documentation.spi.schema.GenericTypeNamingStrategy;
import springfox.documentation.spi.schema.contexts.ModelContext;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;

public class OperationModelContextsBuilder {
  private final DocumentationType documentationType;
  private final AlternateTypeProvider alternateTypeProvider;
  private final GenericTypeNamingStrategy genericsNamingStrategy;
  private final ImmutableSet<Class> ignorableTypes;
  private final List<ModelContext> contexts = newArrayList();
  private final Map<ResolvedMethodParameter, ModelContext> contextsLinks = newHashMap();

  public OperationModelContextsBuilder(
      DocumentationType documentationType,
      AlternateTypeProvider alternateTypeProvider,
      GenericTypeNamingStrategy genericsNamingStrategy,
      ImmutableSet<Class> ignorableParameterTypes) {
    this.documentationType = documentationType;
    this.alternateTypeProvider = alternateTypeProvider;
    this.genericsNamingStrategy = genericsNamingStrategy;
    ignorableTypes = ignorableParameterTypes;
  }

  public ModelContext returnType(Type type) {
    ModelContext returnValue = ModelContext.returnValue(
        type,
        documentationType,
        alternateTypeProvider,
        genericsNamingStrategy,
        ignorableTypes);
    this.contexts.add(returnValue);
    return returnValue;
  }

  public ModelContext inputType(Type type) {
    ModelContext inputParam = ModelContext.inputParam(
        type,
        documentationType,
        alternateTypeProvider,
        genericsNamingStrategy,
        ignorableTypes);
    this.contexts.add(inputParam);
    return inputParam;
  }
  
  public ModelContext inputParam(Type type, ResolvedMethodParameter parameter) {
    if (!contextsLinks.containsKey(parameter)) {
      contextsLinks.put(parameter, parameter.isReturnType()?this.returnType(type):this.inputType(type));
    }
    return contextsLinks.get(parameter);
  }
  
  public List<ModelContext> build() {
    return ImmutableList.copyOf(contexts);
  }
}
