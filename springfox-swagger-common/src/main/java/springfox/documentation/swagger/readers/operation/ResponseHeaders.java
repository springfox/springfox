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
package springfox.documentation.swagger.readers.operation;

import static springfox.documentation.schema.Types.typeNameFor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ResponseHeader;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.schema.ModelReference;
import springfox.documentation.service.Header;
import springfox.documentation.util.Strings;

public class ResponseHeaders {
  private ResponseHeaders() {
    throw new UnsupportedOperationException();
  }

  public static Function<ApiOperation, ResponseHeader[]> responseHeaders() {
    return new Function<ApiOperation, ResponseHeader[]>() {
      @Override
      public ResponseHeader[] apply(ApiOperation input) {
        return input.responseHeaders();
      }
    };
  }

  public static Map<String, Header> headers(ResponseHeader[] responseHeaders) {
    Map<String, Header> headers = new HashMap<>();
    List<ResponseHeader> filtered = Arrays.stream(responseHeaders).filter(emptyOrVoid().negate()).collect(Collectors.toList());
    for (ResponseHeader each : filtered) {
      headers.put(each.name(), new Header(each.name(), each.description(), headerModel(each)));
    }
    return headers;
  }

  private static Predicate<ResponseHeader> emptyOrVoid() {
    return new Predicate<ResponseHeader>() {
      @Override
      public boolean test(ResponseHeader input) {
        return Strings.isNullOrEmpty(input.name()) || Void.class.equals(input.response());
      }
    };
  }

  private static ModelReference headerModel(ResponseHeader each) {
    ModelReference modelReference;
    String typeName = Optional.ofNullable(typeNameFor(each.response())).orElse("string");
    if (Strings.isNullOrEmpty(each.responseContainer())) {
      modelReference = new ModelRef(typeName);
    } else {
      modelReference = new ModelRef(each.responseContainer(), new ModelRef(typeName));
    }
    return modelReference;
  }
}
