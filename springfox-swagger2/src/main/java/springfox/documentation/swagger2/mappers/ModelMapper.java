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

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.types.ResolvedInterfaceType;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Multimap;
import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.properties.AbstractNumericProperty;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.MapProperty;
import io.swagger.models.properties.ObjectProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.StringProperty;
import org.mapstruct.Mapper;
import springfox.documentation.schema.ModelProperty;
import springfox.documentation.schema.ModelReference;
import springfox.documentation.service.AllowableListValues;
import springfox.documentation.service.AllowableRangeValues;
import springfox.documentation.service.AllowableValues;
import springfox.documentation.service.ApiListing;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.google.common.collect.Maps.*;
import static springfox.documentation.schema.Maps.*;
import static springfox.documentation.swagger2.mappers.Properties.*;

@Mapper
public abstract class ModelMapper {
  public Map<String, Model> mapModels(Map<String, springfox.documentation.schema.Model> from) {
    if (from == null) {
      return null;
    }

    Map<String, Model> map = new HashMap<String, Model>();

    for (java.util.Map.Entry<String, springfox.documentation.schema.Model> entry : from.entrySet()) {
      String key = entry.getKey();
      Model value = mapProperties(entry.getValue());
      map.put(key, value);
    }

    return map;
  }

  public Model mapProperties(springfox.documentation.schema.Model source) {
    ModelImpl model = new ModelImpl()
        .description(source.getDescription())
        .discriminator(source.getDiscriminator())
        .example(source.getExample())
        .name(source.getName());

    SortedMap<String, ModelProperty> sortedProperties = sort(source.getProperties());
    Map<String, Property> modelProperties = mapProperties(sortedProperties);
    model.setProperties(modelProperties);

    FluentIterable<String> requiredFields = FluentIterable.from(source.getProperties().values())
        .filter(requiredProperty())
        .transform(propertyName());
    model.setRequired(requiredFields.toList());
    model.setSimple(false);
    if (isInterface(source.getType())) {
      model.setType(ModelImpl.OBJECT);
    }
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
    for (Map.Entry<String, ModelProperty> propertyEntry : properties.entrySet()) {
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

  private boolean isInterface(ResolvedType type) {
    return type instanceof ResolvedInterfaceType;
  }

  @VisibleForTesting
  Optional<Class> typeOfValue(springfox.documentation.schema.Model source) {
    Optional<ResolvedType> mapInterface = findMapInterface(source.getType());
    if (mapInterface.isPresent()) {
      return Optional.of((Class) mapInterface.get().getTypeParameters().get(1).getErasedType());
    }
    return Optional.absent();
  }

  private Optional<ResolvedType> findMapInterface(ResolvedType type) {
    return Optional.fromNullable(type.findSupertype(Map.class));
  }

  public Property mapProperty(ModelProperty source) {
    Property property = modelRefToProperty(source.getModelRef());

    addEnumValues(property, source.getAllowableValues());

    if (property instanceof ArrayProperty) {
      ArrayProperty arrayProperty = (ArrayProperty) property;
      addEnumValues(arrayProperty.getItems(), source.getAllowableValues());
    }

    if (property instanceof AbstractNumericProperty) {
      AllowableValues allowableValues = source.getAllowableValues();
      if (allowableValues instanceof AllowableRangeValues) {
        AllowableRangeValues range = (AllowableRangeValues) allowableValues;
        ((AbstractNumericProperty) property).maximum(Double.valueOf(range.getMax()));
        ((AbstractNumericProperty) property).minimum(Double.valueOf(range.getMin()));
      }
    }

    if (property instanceof StringProperty) {
      AllowableValues allowableValues = source.getAllowableValues();
      if (allowableValues instanceof AllowableRangeValues) {
        AllowableRangeValues range = (AllowableRangeValues) allowableValues;
        ((StringProperty) property).maxLength(Integer.valueOf(range.getMax()));
        ((StringProperty) property).minLength(Integer.valueOf(range.getMin()));
      }
    }

    if (property != null) {
      property.setDescription(source.getDescription());
      property.setName(source.getName());
      property.setRequired(source.isRequired());
      property.setReadOnly(source.isReadOnly());
      property.setExample(source.getExample());
    }
    return property;
  }

  private static Property addEnumValues(Property property, AllowableValues allowableValues) {
    if (property instanceof StringProperty && allowableValues instanceof AllowableListValues) {
      StringProperty stringProperty = (StringProperty) property;
      AllowableListValues listValues = (AllowableListValues) allowableValues;
      stringProperty.setEnum(listValues.getValues());
    }
    return property;
  }

  static Property modelRefToProperty(ModelReference modelRef) {
    if (modelRef == null || "void".equalsIgnoreCase(modelRef.getType())) {
      return null;
    }
    Property responseProperty;
    if (modelRef.isCollection()) {
      responseProperty = new ArrayProperty(
          addEnumValues(itemTypeProperty(modelRef.itemModel().get()), modelRef.getAllowableValues()));
    } else if (modelRef.isMap()) {
      String itemType = modelRef.getItemType();
      responseProperty = new MapProperty(property(itemType));
    } else {
      responseProperty = property(modelRef.getType());
    }

    addEnumValues(responseProperty, modelRef.getAllowableValues());

    return responseProperty;
  }

  protected Map<String, Model> modelsFromApiListings(Multimap<String, ApiListing> apiListings) {
    Map<String, springfox.documentation.schema.Model> definitions = newHashMap();
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
