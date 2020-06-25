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

import com.fasterxml.classmate.ResolvedType;
import io.swagger.models.ComposedModel;
import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.RefModel;
import io.swagger.models.Xml;
import io.swagger.models.properties.AbstractNumericProperty;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.MapProperty;
import io.swagger.models.properties.ObjectProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.StringProperty;
import org.mapstruct.Mapper;
import springfox.documentation.service.AllowableRangeValues;
import springfox.documentation.service.AllowableValues;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Stream;

import static java.util.Collections.*;
import static java.util.Optional.*;
import static java.util.stream.Collectors.*;
import static springfox.documentation.schema.Maps.*;
import static springfox.documentation.swagger2.mappers.EnumMapper.*;
/**
 * Use {@link ModelSpecificationMapper} instead
 * @deprecated @since 3.0.0
 */
@Deprecated
@Mapper
public class ModelMapper {
  public Map<String, Model> mapModels(Map<String, springfox.documentation.schema.Model> from) {
    if (from == null) {
      return null;
    }

    Map<String, Model> map = new TreeMap<>(Comparator.naturalOrder());
    InheritanceDeterminer determiner = new InheritanceDeterminer(from);
    for (java.util.Map.Entry<String, springfox.documentation.schema.Model> entry : from.entrySet()) {
      String key = entry.getKey();
      Model value;
      if (determiner.hasParent(entry.getValue())) {
        value = mapComposedModel(determiner.parent(entry.getValue()), entry.getValue());
      } else {
        value = mapModel(entry.getValue());
      }
      map.put(key, value);
    }

    return map;
  }

  private Model mapComposedModel(
      RefModel parent,
      springfox.documentation.schema.Model source) {
    ComposedModel model = new ComposedModel()
        .interfaces(singletonList(parent))
        .child(mapModel(source));

    model.setDescription(source.getDescription());
    model.setExample(source.getExample());
    model.setTitle(source.getName());

    SortedMap<String, springfox.documentation.schema.ModelProperty> sortedProperties = sort(source.getProperties());
    Map<String, Property> modelProperties = mapProperties(sortedProperties);
    model.setProperties(modelProperties);
    return model;
  }

  private Model mapModel(springfox.documentation.schema.Model source) {
    ModelImpl model = new ModelImpl()
        .description(source.getDescription())
        .discriminator(source.getDiscriminator())
        .example(source.getExample())
        .name(source.getName())
        .xml(mapXml(source.getXml()));

    SortedMap<String, springfox.documentation.schema.ModelProperty> sortedProperties = sort(source.getProperties());
    Map<String, Property> modelProperties = mapProperties(sortedProperties);
    model.setProperties(modelProperties);

    Stream<String> requiredFields = source.getProperties().values().stream()
        .filter(springfox.documentation.schema.ModelProperty::isRequired)
        .map(springfox.documentation.schema.ModelProperty::getName);
    model.setRequired(requiredFields.collect(toList()));
    model.setSimple(false);
    model.setType(ModelImpl.OBJECT);
    model.setTitle(source.getName());
    if (isMapType(source.getType())) {
      Optional<Class> clazz = typeOfValue(source);
      if (clazz.isPresent()) {
        model.additionalProperties(
            springfox.documentation.swagger2.mappers.Properties.property(clazz.get().getSimpleName()));
      } else {
        model.additionalProperties(new ObjectProperty());
      }
    }
    return model;
  }

  private Map<String, Property> mapProperties(
      SortedMap<String, springfox.documentation.schema.ModelProperty> properties) {
    Map<String, Property> mappedProperties = new LinkedHashMap<>();
    properties.entrySet().stream()
        .filter(springfox.documentation.swagger2.mappers.Properties.voidProperties().negate())
        .forEachOrdered(each -> mappedProperties.put(each.getKey(), mapProperty(each.getValue())));
    return mappedProperties;
  }

  /**
   * Returns a {@link TreeMap} where the keys are sorted by their respective property position values in ascending
   * order.
   *
   * @param modelProperties properties to sort
   * @return sorted properties by position and name
   */
  private SortedMap<String, springfox.documentation.schema.ModelProperty>
  sort(Map<String, springfox.documentation.schema.ModelProperty> modelProperties) {

    SortedMap<String, springfox.documentation.schema.ModelProperty> sortedMap
        = new TreeMap<>(springfox.documentation.swagger2.mappers.Properties.defaultOrdering(modelProperties));
    sortedMap.putAll(modelProperties);
    return sortedMap;
  }

  Optional<Class> typeOfValue(springfox.documentation.schema.Model source) {
    Optional<ResolvedType> mapInterface = findMapInterface(source.getType());
    if (mapInterface.isPresent()) {
      if (mapInterface.get().getTypeParameters().size() == 2) {
        return of(mapInterface.get().getTypeParameters().get(1).getErasedType());
      }
      return of(Object.class);
    }
    return empty();
  }

  private Optional<ResolvedType> findMapInterface(ResolvedType type) {
    return ofNullable(type.findSupertype(Map.class));
  }

  @SuppressWarnings("NPathComplexity")
  private Property mapProperty(springfox.documentation.schema.ModelProperty source) {
    Property property = modelRefToProperty(source.getModelRef());

    maybeAddAllowableValues(property, source.getAllowableValues());

    if (property instanceof ArrayProperty) {
      ArrayProperty arrayProperty = (ArrayProperty) property;
      maybeAddAllowableValues(arrayProperty.getItems(), source.getAllowableValues());
    }

    if (property instanceof AbstractNumericProperty) {
      AbstractNumericProperty numericProperty = (AbstractNumericProperty) property;
      AllowableValues allowableValues = source.getAllowableValues();
      if (allowableValues instanceof AllowableRangeValues) {
        AllowableRangeValues range = (AllowableRangeValues) allowableValues;
        numericProperty.maximum(safeBigDecimal(range.getMax()));
        numericProperty.exclusiveMaximum(range.getExclusiveMax());
        numericProperty.minimum(safeBigDecimal(range.getMin()));
        numericProperty.exclusiveMinimum(range.getExclusiveMin());
      }
    }

    if (property instanceof StringProperty) {
      StringProperty stringProperty = (StringProperty) property;
      AllowableValues allowableValues = source.getAllowableValues();
      if (allowableValues instanceof AllowableRangeValues) {
        AllowableRangeValues range = (AllowableRangeValues) allowableValues;
        stringProperty.maxLength(safeInteger(range.getMax()));
        stringProperty.minLength(safeInteger(range.getMin()));
      }
      if (source.getPattern() != null) {
        stringProperty.setPattern(source.getPattern());
      }
      stringProperty.setDefault(source.getDefaultValue());
    }

    Map<String, Object> extensions = new VendorExtensionsMapper()
        .mapExtensions(source.getVendorExtensions());

    if (property != null) {
      property.setDescription(source.getDescription());
      property.setName(source.getName());
      property.setRequired(source.isRequired());
      property.setReadOnly(source.isReadOnly());
      property.setAllowEmptyValue(source.isAllowEmptyValue());
      property.setExample(source.getExample());
      property.getVendorExtensions().putAll(extensions);
      property.setXml(mapXml(source.getXml()));
    }

    return property;
  }

  private Xml mapXml(springfox.documentation.schema.Xml xml) {
    if (xml == null) {
      return null;
    }
    return new Xml()
        .name(xml.getName())
        .attribute(xml.getAttribute())
        .namespace(xml.getNamespace())
        .prefix(xml.getPrefix())
        .wrapped(xml.getWrapped());
  }


  static Integer safeInteger(String doubleString) {
    try {
      return Integer.valueOf(doubleString);
    } catch (NumberFormatException e) {
      return null;
    }
  }

  static Property modelRefToProperty(springfox.documentation.schema.ModelReference modelRef) {
    if (modelRef == null || "void".equalsIgnoreCase(modelRef.getType())) {
      return null;
    }
    Property responseProperty;
    if (modelRef.isCollection()) {
      responseProperty = springfox.documentation.swagger2.mappers.Properties.property(modelRef);
    } else if (modelRef.isMap()) {
      responseProperty =
          new MapProperty(springfox.documentation.swagger2.mappers.Properties.property(modelRef.itemModel()
              .orElseThrow(() -> new IllegalStateException("ModelRef that is a map should have an itemModel"))));
    } else {
      responseProperty = springfox.documentation.swagger2.mappers.Properties.property(modelRef.getType());
    }

    maybeAddAllowableValues(responseProperty, modelRef.getAllowableValues());

    return responseProperty;
  }


}
