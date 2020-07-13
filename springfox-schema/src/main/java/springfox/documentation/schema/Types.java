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

package springfox.documentation.schema;

import com.fasterxml.classmate.ResolvedType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.AbstractMap;
import java.util.Currency;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.Collections.*;
import static java.util.stream.Collectors.*;

/**
 * @since 3.0.0
 * @deprecated use @see {@link ScalarType} instead
 */
@Deprecated
public class Types {
  private static final Set<String> BASE_TYPES = Stream.of(
      "int",
      "date",
      "string",
      "double",
      "float",
      "boolean",
      "byte",
      "object",
      "long",
      "date-time",
      "__file",
      "biginteger",
      "bigdecimal",
      "uuid").collect(toSet());

  private static final Map<Type, String> TYPE_NAME_LOOKUP = unmodifiableMap(
      Stream.of(
          new AbstractMap.SimpleEntry<>(Long.TYPE, "long"),
          new AbstractMap.SimpleEntry<>(Short.TYPE, "int"),
          new AbstractMap.SimpleEntry<>(Integer.TYPE, "int"),
          new AbstractMap.SimpleEntry<>(Double.TYPE, "double"),
          new AbstractMap.SimpleEntry<>(Float.TYPE, "float"),
          new AbstractMap.SimpleEntry<>(Byte.TYPE, "byte"),
          new AbstractMap.SimpleEntry<>(Boolean.TYPE, "boolean"),
          new AbstractMap.SimpleEntry<>(Character.TYPE, "string"),
          new AbstractMap.SimpleEntry<>(Date.class, "date-time"),
          new AbstractMap.SimpleEntry<>(java.sql.Date.class, "date"),
          new AbstractMap.SimpleEntry<>(String.class, "string"),
          new AbstractMap.SimpleEntry<>(Object.class, "object"),
          new AbstractMap.SimpleEntry<>(Long.class, "long"),
          new AbstractMap.SimpleEntry<>(Integer.class, "int"),
          new AbstractMap.SimpleEntry<>(Short.class, "int"),
          new AbstractMap.SimpleEntry<>(Double.class, "double"),
          new AbstractMap.SimpleEntry<>(Float.class, "float"),
          new AbstractMap.SimpleEntry<>(Boolean.class, "boolean"),
          new AbstractMap.SimpleEntry<>(Byte.class, "byte"),
          new AbstractMap.SimpleEntry<>(BigDecimal.class, "bigdecimal"),
          new AbstractMap.SimpleEntry<>(BigInteger.class, "biginteger"),
          new AbstractMap.SimpleEntry<>(Currency.class, "string"),
          new AbstractMap.SimpleEntry<>(UUID.class, "uuid"),
          new AbstractMap.SimpleEntry<>(MultipartFile.class, "__file"),
          new AbstractMap.SimpleEntry<>(FilePart.class, "__file"))
          .collect(toMap(
              Map.Entry::getKey,
              Map.Entry::getValue)));

  private Types() {
    throw new UnsupportedOperationException();
  }

  public static String typeNameFor(Type type) {
    return TYPE_NAME_LOOKUP.get(type);
  }

  public static boolean isBaseType(String typeName) {
    return BASE_TYPES.contains(typeName);
  }

  public static boolean isBaseType(ResolvedType type) {
    return BASE_TYPES.contains(typeNameFor(type.getErasedType()));
  }

}
