/*
 *
 *  Copyright 2015-2017 the original author or authors.
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

package springfox.documentation.swagger2.mappers;

import com.fasterxml.classmate.ResolvedType;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.BooleanProperty;
import io.swagger.models.properties.ByteArrayProperty;
import io.swagger.models.properties.DateProperty;
import io.swagger.models.properties.DateTimeProperty;
import io.swagger.models.properties.DecimalProperty;
import io.swagger.models.properties.DoubleProperty;
import io.swagger.models.properties.FileProperty;
import io.swagger.models.properties.FloatProperty;
import io.swagger.models.properties.IntegerProperty;
import io.swagger.models.properties.LongProperty;
import io.swagger.models.properties.MapProperty;
import io.swagger.models.properties.ObjectProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;
import io.swagger.models.properties.StringProperty;
import io.swagger.models.properties.UUIDProperty;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Optional.*;
import static springfox.documentation.schema.Collections.*;
import static springfox.documentation.schema.ResolvedTypes.*;
import static springfox.documentation.swagger2.mappers.EnumMapper.*;

/**
 * Use {@link ModelSpecificationMapper} instead
 *
 * @deprecated @since 3.0.0
 */
@Deprecated
class Properties {
  private static final Map<String, Function<String, Property>> TYPE_FACTORY = new HashMap<>();

  static {
    TYPE_FACTORY.put("int", newInstanceOf(IntegerProperty.class));
    TYPE_FACTORY.put("long", newInstanceOf(LongProperty.class));
    TYPE_FACTORY.put("float", newInstanceOf(FloatProperty.class));
    TYPE_FACTORY.put("double", newInstanceOf(DoubleProperty.class));
    TYPE_FACTORY.put("string", newInstanceOf(StringProperty.class));
    TYPE_FACTORY.put("boolean", newInstanceOf(BooleanProperty.class));
    TYPE_FACTORY.put("date", newInstanceOf(DateProperty.class));
    TYPE_FACTORY.put("date-time", newInstanceOf(DateTimeProperty.class));
    TYPE_FACTORY.put("bigdecimal", newInstanceOf(DecimalProperty.class));
    TYPE_FACTORY.put("biginteger", newInstanceOf(LongProperty.class));
    TYPE_FACTORY.put("uuid", newInstanceOf(UUIDProperty.class));
    TYPE_FACTORY.put("object", newInstanceOf(ObjectProperty.class));
    TYPE_FACTORY.put("byte", bytePropertyFactory());
    TYPE_FACTORY.put("__file", filePropertyFactory());
  }

  private Properties() {
    throw new UnsupportedOperationException();
  }

  public static Property property(String typeName) {
    String safeTypeName = ofNullable(typeName).orElse("");
    return TYPE_FACTORY.getOrDefault(safeTypeName.toLowerCase(), voidOrRef(safeTypeName)).apply(safeTypeName);
  }

  public static Property property(springfox.documentation.schema.ModelReference modelRef) {
    if (modelRef.isMap()) {
      return new MapProperty(property(modelRef.itemModel()
          .orElseThrow(() -> new IllegalStateException("ModelRef that is a map should have an itemModel"))));
    } else if (modelRef.isCollection()) {
      if ("byte".equals(modelRef.itemModel()
          .map(springfox.documentation.schema.ModelReference::getType).orElse(""))) {
        return new ByteArrayProperty();
      }
      return new ArrayProperty(
          maybeAddAllowableValues(itemTypeProperty(modelRef.itemModel()
                  .orElseThrow(() ->
                      new IllegalStateException("ModelRef that is a collection should have an itemModel"))),
              modelRef.getAllowableValues()));
    }
    return property(modelRef.getType());
  }

  public static Property itemTypeProperty(springfox.documentation.schema.ModelReference paramModel) {
    if (paramModel.isCollection()) {
      return new ArrayProperty(
          maybeAddAllowableValues(itemTypeProperty(paramModel.itemModel()
                  .orElseThrow(() ->
                      new IllegalStateException("ModelRef that is a collection should have an itemModel"))),
              paramModel.getAllowableValues()));
    }
    return property(paramModel.getType());
  }

  private static Function<String, Property> newInstanceOf(final Class<? extends Property> clazz) {
    return input -> {
      try {
        return clazz.getDeclaredConstructor().newInstance();
      } catch (Exception e) {
        //This is bad! should never come here
        throw new IllegalStateException(e);
      }
    };
  }

  static Comparator<String> defaultOrdering(Map<String, springfox.documentation.schema.ModelProperty> properties) {
    return byPosition(properties).thenComparing(byName());
  }

  private static Function<String, Property> voidOrRef(final String typeName) {
    return input -> {
      if (typeName.equalsIgnoreCase("void")) {
        return null;
      }
      return new RefProperty(typeName);
    };
  }

  private static Function<String, Property> bytePropertyFactory() {
    return input -> {
      final IntegerProperty integerProperty = new IntegerProperty();
      integerProperty.setFormat("int32");
      integerProperty.setMaximum(BigDecimal.valueOf(Byte.MAX_VALUE));
      integerProperty.setMinimum(BigDecimal.valueOf(Byte.MIN_VALUE));
      return integerProperty;
    };
  }

  private static Function<String, Property> filePropertyFactory() {
    return input -> new FileProperty();
  }

  private static Comparator<String> byName() {
    return String::compareTo;
  }

  private static Comparator<String> byPosition(
      Map<String, springfox.documentation.schema.ModelProperty> modelProperties) {
    return (first, second) -> {
      springfox.documentation.schema.ModelProperty p1 = modelProperties.get(first);
      springfox.documentation.schema.ModelProperty p2 = modelProperties.get(second);
      return Integer.compare(p1.getPosition(), p2.getPosition());
    };
  }

  static Predicate<Map.Entry<String, springfox.documentation.schema.ModelProperty>> voidProperties() {
    return input -> isVoid(input.getValue().getType())
        || collectionOfVoid(input.getValue().getType())
        || isVoid(input.getValue().getType().getArrayElementType());
  }

  private static boolean collectionOfVoid(ResolvedType type) {
    return isContainerType(type) && isVoid(collectionElementType(type));
  }

}
