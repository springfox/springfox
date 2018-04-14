/*
 *
 *  Copyright 2015-2018 the original author or authors.
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
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Multimap;
import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.Xml;
import io.swagger.models.properties.AbstractNumericProperty;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.MapProperty;
import io.swagger.models.properties.ObjectProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.StringProperty;
import org.mapstruct.Mapper;
import springfox.documentation.schema.ModelProperty;
import springfox.documentation.schema.ModelReference;
import springfox.documentation.service.AllowableRangeValues;
import springfox.documentation.service.AllowableValues;
import springfox.documentation.service.ApiListing;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.google.common.base.Predicates.*;
import static com.google.common.collect.Maps.*;
import static springfox.documentation.schema.Maps.*;
import static springfox.documentation.swagger2.mappers.EnumMapper.*;
import static springfox.documentation.swagger2.mappers.Properties.*;

@Mapper
public abstract class ModelMapper {
  public Map<String, Model> mapModels(Map<String, springfox.documentation.schema.Model> from) {
    if (from == null) {
      return null;
    }

    Map<String, Model> map = newTreeMap();

    for (java.util.Map.Entry<String, springfox.documentation.schema.Model> entry : from.entrySet()) {
      String key = entry.getKey();
      Model value = mapProperties(entry.getValue());
      map.put(key, value);
    }

    return map;
  }

  protected Model mapProperties(springfox.documentation.schema.Model source) {
    ModelImpl model = new ModelImpl()
        .description(source.getDescription())
        .discriminator(source.getDiscriminator())
        .example(source.getExample())
        .name(source.getName())
        .xml(mapXml(source.getXml()));

    SortedMap<String, ModelProperty> sortedProperties = sort(source.getProperties());
    Map<String, Property> modelProperties = mapProperties(sortedProperties);
    model.setProperties(modelProperties);

    FluentIterable<String> requiredFields = FluentIterable.from(source.getProperties().values())
        .filter(requiredProperty())
        .transform(propertyName());
    model.setRequired(requiredFields.toList());
    model.setSimple(false);
    model.setType(ModelImpl.OBJECT);
    model.setTitle(source.getName());
    if (isMapType(source.getType())) {
      Optional<Class> clazz = typeOfValue(source);
      if (clazz.isPresent()) {
        model.additionalProperties(property(clazz.get().getSimpleName()));
      } else {
        model.additionalProperties(new ObjectProperty());
      }
    }
    return model;
  }

  private Map<String, Property> mapProperties(SortedMap<String, ModelProperty> properties) {
    Map<String, Property> mappedProperties = new LinkedHashMap<String, Property>();
    SortedMap<String, ModelProperty> nonVoidProperties = filterEntries(properties, not(voidProperties()));
    for (Map.Entry<String, ModelProperty> propertyEntry : nonVoidProperties.entrySet()) {
      mappedProperties.put(propertyEntry.getKey(), mapProperty(propertyEntry.getValue()));
    }
    return mappedProperties;
  }

  /**
   * Returns a {@link TreeMap} where the keys are sorted by their respective property position values in ascending
   * order.
   *
   * @param modelProperties
   * @return
   */
  private SortedMap<String, ModelProperty> sort(Map<String, ModelProperty> modelProperties) {

    SortedMap<String, ModelProperty> sortedMap = new TreeMap<String, ModelProperty>(defaultOrdering(modelProperties));
    sortedMap.putAll(modelProperties);
    return sortedMap;
  }

  @VisibleForTesting
  Optional<Class> typeOfValue(springfox.documentation.schema.Model source) {
    Optional<ResolvedType> mapInterface = findMapInterface(source.getType());
    if (mapInterface.isPresent()) {
      if (mapInterface.get().getTypeParameters().size() == 2) {
        return Optional.of((Class) mapInterface.get().getTypeParameters().get(1).getErasedType());
      }
      return Optional.of((Class) Object.class);
    }
    return Optional.absent();
  }

  private Optional<ResolvedType> findMapInterface(ResolvedType type) {
    return Optional.fromNullable(type.findSupertype(Map.class));
  }

  private Property mapProperty(ModelProperty source) {
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

    Map<String, Object> extensions = new VendorExtensionsMapper().mapExtensions(source.getVendorExtensions());

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

  static Property modelRefToProperty(ModelReference modelRef) {
    if (modelRef == null || "void".equalsIgnoreCase(modelRef.getType())) {
      return null;
    }
    Property responseProperty;
    if (modelRef.isCollection()) {
      responseProperty = property(modelRef);
    } else if (modelRef.isMap()) {
      responseProperty = new MapProperty(property(modelRef.itemModel().get()));
    } else {
      responseProperty = property(modelRef.getType());
    }

    maybeAddAllowableValues(responseProperty, modelRef.getAllowableValues());

    return responseProperty;
  }

  Map<String, Model> modelsFromApiListings(Multimap<String, ApiListing> apiListings) {
    Map<String, springfox.documentation.schema.Model> definitions = newTreeMap();
    for (ApiListing each : apiListings.values()) {
      definitions.putAll(each.getModels());
    }
    return mapModels(definitions);
  }

  private Function<ModelProperty, String> propertyName() {
    return new Function<ModelProperty, String>() {
      @Override
      public String apply(ModelProperty input) {
        return input.getName();
      }
    };
  }

  private Predicate<ModelProperty> requiredProperty() {
    return new Predicate<ModelProperty>() {
      @Override
      public boolean apply(ModelProperty input) {
        return input.isRequired();
      }
    };
  }
}
