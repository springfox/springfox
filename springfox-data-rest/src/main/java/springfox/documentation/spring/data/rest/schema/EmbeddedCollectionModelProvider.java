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
import org.springframework.hateoas.RelProvider;
import org.springframework.hateoas.Resources;
import springfox.documentation.builders.ModelPropertyBuilder;
import springfox.documentation.schema.Model;
import springfox.documentation.schema.ModelProperty;
import springfox.documentation.schema.TypeNameExtractor;
import springfox.documentation.schema.Xml;
import springfox.documentation.spi.DocumentationType;
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
  private final RelProvider relProvider;
  private final TypeNameExtractor typeNameExtractor;

  EmbeddedCollectionModelProvider(
      TypeResolver resolver,
      RelProvider relProvider,
      TypeNameExtractor typeNameExtractor) {
    this.resolver = resolver;
    this.relProvider = relProvider;
    this.typeNameExtractor = typeNameExtractor;
  }

  @Override
  public Model create(ModelContext context) {
    ResolvedType resourceType = resolver.resolve(context.getType());
    List<ResolvedType> typeParameters = resourceType.getTypeParameters();
    Class<?> type = typeParameters.get(0).getErasedType();
    String name = typeNameExtractor.typeName(context);
    return context.getBuilder()
        .description(String.format(
            "Embedded collection of %s",
            type.getSimpleName()))
        .name(name)
        .id(name)
        .qualifiedType(type.getName())
        .type(typeParameters.get(0))
        .properties(properties(context).stream().collect(toMap(ModelProperty::getName, identity())))
        .xml(new Xml()
            .wrapped(true)
            .name("content")
        )
        .build();
  }

  @Override
  public List<ModelProperty> properties(ModelContext context) {
    ResolvedType resourceType = resolver.resolve(context.getType());
    List<ResolvedType> typeParameters = resourceType.getTypeParameters();
    Class<?> type = typeParameters.get(0).getErasedType();
    return singletonList(
        new ModelPropertyBuilder()
            .name(relProvider.getCollectionResourceRelFor(type))
            .type(resolver.resolve(List.class, type))
            .qualifiedType(Resources.class.getName())
            .position(0)
            .required(true)
            .isHidden(false)
            .description("Resource collection")
            .build()
            .updateModelRef(modelRefFactory(context, typeNameExtractor)));
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
        && delimiter.getDocumentationType() == DocumentationType.SWAGGER_2;
  }

}
