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
import springfox.documentation.schema.CollectionSpecification;
import springfox.documentation.schema.CollectionType;
import springfox.documentation.schema.ModelKeyBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.schema.ModelReference;
import springfox.documentation.schema.ModelSpecification;
import springfox.documentation.schema.QualifiedModelName;
import springfox.documentation.schema.ReferenceModelSpecification;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.schema.ScalarTypes;
import springfox.documentation.schema.property.PackageNames;
import springfox.documentation.service.Header;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Optional.*;
import static org.springframework.util.StringUtils.*;
import static springfox.documentation.schema.Types.*;

public class ResponseHeaders {
  private ResponseHeaders() {
    throw new UnsupportedOperationException();
  }


  public static Map<String, Header> headers(io.swagger.v3.oas.annotations.headers.Header[] responseHeaders) {
    Map<String, Header> headers = new HashMap<>();
    Stream.of(responseHeaders)
          .filter(emptyOrVoidHeader().negate()).forEachOrdered(each -> {
      headers.put(each.name(), new Header(
          each.name(),
          each.description(),
          null,
          headerModelSpecification(each),
          each.required()));
    });
    return headers;
  }

  public static Map<String, Header> headers(ResponseHeader[] responseHeaders) {
    Map<String, Header> headers = new HashMap<>();
    Stream.of(responseHeaders).filter(emptyOrVoid().negate()).forEachOrdered(each -> {
      headers.put(each.name(), new Header(
          each.name(),
          each.description(),
          headerModel(each),
          headerSpecification(each)));
    });
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
          .referenceModel(new ReferenceModelSpecification(
              new ModelKeyBuilder()
                  .qualifiedModelName(
                      new QualifiedModelName(
                          PackageNames.safeGetPackageName(each.response()),
                          each.response().getSimpleName()))
                  .build()))
          .build();
    }
    if (collectionTypeFromEnum(each.responseContainer()) == null) {
      new ModelSpecificationBuilder()
          .collectionModel(new CollectionSpecification(
              itemSpecification,
              collectionTypeFromEnum(each.responseContainer())))
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

  private static ModelReference headerModel(ResponseHeader each) {
    ModelReference modelReference;
    String typeName = ofNullable(typeNameFor(each.response())).orElse("string");
    if (isEmpty(each.responseContainer())) {
      modelReference = new ModelRef(typeName);
    } else {
      modelReference = new ModelRef(each.responseContainer(), new ModelRef(typeName));
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
          .referenceModel(new ReferenceModelSpecification(
              new ModelKeyBuilder()
                  .qualifiedModelName(
                      new QualifiedModelName(
                          PackageNames.safeGetPackageName(type),
                          type.getSimpleName()))
                  .build()))
          .facets(f -> f.deprecated(each.deprecated()))
          .build();
    }
    if (each.schema().multipleOf() >  0) {
      return new ModelSpecificationBuilder()
          .collectionModel(new CollectionSpecification(
              itemSpecification,
              CollectionType.ARRAY))
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
