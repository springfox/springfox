package com.mangofactory.documentation.swagger2.mappers;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.mangofactory.documentation.schema.ModelProperty;
import com.mangofactory.documentation.schema.ModelRef;
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
import com.wordnik.swagger.models.properties.ObjectProperty;
import com.wordnik.swagger.models.properties.Property;
import com.wordnik.swagger.models.properties.RefProperty;
import com.wordnik.swagger.models.properties.StringProperty;
import com.wordnik.swagger.models.properties.UUIDProperty;
import org.mapstruct.Mapper;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static com.google.common.collect.Maps.*;

@Mapper
public abstract class ModelMapper {
  public Map<String, Model> mapModels(Map<String, com.mangofactory.documentation.schema.Model> from) {
    if (from == null) {
      return null;
    }

    Map<String, Model> map = new HashMap<String, Model>();

    for (java.util.Map.Entry<String, com.mangofactory.documentation.schema.Model> entry : from.entrySet()) {
      String key = entry.getKey();
      Model value = mapProperties(entry.getValue());
      map.put(key, value);
    }

    return map;
  }

  public Model mapProperties(com.mangofactory.documentation.schema.Model source) {
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
    String typeName = source.getModelRef().getType();
    return property(typeName);
  }

  static Property modelRefToProperty(ModelRef modelRef) {
    if (modelRef == null) {
      return null;
    }
    Property responseProperty;
    if (modelRef.isCollection()) {
      String itemType = modelRef.getItemType();
      responseProperty = new ArrayProperty(property(itemType));
    } else {
      responseProperty = property(modelRef.getType());
    }
    return responseProperty;
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
}
