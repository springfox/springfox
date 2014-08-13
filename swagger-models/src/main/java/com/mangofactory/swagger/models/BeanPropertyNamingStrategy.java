package com.mangofactory.swagger.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;

public interface BeanPropertyNamingStrategy {
  String nameForSerialization(BeanPropertyDefinition beanProperty);

  String nameForDeserialization(BeanPropertyDefinition beanProperty);

  void setObjectMapper(ObjectMapper objectMapper);
}
