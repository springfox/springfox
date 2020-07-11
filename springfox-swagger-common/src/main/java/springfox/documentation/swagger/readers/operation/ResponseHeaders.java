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
package springfox.documentation.swagger.readers.operation;

import io.swagger.annotations.ResponseHeader;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.util.StringUtils;
import springfox.documentation.builders.ModelSpecificationBuilder;
import springfox.documentation.schema.CollectionType;
import springfox.documentation.schema.ModelSpecification;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.schema.ScalarTypes;
import springfox.documentation.schema.property.PackageNames;
import springfox.documentation.service.Header;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Optional.*;
import static org.springframework.util.StringUtils.*;

public class ResponseHeaders {
  private ResponseHeaders() {
    throw new UnsupportedOperationException();
  }

  public static Map<String, Header> headers(io.swagger.v3.oas.annotations.headers.Header[] responseHeaders) {
    Map<String, Header> headers = new HashMap<>();
    Stream.of(responseHeaders)
        .filter(emptyOrVoidHeader().negate()).forEachOrdered(each ->
        headers.put(each.name(), new Header(
            each.name(),
            each.description(),
            null,
            headerModelSpecification(each),
            each.required())));
    return headers;
  }

  public static Map<String, Header> headers(ResponseHeader[] responseHeaders) {
    return headers(Arrays.asList(responseHeaders));
  }

  public static Map<String, Header> headers(Collection<ResponseHeader> responseHeaders) {
    Map<String, Header> headers = new HashMap<>();
    responseHeaders.stream()
        .filter(emptyOrVoid().negate())
        .forEachOrdered(each -> headers.put(each.name(), new Header(
            each.name(),
            each.description(),
            headerModel(each),
            headerSpecification(each))));
    return headers;
  }

  private static ModelSpecification headerSpecification(ResponseHeader each) {
    ModelSpecification itemSpecification;
    if (each.response() == Void.class || each.response() == Void.TYPE) {
      itemSpecification = new ModelSpecificationBuilder()
          .scalarModel(ScalarType.STRING)
          .build();
    } else if (ScalarTypes.builtInScalarType(each.response()).isPresent()) {
      itemSpecification = new ModelSpecificationBuilder()
          .scalarModel(ScalarTypes.builtInScalarType(each.response()).get())
          .build();
    } else {
      itemSpecification = new ModelSpecificationBuilder()
          .referenceModel(r -> r.key(k ->
              k.qualifiedModelName(q -> q.namespace(PackageNames.safeGetPackageName(each.response()))
                  .name(each.response().getSimpleName()))))
          .build();
    }
    if (collectionTypeFromEnum(each.responseContainer()) == null) {
      new ModelSpecificationBuilder()
          .collectionModel(c ->
              c.model(m ->
                  m.copyOf(itemSpecification))
                  .collectionType(collectionTypeFromEnum(each.responseContainer())))
          .build();
    }
    return itemSpecification;
  }

  private static CollectionType collectionTypeFromEnum(String type) {
    if (Arrays.asList("LIST", "SET").contains(type.toUpperCase())) {
      return CollectionType.valueOf(type.toUpperCase());
    }
    return null;
  }

  private static Predicate<io.swagger.v3.oas.annotations.headers.Header> emptyOrVoidHeader() {
    return input -> isEmpty(input.name()) ||
        (isVoidImplementation(input) && StringUtils.isEmpty(input.schema().type()));
  }

  private static boolean isVoidImplementation(io.swagger.v3.oas.annotations.headers.Header input) {
    return !Void.class.equals(input.schema().implementation()) && Void.TYPE.equals(input.schema().implementation());
  }

  private static Predicate<ResponseHeader> emptyOrVoid() {
    return input -> isEmpty(input.name()) || Void.class.equals(input.response());
  }

  @SuppressWarnings("deprecation")
  private static springfox.documentation.schema.ModelReference headerModel(ResponseHeader each) {
    springfox.documentation.schema.ModelReference modelReference;
    String typeName = ofNullable(springfox.documentation.schema.Types.typeNameFor(each.response()))
        .orElse("string");
    if (isEmpty(each.responseContainer())) {
      modelReference = new springfox.documentation.schema.ModelRef(typeName);
    } else {
      modelReference = new springfox.documentation.schema.ModelRef(each.responseContainer(),
          new springfox.documentation.schema.ModelRef(typeName));
    }
    return modelReference;
  }

  private static ModelSpecification headerModelSpecification(io.swagger.v3.oas.annotations.headers.Header each) {
    ModelSpecification itemSpecification;
    Class<?> type = each.schema().implementation();
    if (emptyOrVoidHeader().test(each)) {
      itemSpecification = new ModelSpecificationBuilder()
          .scalarModel(ScalarType.STRING)
          .facets(f -> f.deprecated(each.deprecated()))
          .build();
    } else if (scalarType(each.schema()).isPresent()) {
      itemSpecification = new ModelSpecificationBuilder()
          .scalarModel(scalarType(each.schema()).get())
          .facets(f -> f.deprecated(each.deprecated()))
          .build();
    } else if (ScalarTypes.builtInScalarType(type).isPresent()) {
      itemSpecification = new ModelSpecificationBuilder()
          .scalarModel(ScalarTypes.builtInScalarType(type).get())
          .facets(f -> f.deprecated(each.deprecated())).build();
    } else {
      itemSpecification = new ModelSpecificationBuilder()
          .referenceModel(r ->
              r.key(k ->
                  k.qualifiedModelName(q ->
                      q.namespace(PackageNames.safeGetPackageName(type))
                          .name(type.getSimpleName()))))
          .facets(f -> f.deprecated(each.deprecated()))
          .build();
    }
    if (each.schema().multipleOf() > 0) {
      return new ModelSpecificationBuilder()
          .collectionModel(c -> c.model(m -> m.copyOf(itemSpecification))
              .collectionType(CollectionType.ARRAY))
          .facets(f -> f.deprecated(each.deprecated()))
          .build();
    }
    return itemSpecification;
  }

  private static Optional<ScalarType> scalarType(Schema schema) {
    if (StringUtils.isEmpty(schema.type())) {
      return Optional.empty();
    }
    return ScalarType.from(schema.type(), schema.format());
  }
}
