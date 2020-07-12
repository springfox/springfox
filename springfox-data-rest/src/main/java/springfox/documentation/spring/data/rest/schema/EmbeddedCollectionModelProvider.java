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
package springfox.documentation.spring.data.rest.schema;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.LinkRelationProvider;
import springfox.documentation.builders.ModelPropertyBuilder;
import springfox.documentation.builders.ModelSpecificationBuilder;
import springfox.documentation.builders.PropertySpecificationBuilder;
import springfox.documentation.schema.CollectionType;
import springfox.documentation.schema.ModelSpecification;
import springfox.documentation.schema.PropertySpecification;
import springfox.documentation.schema.TypeNameExtractor;
import springfox.documentation.schema.Xml;
import springfox.documentation.schema.property.ModelSpecificationFactory;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.EnumTypeDeterminer;
import springfox.documentation.spi.schema.SyntheticModelProviderPlugin;
import springfox.documentation.spi.schema.contexts.ModelContext;

import java.util.List;
import java.util.Set;

import static java.util.Collections.*;
import static java.util.function.Function.*;
import static java.util.stream.Collectors.*;
import static springfox.documentation.schema.ResolvedTypes.*;

class EmbeddedCollectionModelProvider implements SyntheticModelProviderPlugin {

  private final TypeResolver resolver;
  private final LinkRelationProvider relProvider;
  private final TypeNameExtractor typeNameExtractor;
  private final EnumTypeDeterminer enumTypeDeterminer;
  private final ModelSpecificationFactory modelSpecifications;

  EmbeddedCollectionModelProvider(
      TypeResolver resolver,
      LinkRelationProvider relProvider,
      TypeNameExtractor typeNameExtractor,
      EnumTypeDeterminer enumTypeDeterminer,
      ModelSpecificationFactory modelSpecifications) {
    this.resolver = resolver;
    this.relProvider = relProvider;
    this.typeNameExtractor = typeNameExtractor;
    this.enumTypeDeterminer = enumTypeDeterminer;
    this.modelSpecifications = modelSpecifications;
  }

  @SuppressWarnings("deprecation")
  @Override
  public springfox.documentation.schema.Model create(ModelContext context) {
    ResolvedType resourceType = resolver.resolve(context.getType());
    List<ResolvedType> typeParameters = resourceType.getTypeParameters();
    Class<?> type = typeParameters.get(0).getErasedType();
    String name = typeNameExtractor.typeName(context);
    return context.getBuilder()
        .description(String.format(
            "Embedded collection of %s",
            type.getSimpleName()))
        .name(name)
        .qualifiedType(type.getName())
        .type(typeParameters.get(0))
        .properties(properties(context).stream()
            .collect(toMap(springfox.documentation.schema.ModelProperty::getName, identity())))
        .xml(new Xml()
            .wrapped(true)
            .name("content")
        )
        .build();
  }

  @Override
  @SuppressWarnings("deprecation")
  public List<springfox.documentation.schema.ModelProperty> properties(ModelContext context) {
    ResolvedType resourceType = resolver.resolve(context.getType());
    List<ResolvedType> typeParameters = resourceType.getTypeParameters();
    Class<?> type = typeParameters.get(0).getErasedType();
    return singletonList(
        new ModelPropertyBuilder()
            .name(relProvider.getCollectionResourceRelFor(type).value())
            .type(resolver.resolve(List.class, type))
            .qualifiedType(CollectionModel.class.getName())
            .position(0)
            .required(true)
            .isHidden(false)
            .description("Resource collection")
            .build()
            .updateModelRef(modelRefFactory(context, enumTypeDeterminer, typeNameExtractor)));
  }

  @Override
  public ModelSpecification createModelSpecification(ModelContext context) {
    ResolvedType resourceType = resolver.resolve(context.getType());
    List<ResolvedType> typeParameters = resourceType.getTypeParameters();
    Class<?> type = typeParameters.get(0).getErasedType();
    String name = typeNameExtractor.typeName(context);
    return context.getModelSpecificationBuilder()
        .name(name)
        .facets(f -> f.description(String.format(
            "Embedded collection of %s",
            type.getSimpleName()))
            .xml(new Xml()
                .wrapped(true)
                .name("content")))
        .compoundModel(cm ->
            cm.properties(propertySpecifications(context))
                .modelKey(m ->
                    m.isResponse(context.isReturnType())
                        .qualifiedModelName(q ->
                            q.namespace("springfox.documentation.spring.data.rest.schema")
                                .name(name))
                        .build()))
        .build();
  }

  @Override
  public List<PropertySpecification> propertySpecifications(ModelContext context) {
    ResolvedType resourceType = resolver.resolve(context.getType());
    List<ResolvedType> typeParameters = resourceType.getTypeParameters();
    Class<?> type = typeParameters.get(0).getErasedType();
    ModelSpecification modelSpecification = new ModelSpecificationBuilder()
        .collectionModel(c ->
            c.model(m -> m.copyOf(modelSpecifications.create(context,
                typeParameters.get(0))))
                .collectionType(CollectionType.LIST))
        .build();
    return singletonList(
        new PropertySpecificationBuilder(relProvider.getCollectionResourceRelFor(type).value())
            .type(modelSpecification)
            .position(0)
            .required(true)
            .isHidden(false)
            .description("Resource collection")
            .build());
  }

  @Override
  public Set<ResolvedType> dependencies(ModelContext context) {
    ResolvedType resourceType = resolver.resolve(context.getType());
    List<ResolvedType> typeParameters = resourceType.getTypeParameters();
    Class<?> type = typeParameters.get(0).getErasedType();

    return singleton(resolver.resolve(type));
  }

  @Override
  public boolean supports(ModelContext delimiter) {
    return EmbeddedCollection.class.equals(resolver.resolve(delimiter.getType()).getErasedType())
        && (delimiter.getDocumentationType() == DocumentationType.SWAGGER_2
        || delimiter.getDocumentationType() == DocumentationType.OAS_30);
  }

}
