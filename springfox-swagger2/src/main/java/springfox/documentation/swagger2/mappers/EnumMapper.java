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

package springfox.documentation.swagger2.mappers;

import io.swagger.models.ModelImpl;
import io.swagger.models.parameters.SerializableParameter;
import io.swagger.models.properties.AbstractNumericProperty;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.DoubleProperty;
import io.swagger.models.properties.FloatProperty;
import io.swagger.models.properties.IntegerProperty;
import io.swagger.models.properties.LongProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.StringProperty;
import springfox.documentation.schema.CollectionElementFacet;
import springfox.documentation.schema.ElementFacetSource;
import springfox.documentation.schema.EnumerationFacet;
import springfox.documentation.schema.NumericElementFacet;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.schema.StringElementFacet;
import springfox.documentation.service.AllowableListValues;
import springfox.documentation.service.AllowableRangeValues;
import springfox.documentation.service.AllowableValues;
import springfox.documentation.service.SimpleParameterSpecification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Optional.*;
import static java.util.stream.Collectors.*;

public class EnumMapper {
  private EnumMapper() {
    throw new UnsupportedOperationException();
  }

  static ModelImpl maybeAddAllowableValuesToParameter(
      ModelImpl toReturn,
      AllowableValues allowableValues) {
    if (allowableValues instanceof AllowableListValues) {
      toReturn.setEnum(((AllowableListValues) allowableValues).getValues());
    }
    return toReturn;
  }

  static SerializableParameter maybeAddAllowableValuesToParameter(
      SerializableParameter toReturn,
      Property property,
      AllowableValues allowableValues) {

    if (allowableValues instanceof AllowableListValues) {
      toReturn.setEnum(((AllowableListValues) allowableValues).getValues());
    }
    if (allowableValues instanceof AllowableRangeValues) {
      AllowableRangeValues range = (AllowableRangeValues) allowableValues;
      if (property instanceof StringProperty) {
        toReturn.setMinLength(safeInteger(range.getMin()));
        toReturn.setMaxLength(safeInteger(range.getMax()));
      } else {
        toReturn.setMinimum(safeBigDecimal(range.getMin()));
        toReturn.setExclusiveMinimum(range.getExclusiveMin());
        toReturn.setMaximum(safeBigDecimal(range.getMax()));
        toReturn.setExclusiveMaximum(range.getExclusiveMax());
      }
    }
    return toReturn;
  }

  static SerializableParameter maybeAddAllowableValuesToParameter(
      SerializableParameter parameter,
      SimpleParameterSpecification parameterSpecification) {

    parameterSpecification.facetOfType(EnumerationFacet.class)
        .ifPresent(e -> parameter.setEnum(e.getAllowedValues()));
    parameterSpecification.facetOfType(NumericElementFacet.class)
        .ifPresent(range -> parameterSpecification.getModel().getScalar()
            .ifPresent(s -> {
              if (s.getType() == ScalarType.STRING) {
                parameter.setMinLength(safeConvertBigDecimalToInt(range.getMinimum()));
                parameter.setMaxLength(safeConvertBigDecimalToInt(range.getMaximum()));
              } else {
                parameter.setMinimum(range.getMinimum());
                parameter.setExclusiveMinimum(range.getExclusiveMinimum());
                parameter.setMaximum(range.getMaximum());
                parameter.setExclusiveMaximum(range.getExclusiveMaximum());
              }
            }));
    parameterSpecification.facetOfType(StringElementFacet.class)
        .ifPresent(string -> parameterSpecification.getModel()
            .getScalar()
            .ifPresent(s -> {
              if (s.getType() == ScalarType.STRING) {
                parameter.setMinLength(string.getMinLength());
                parameter.setMaxLength(string.getMaxLength());
                parameter.setPattern(string.getPattern());
              }
            }));
    parameterSpecification.getModel().getCollection()
        .flatMap(collection -> parameterSpecification
            .facetOfType(CollectionElementFacet.class))
        .ifPresent(element -> {
          parameter.setMaxItems(element.getMaxItems());
          parameter.setMinItems(element.getMinItems());
          parameter.setUniqueItems(element.getUniqueItems());
        });
    return parameter;
  }

  private static Integer safeConvertBigDecimalToInt(BigDecimal bigDecimalValue) {
    return bigDecimalValue != null ? bigDecimalValue.intValue() : null;
  }

  static BigDecimal safeBigDecimal(String doubleString) {
    if (doubleString == null) {
      return null;
    }
    try {
      return new BigDecimal(doubleString);
    } catch (NumberFormatException e) {
      return null;
    }
  }

  static Integer safeInteger(String doubleString) {
    if (doubleString == null) {
      return null;
    }
    try {
      return new BigDecimal(doubleString).intValue();
    } catch (NumberFormatException e) {
      return null;
    }
  }

  static Property maybeAddAllowableValues(
      Property property,
      AllowableValues allowableValues) {
    if (allowableValues instanceof AllowableListValues) {
      if (property instanceof StringProperty) {
        StringProperty stringProperty = (StringProperty) property;
        AllowableListValues listValues = (AllowableListValues) allowableValues;
        stringProperty.setEnum(listValues.getValues());
      } else if (property instanceof IntegerProperty) {
        IntegerProperty integerProperty = (IntegerProperty) property;
        AllowableListValues listValues = (AllowableListValues) allowableValues;
        integerProperty.setEnum(convert(listValues.getValues(), Integer.class));
      } else if (property instanceof LongProperty) {
        LongProperty longProperty = (LongProperty) property;
        AllowableListValues listValues = (AllowableListValues) allowableValues;
        longProperty.setEnum(convert(listValues.getValues(), Long.class));
      } else if (property instanceof DoubleProperty) {
        DoubleProperty doubleProperty = (DoubleProperty) property;
        AllowableListValues listValues = (AllowableListValues) allowableValues;
        doubleProperty.setEnum(convert(listValues.getValues(), Double.class));
      } else if (property instanceof FloatProperty) {
        FloatProperty floatProperty = (FloatProperty) property;
        AllowableListValues listValues = (AllowableListValues) allowableValues;
        floatProperty.setEnum(convert(listValues.getValues(), Float.class));
      }
    }
    if (allowableValues instanceof AllowableRangeValues) {
      if (property instanceof AbstractNumericProperty) {
        AbstractNumericProperty numeric = (AbstractNumericProperty) property;
        AllowableRangeValues range = (AllowableRangeValues) allowableValues;
        numeric.setMaximum(safeBigDecimal(range.getMax()));
        numeric.exclusiveMaximum(range.getExclusiveMax());
        numeric.setMinimum(safeBigDecimal(range.getMin()));
        numeric.exclusiveMinimum(range.getExclusiveMin());
      }
      if (property instanceof ArrayProperty) {
        ArrayProperty arrayProperty = (ArrayProperty) property;
        AllowableRangeValues allowableRangeValues = (AllowableRangeValues) allowableValues;
        arrayProperty.setMinItems(safeInteger(allowableRangeValues.getMin()));
        arrayProperty.setMaxItems(safeInteger(allowableRangeValues.getMax()));
      }
    }
    return property;
  }

  @SuppressWarnings({"NPathComplexity", "CyclomaticComplexity"})
  static Property maybeAddFacets(
      Property property,
      ElementFacetSource facets) {
    if (facets == null) {
      return property;
    }
    facets.elementFacet(EnumerationFacet.class).ifPresent(f -> {
      if (property instanceof StringProperty) {
        StringProperty stringProperty = (StringProperty) property;
        stringProperty.setEnum(f.getAllowedValues());
      } else if (property instanceof IntegerProperty) {
        IntegerProperty integerProperty = (IntegerProperty) property;
        integerProperty.setEnum(convert(f.getAllowedValues(), Integer.class));
      } else if (property instanceof LongProperty) {
        LongProperty longProperty = (LongProperty) property;
        longProperty.setEnum(convert(f.getAllowedValues(), Long.class));
      } else if (property instanceof DoubleProperty) {
        DoubleProperty doubleProperty = (DoubleProperty) property;
        doubleProperty.setEnum(convert(f.getAllowedValues(), Double.class));
      } else if (property instanceof FloatProperty) {
        FloatProperty floatProperty = (FloatProperty) property;
        floatProperty.setEnum(convert(f.getAllowedValues(), Float.class));
      }
    });
    if (property instanceof AbstractNumericProperty) {
      facets.elementFacet(NumericElementFacet.class).ifPresent(f -> {
        AbstractNumericProperty numeric = (AbstractNumericProperty) property;
        numeric.setMaximum(f.getMaximum());
        numeric.exclusiveMaximum(f.getExclusiveMaximum());
        numeric.setMinimum(f.getMinimum());
        numeric.exclusiveMinimum(f.getExclusiveMinimum());
      });
    }
    if (property instanceof ArrayProperty) {
      facets.elementFacet(CollectionElementFacet.class).ifPresent(f -> {
        ArrayProperty arrayProperty = (ArrayProperty) property;
        arrayProperty.setMinItems(f.getMinItems());
        arrayProperty.setMaxItems(f.getMaxItems());
      });
    }
    if (property instanceof StringProperty) {
      StringProperty stringProperty = (StringProperty) property;
      facets.elementFacet(StringElementFacet.class).ifPresent(f -> {
        stringProperty.maxLength(f.getMaxLength());
        stringProperty.minLength(f.getMinLength());
        if (f.getPattern() != null) {
          stringProperty.pattern(f.getPattern());
        }
      });
    }
    return property;
  }

  private static <T extends Number> List<T> convert(
      List<String> values,
      Class<T> toType) {
    return values.stream().map(converterOfType(toType))
        .filter(Optional::isPresent).map(Optional::get)
        .collect(toList());
  }

  @SuppressWarnings("unchecked")
  private static <T extends Number> Function<? super String, Optional<T>> converterOfType(final Class<T> toType) {
    return (Function<String, Optional<T>>) input -> {
      try {
        if (Integer.class.equals(toType)) {
          return (Optional<T>) of(Integer.valueOf(input));
        } else if (Long.class.equals(toType)) {
          return (Optional<T>) of(Long.valueOf(input));
        } else if (Double.class.equals(toType)) {
          return (Optional<T>) of(Double.valueOf(input));
        } else if (Float.class.equals(toType)) {
          return (Optional<T>) of(Float.valueOf(input));
        }
      } catch (NumberFormatException ignored) {
      }
      return empty();
    };
  }
}
