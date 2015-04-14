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

package springfox.documentation.swagger2.mappers;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.wordnik.swagger.models.properties.BooleanProperty;
import com.wordnik.swagger.models.properties.DateProperty;
import com.wordnik.swagger.models.properties.DateTimeProperty;
import com.wordnik.swagger.models.properties.DecimalProperty;
import com.wordnik.swagger.models.properties.DoubleProperty;
import com.wordnik.swagger.models.properties.FloatProperty;
import com.wordnik.swagger.models.properties.IntegerProperty;
import com.wordnik.swagger.models.properties.LongProperty;
import com.wordnik.swagger.models.properties.ObjectProperty;
import com.wordnik.swagger.models.properties.Property;
import com.wordnik.swagger.models.properties.RefProperty;
import com.wordnik.swagger.models.properties.StringProperty;
import com.wordnik.swagger.models.properties.UUIDProperty;

import java.util.Map;

import static com.google.common.base.Functions.*;
import static com.google.common.base.Strings.*;

class Properties {
  private static final Map<String, Function<String, ? extends Property>> typeFactory
      = ImmutableMap.<String, Function<String, ? extends Property>>builder()
          .put("int", newInstanceOf(IntegerProperty.class))
          .put("long", newInstanceOf(LongProperty.class))
          .put("float", newInstanceOf(FloatProperty.class))
          .put("double", newInstanceOf(DoubleProperty.class))
          .put("string", newInstanceOf(StringProperty.class))
          .put("boolean", newInstanceOf(BooleanProperty.class))
          .put("date", newInstanceOf(DateProperty.class))
          .put("date-time", newInstanceOf(DateTimeProperty.class))
          .put("bigdecimal", newInstanceOf(DecimalProperty.class))
          .put("biginteger", newInstanceOf(DecimalProperty.class))
          .put("uuid", newInstanceOf(UUIDProperty.class))
          .put("object", newInstanceOf(ObjectProperty.class))
          .put("byte", bytePropertyFactory())
        .build();

  private Properties() {
    throw new UnsupportedOperationException();
  }

  public static Property property(final String typeName) {
    String safeTypeName = nullToEmpty(typeName);
    Function<String, Function<String, ? extends Property>> propertyLookup
        = forMap(typeFactory, voidOrRef(safeTypeName));
    return propertyLookup.apply(safeTypeName.toLowerCase()).apply(safeTypeName);
  }

  private static <T extends Property> Function<String, T> newInstanceOf(final Class<T> clazz) {
    return new Function<String, T>() {
      @Override
      public T apply(String input) {
        try {
          return clazz.newInstance();
        } catch (Exception e) {
          //This is bad! should never come here
          throw new IllegalStateException(e);
        }
      }
    };
  }

  private static Function<String, ? extends Property> voidOrRef(final String typeName) {
    return new Function<String, Property>() {
      @Override
      public Property apply(String input) {
        if (typeName.equalsIgnoreCase("void")) {
          return null;
        }
        return new RefProperty(typeName);
      }
    };
  }

  private static Function<String, ? extends Property> bytePropertyFactory() {
    return new Function<String, Property>() {
      @Override
      public Property apply(String input) {
        StringProperty byteArray = new StringProperty();
        byteArray.setFormat("byte");
        return byteArray;
      }
    };
  }
}
