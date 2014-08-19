package com.mangofactory.swagger.models;

import com.wordnik.swagger.model.ModelProperty;
import scala.Option;

import java.util.Map;

public class ScalaConverters {
  public static scala.collection.mutable.LinkedHashMap<String, ModelProperty>
  toScalaLinkedHashMap(Map<String, ModelProperty> propertyMap) {
    scala.collection.mutable.LinkedHashMap<String, ModelProperty> properties = new scala.collection.mutable
            .LinkedHashMap<String, ModelProperty>();
    for (Map.Entry<String, ModelProperty> entry : propertyMap.entrySet()) {
      properties.put(entry.getKey(), entry.getValue());
    }
    return properties;
  }

  public static <T> T fromOption(Option<T> o) {
    if (!o.isDefined()) {
      return null;
    }
    return o.get();
  }
}
