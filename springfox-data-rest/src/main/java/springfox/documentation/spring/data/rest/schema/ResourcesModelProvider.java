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
import org.springframework.hateoas.Link;
import springfox.documentation.builders.ModelPropertyBuilder;
import springfox.documentation.builders.PropertySpecificationBuilder;
import springfox.documentation.schema.Model;
import springfox.documentation.schema.ModelKeyBuilder;
import springfox.documentation.schema.ModelProperty;
import springfox.documentation.schema.ModelSpecification;
import springfox.documentation.schema.PropertySpecification;
import springfox.documentation.schema.QualifiedModelName;
import springfox.documentation.schema.TypeNameExtractor;
import springfox.documentation.schema.Xml;
import springfox.documentation.schema.property.ModelSpecificationFactory;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.EnumTypeDeterminer;
import springfox.documentation.spi.schema.SyntheticModelProviderPlugin;
import springfox.documentation.spi.schema.contexts.ModelContext;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.function.Function.*;
import static java.util.stream.Collectors.*;
import static springfox.documentation.schema.ResolvedTypes.*;
import static springfox.documentation.schema.property.PackageNames.*;

class ResourcesModelProvider implements SyntheticModelProviderPlugin {

  private final TypeResolver resolver;
  private final TypeNameExtractor typeNameExtractor;
  private final EnumTypeDeterminer enumTypeDeterminer;
  private final ModelSpecificationFactory modelSpecifications;

  ResourcesModelProvider(
      TypeResolver resolver,
      TypeNameExtractor typeNameExtractor,
      EnumTypeDeterminer enumTypeDeterminer,
      ModelSpecificationFactory modelSpecifications) {
    this.resolver = resolver;
    this.typeNameExtractor = typeNameExtractor;
    this.enumTypeDeterminer = enumTypeDeterminer;
    this.modelSpecifications = modelSpecifications;
  }

  @Override
  public Model create(ModelContext context) {
    ResolvedType resourceType = resourceType(context.getType());
    List<ResolvedType> typeParameters = resourceType.getTypeParameters();
    Class<?> type = typeParameters.get(0).getErasedType();
    String name = typeNameExtractor.typeName(context);
    return context.getBuilder()
        .description(String.format(
            "Resources of %s",
            type.getSimpleName()))
        .name(name)
        .qualifiedType(type.getName())
        .type(typeParameters.get(0))
        .properties(properties(context).stream().collect(toMap(
            ModelProperty::getName,
            identity())))
        .xml(new Xml()
                 .name("entities")
                 .wrapped(false)
                 .attribute(false))
        .build();
  }

  @Override
  public List<ModelProperty> properties(ModelContext context) {
    ResolvedType resourceType = resourceType(context.getType());
    List<ResolvedType> typeParameters = resourceType.getTypeParameters();
    Class<?> type = typeParameters.get(0).getErasedType();
    ResolvedType embedded = resolver.resolve(
        EmbeddedCollection.class,
        type);
    ResolvedType mapOfLinks = resolver.resolve(
        Map.class,
        String.class,
        Link.class);
    return Stream.of(
        new ModelPropertyBuilder()
            .name("_embedded")
            .type(embedded)
            .qualifiedType(embedded.getSignature())
            .position(0)
            .required(true)
            .isHidden(false)
            .build()
            .updateModelRef(modelRefFactory(
                context,
                enumTypeDeterminer,
                typeNameExtractor)),
        new ModelPropertyBuilder()
            .name("_links")
            .type(mapOfLinks)
            .qualifiedType(mapOfLinks.getSignature())
            .position(1)
            .required(true)
            .isHidden(false)
            .description("Link collection")
            .build()
            .updateModelRef(modelRefFactory(
                context,
                enumTypeDeterminer,
                typeNameExtractor))
                    )
        .collect(toList());
  }

  @Override
  public ModelSpecification createModelSpecification(ModelContext context) {
    ResolvedType resourceType = resourceType(context.getType());
    List<ResolvedType> typeParameters = resourceType.getTypeParameters();
    Class<?> type = typeParameters.get(0).getErasedType();
    String name = typeNameExtractor.typeName(context);
    return context.getModelSpecificationBuilder()
        .name(name)
        .facetsBuilder()
        .description(String.format(
            "Resources of %s",
            type.getSimpleName()))
        .xml(new Xml()
                 .name("entities")
                 .wrapped(false)
                 .attribute(false))
        .yield()
        .compoundModelBuilder()
        .properties(propertySpecifications(context))
        .modelKey(new ModelKeyBuilder()
                      .isResponse(context.isReturnType())
                      .qualifiedModelName(
                          new QualifiedModelName(
                              "org.springframework.hateoas",
                              name))
                      .build())
        .yield()
        .build();
  }

  @Override
  public List<PropertySpecification> propertySpecifications(ModelContext context) {
    ResolvedType resourceType = resourceType(context.getType());
    List<ResolvedType> typeParameters = resourceType.getTypeParameters();
    Class<?> type = typeParameters.get(0).getErasedType();
    ResolvedType embedded = resolver.resolve(
        EmbeddedCollection.class,
        type);
    ResolvedType mapOfLinks = resolver.resolve(
        Map.class,
        String.class,
        Link.class);
    ModelSpecification embeddedProperty = modelSpecifications.create(context, embedded);
    ModelSpecification mapOfLinksProperty = modelSpecifications.create(context, mapOfLinks);
    return Stream.of(
        new PropertySpecificationBuilder("_embedded")
            .type(embeddedProperty)
            .position(0)
            .required(true)
            .isHidden(false)
            .build(),
        new PropertySpecificationBuilder("_links")
            .type(mapOfLinksProperty)
            .position(1)
            .required(true)
            .isHidden(false)
            .description("Link collection")
            .build())
        .collect(toList());
  }

  @Override
  public Set<ResolvedType> dependencies(ModelContext context) {
    ResolvedType resourceType = resourceType(context.getType());
    List<ResolvedType> typeParameters = resourceType.getTypeParameters();
    Class<?> type = typeParameters.get(0).getErasedType();

    return Stream.of(
        resolver.resolve(
            EmbeddedCollection.class,
            type),
        resolver.resolve(Link.class)
                    ).collect(toSet());
  }

  @Override
  public boolean supports(ModelContext delimiter) {
    return CollectionModel.class.equals(resourceType(delimiter.getType()).getErasedType())
        && (delimiter.getDocumentationType() == DocumentationType.SWAGGER_2
        || delimiter.getDocumentationType() == DocumentationType.OAS_30
        || delimiter.getDocumentationType() == DocumentationType.SPRING_WEB);
  }

  private ResolvedType resourceType(Type type) {
    return resolver.resolve(type);
  }
}
