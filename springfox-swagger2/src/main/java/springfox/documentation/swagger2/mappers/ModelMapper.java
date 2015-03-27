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
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.wordnik.swagger.models.Model;
import com.wordnik.swagger.models.ModelImpl;
import com.wordnik.swagger.models.properties.ArrayProperty;
import com.wordnik.swagger.models.properties.BooleanProperty;
import com.wordnik.swagger.models.properties.DateProperty;
import com.wordnik.swagger.models.properties.DateTimeProperty;
import com.wordnik.swagger.models.properties.DecimalProperty;
import com.wordnik.swagger.models.properties.DoubleProperty;
import com.wordnik.swagger.models.properties.FloatProperty;
import com.wordnik.swagger.models.properties.IntegerProperty;
import com.wordnik.swagger.models.properties.LongProperty;
import com.wordnik.swagger.models.properties.MapProperty;
import com.wordnik.swagger.models.properties.ObjectProperty;
import com.wordnik.swagger.models.properties.Property;
import com.wordnik.swagger.models.properties.RefProperty;
import com.wordnik.swagger.models.properties.StringProperty;
import com.wordnik.swagger.models.properties.UUIDProperty;
import org.mapstruct.Mapper;
import springfox.documentation.schema.ModelProperty;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.AllowableListValues;
import springfox.documentation.service.AllowableValues;
import springfox.documentation.service.ApiListing;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static com.google.common.collect.Maps.*;

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
            .filter(requiredProperties())
            .transform(propertyName());
    model.setRequired(requiredFields.toList());
    model.setSimple(false);
    return model;
  }

  public Property mapProperty(ModelProperty source) {
    Property property = modelRefToProperty(source.getModelRef());
    //TODO: more mapping needs to happen for AllowableRange
    if (property instanceof StringProperty) {
      AllowableValues allowableValues = source.getAllowableValues();
      if (allowableValues instanceof AllowableListValues) {
        ((StringProperty) property).setEnum(((AllowableListValues) allowableValues).getValues());
      }
    }
    property.setDescription(source.getDescription());
    property.setName(source.getName());
    property.setRequired(source.isRequired());
    return property;
  }

  static Property modelRefToProperty(ModelRef modelRef) {
    if (modelRef == null) {
      return null;
    }
    Property responseProperty;
    if (modelRef.isCollection()) {
      String itemType = modelRef.getItemType();
      responseProperty = new ArrayProperty(property(itemType));
    } else if (modelRef.isMap()) {
      String itemType = modelRef.getItemType();
      responseProperty = new MapProperty(property(itemType));
    } else {
      responseProperty = property(modelRef.getType());
    }
    return responseProperty;
  }

  // CHECKSTYLE:OFF
  static Property property(String typeName) {
    if (isOfType(typeName, "int")) {
      return new IntegerProperty();
    }
    if (isOfType(typeName, "long")) {
      return new LongProperty();
    }
    if (isOfType(typeName, "float")) {
      return new FloatProperty();
    }
    if (isOfType(typeName, "double")) {
      return new DoubleProperty();
    }
    if (isOfType(typeName, "string")) {
      return new StringProperty();
    }
    if (isOfType(typeName, "byte")) {
      StringProperty byteArray = new StringProperty();
      byteArray.setFormat("byte");
      return byteArray;
    }
    if (isOfType(typeName, "boolean")) {
      return new BooleanProperty();
    }
    if (isOfType(typeName, "Date")) {
      return new DateProperty();
    }
    if (isOfType(typeName, "DateTime") || isOfType(typeName, "date-time")) {
      return new DateTimeProperty();
    }
    if (isOfType(typeName, "BigDecimal") || isOfType(typeName, "BigInteger")) {
      return new DecimalProperty();
    }
    if (isOfType(typeName, "UUID")) {
      return new UUIDProperty();
    }
    if (isOfType(typeName, "Void")) {
      return null;
    }
    if (isOfType(typeName, "Object")) {
      return new ObjectProperty();
    }
    return new RefProperty(typeName);
  }
  // CHECKSTYLE:ON
  
  private static boolean isOfType(String initialType, String ofType) {
    return initialType.equalsIgnoreCase(ofType);
  }

  protected Map<String, Model> modelsFromApiListings(Map<String, ApiListing> apiListings) {
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

  private Predicate<ModelProperty> requiredProperties() {
    return new Predicate<ModelProperty>() {
      @Override
      public boolean apply(ModelProperty input) {
        return input.isRequired();
      }
    };
  }
}
