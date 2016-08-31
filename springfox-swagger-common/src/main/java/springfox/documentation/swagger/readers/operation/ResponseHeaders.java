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

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ResponseHeader;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.schema.ModelReference;
import springfox.documentation.service.Header;

import java.util.Map;

import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Predicates.not;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static springfox.documentation.schema.Types.typeNameFor;

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
    Map<String, Header> headers = newHashMap();
    for (ResponseHeader each : from(newArrayList(responseHeaders)).filter(not(emptyOrVoid()))) {
      headers.put(each.name(), new Header(each.name(), each.description(), headerModel(each)));
    }
    return headers;
  }

  private static Predicate<ResponseHeader> emptyOrVoid() {
    return new Predicate<ResponseHeader>() {
      @Override
      public boolean apply(ResponseHeader input) {
        return Strings.isNullOrEmpty(input.name()) || Void.class.equals(input.response());
      }
    };
  }

  private static ModelReference headerModel(ResponseHeader each) {
    ModelReference modelReference;
    String typeName = fromNullable(typeNameFor(each.response())).or("string");
    if (isNullOrEmpty(each.responseContainer())) {
      modelReference = new ModelRef(typeName);
    } else {
      modelReference = new ModelRef(each.responseContainer(), new ModelRef(typeName));
    }
    return modelReference;
  }
}
