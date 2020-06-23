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
import springfox.documentation.schema.ModelProperty;
import springfox.documentation.schema.ModelReference;

import java.math.BigDecimal;
import java.util.AbstractMap;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Collections.*;
import static java.util.Optional.*;
import static java.util.stream.Collectors.*;
import static springfox.documentation.schema.Collections.*;
import static springfox.documentation.schema.Types.*;
import static springfox.documentation.swagger2.mappers.EnumMapper.*;

class Properties {
  private static final Map<String, Function<String, ? extends Property>> TYPE_FACTORY
      = unmodifiableMap(Stream.of(
      new AbstractMap.SimpleEntry<>("int", newInstanceOf(IntegerProperty.class)),
      new AbstractMap.SimpleEntry<>("long", newInstanceOf(LongProperty.class)),
      new AbstractMap.SimpleEntry<>("float", newInstanceOf(FloatProperty.class)),
      new AbstractMap.SimpleEntry<>("double", newInstanceOf(DoubleProperty.class)),
      new AbstractMap.SimpleEntry<>("string", newInstanceOf(StringProperty.class)),
      new AbstractMap.SimpleEntry<>("boolean", newInstanceOf(BooleanProperty.class)),
      new AbstractMap.SimpleEntry<>("date", newInstanceOf(DateProperty.class)),
      new AbstractMap.SimpleEntry<>("date-time", newInstanceOf(DateTimeProperty.class)),
      new AbstractMap.SimpleEntry<>("bigdecimal", newInstanceOf(DecimalProperty.class)),
      new AbstractMap.SimpleEntry<>("biginteger", newInstanceOf(LongProperty.class)),
      new AbstractMap.SimpleEntry<>("uuid", newInstanceOf(UUIDProperty.class)),
      new AbstractMap.SimpleEntry<>("object", newInstanceOf(ObjectProperty.class)),
      new AbstractMap.SimpleEntry<>("byte", bytePropertyFactory()),
      new AbstractMap.SimpleEntry<>("__file", filePropertyFactory()))
                            .collect(toMap(Map.Entry::getKey, Map.Entry::getValue)));

  private Properties() {
    throw new UnsupportedOperationException();
  }

  public static Property property(String typeName) {
    String safeTypeName = ofNullable(typeName).orElse("");
    return TYPE_FACTORY.getOrDefault(safeTypeName.toLowerCase(), voidOrRef(safeTypeName)).apply(safeTypeName);
  }

  public static Property property(ModelReference modelRef) {
    if (modelRef.isMap()) {
      return new MapProperty(property(modelRef.itemModel().get()));
    } else if (modelRef.isCollection()) {
      if ("byte".equals(modelRef.itemModel().map((Function<? super ModelReference, String>) (Function<ModelReference,
          String>) ModelReference::getType).orElse(""))) {
        return new ByteArrayProperty();
      }
      return new ArrayProperty(
          maybeAddAllowableValues(itemTypeProperty(modelRef.itemModel().get()), modelRef.getAllowableValues()));
    }
    return property(modelRef.getType());
  }

  public static Property itemTypeProperty(ModelReference paramModel) {
    if (paramModel.isCollection()) {
      return new ArrayProperty(
          maybeAddAllowableValues(itemTypeProperty(paramModel.itemModel().get()), paramModel.getAllowableValues()));
    }
    return property(paramModel.getType());
  }

  private static <T extends Property> Function<String, T> newInstanceOf(final Class<T> clazz) {
    return input -> {
      try {
        return clazz.newInstance();
      } catch (Exception e) {
        //This is bad! should never come here
        throw new IllegalStateException(e);
      }
    };
  }

  static Comparator<String> defaultOrdering(Map<String, ModelProperty> properties) {
    return byPosition(properties).thenComparing(byName());
  }

  private static Function<String, ? extends Property> voidOrRef(final String typeName) {
    return (Function<String, Property>) input -> {
      if (typeName.equalsIgnoreCase("void")) {
        return null;
      }
      return new RefProperty(typeName);
    };
  }

  private static Function<String, ? extends Property> bytePropertyFactory() {
    return (Function<String, Property>) input -> {
      final IntegerProperty integerProperty = new IntegerProperty();
      integerProperty.setFormat("int32");
      integerProperty.setMaximum(BigDecimal.valueOf(Byte.MAX_VALUE));
      integerProperty.setMinimum(BigDecimal.valueOf(Byte.MIN_VALUE));
      return integerProperty;
    };
  }

  private static Function<String, ? extends Property> filePropertyFactory() {
    return (Function<String, Property>) input -> new FileProperty();
  }

  private static Comparator<String> byName() {
    return String::compareTo;
  }

  private static Comparator<String> byPosition(final Map<String, ModelProperty> modelProperties) {
    return (first, second) -> {
      ModelProperty p1 = modelProperties.get(first);
      ModelProperty p2 = modelProperties.get(second);
      return Integer.compare(p1.getPosition(), p2.getPosition());
    };
  }

  static Predicate<Map.Entry<String, ModelProperty>> voidProperties() {
    return input -> isVoid(input.getValue().getType())
        || collectionOfVoid(input.getValue().getType())
        || arrayTypeOfVoid(input.getValue().getType().getArrayElementType());
  }

  private static boolean arrayTypeOfVoid(ResolvedType arrayElementType) {
    return arrayElementType != null && isVoid(arrayElementType);
  }

  private static boolean collectionOfVoid(ResolvedType type) {
    return isContainerType(type) && isVoid(collectionElementType(type));
  }
}
