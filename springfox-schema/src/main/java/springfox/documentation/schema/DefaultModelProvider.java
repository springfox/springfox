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

package springfox.documentation.schema;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import springfox.documentation.schema.plugins.SchemaPluginsManager;
import springfox.documentation.schema.property.provider.ModelPropertiesProvider;
import springfox.documentation.spi.schema.contexts.ModelContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.*;


@Component
public class DefaultModelProvider implements ModelProvider {
  private final TypeResolver resolver;
  private final ModelPropertiesProvider propertiesProvider;
  private final ModelDependencyProvider dependencyProvider;
  private final SchemaPluginsManager schemaPluginsManager;
  private final TypeNameExtractor typeNameExtractor;

  @Autowired
  public DefaultModelProvider(TypeResolver resolver,
                              @Qualifier("default") ModelPropertiesProvider propertiesProvider,
                              ModelDependencyProvider dependencyProvider,
                              SchemaPluginsManager schemaPluginsManager,
                              TypeNameExtractor typeNameExtractor) {
    this.resolver = resolver;
    this.propertiesProvider = propertiesProvider;
    this.dependencyProvider = dependencyProvider;
    this.schemaPluginsManager = schemaPluginsManager;
    this.typeNameExtractor = typeNameExtractor;
  }

  @Override
  public com.google.common.base.Optional<Model> modelFor(ModelContext modelContext) {
    ResolvedType propertiesHost = modelContext.alternateFor(modelContext.resolvedType(resolver));
    if (Collections.isContainerType(propertiesHost)
            || Maps.isMapType(propertiesHost)
            || propertiesHost.getErasedType().isEnum()
            || Types.isBaseType(Types.typeNameFor(propertiesHost.getErasedType()))
            || modelContext.hasSeenBefore(propertiesHost)) {
      return Optional.absent();
    }
    Map<String, ModelProperty> properties = newTreeMap();
    properties.putAll(uniqueIndex(properties(modelContext, propertiesHost), byPropertyName()));

    return Optional.of(modelBuilder(propertiesHost, properties, modelContext));
  }

  private Model modelBuilder(ResolvedType propertiesHost,
                                    Map<String, ModelProperty> properties,
                                    ModelContext modelContext) {
    String typeName = typeNameExtractor.typeName(ModelContext.fromParent(modelContext, propertiesHost));
    modelContext.getBuilder()
            .id(typeName)
            .type(propertiesHost)
            .name(typeName)
            .qualifiedType(ResolvedTypes.simpleQualifiedTypeName(propertiesHost))
            .properties(properties)
            .description("")
            .baseModel("")
            .discriminator("")
            .subTypes(new ArrayList<String>());
    return schemaPluginsManager.model(modelContext);
  }

  @Override
  public Map<String, Model> dependencies(ModelContext modelContext) {
    Map<String, Model> models = newHashMap();
    for (ResolvedType resolvedType : dependencyProvider.dependentModels(modelContext)) {
      Optional<Model> model = modelFor(ModelContext.fromParent(modelContext, resolvedType));
      if (model.isPresent()) {
        models.put(model.get().getName(), model.get());
      }
    }
    return models;
  }

  private Function<ModelProperty, String> byPropertyName() {
    return new Function<ModelProperty, String>() {
      @Override
      public String apply(ModelProperty input) {
        return input.getName();
      }
    };
  }

  private List<ModelProperty> properties(ModelContext context, ResolvedType propertiesHost) {
    return propertiesProvider.propertiesFor(propertiesHost, context);
  }
}
