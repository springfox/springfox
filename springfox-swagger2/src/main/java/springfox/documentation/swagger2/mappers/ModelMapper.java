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
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.AllowableListValues;
import springfox.documentation.service.AllowableRangeValues;
import springfox.documentation.service.AllowableValues;
import springfox.documentation.service.ApiListing;

import java.util.HashMap;
import java.util.Map;
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
        .example("")
        .name(source.getName());
    TreeMap<String, Property> sorted = newTreeMap();
    sorted.putAll(mapProperties(source.getProperties()));
    model.setProperties(sorted);
    FluentIterable<String> requiredFields = FluentIterable.from(source.getProperties().values())
        .filter(requiredProperty())
        .transform(propertyName());
    model.setRequired(requiredFields.toList());
    model.setSimple(false);
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

  private Optional<Class> typeOfValue(springfox.documentation.schema.Model source) {
    if (source.getType().getTypeParameters() != null && source.getType().getTypeParameters().size() > 0) {
      return Optional.of((Class)source.getType().getTypeParameters().get(1).getErasedType());
    }
    return Optional.absent();
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
    if (property != null) {
      property.setDescription(source.getDescription());
      property.setName(source.getName());
      property.setRequired(source.isRequired());
      property.setReadOnly(source.isReadOnly());
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

  static Property modelRefToProperty(ModelRef modelRef) {
    if (modelRef == null || "void".equalsIgnoreCase(modelRef.getType())) {
      return null;
    }
    Property responseProperty;
    if (modelRef.isCollection()) {
      String itemType = modelRef.getItemType();
      responseProperty = new ArrayProperty(addEnumValues(property(itemType), modelRef.getAllowableValues()));
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

  protected abstract Map<String, Property> mapProperties(Map<String, ModelProperty> properties);

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
