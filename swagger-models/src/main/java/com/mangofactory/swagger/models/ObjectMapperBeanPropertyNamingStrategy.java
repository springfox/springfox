package com.mangofactory.swagger.models;

import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static com.mangofactory.swagger.models.property.BeanPropertyDefinitions.*;

/**
 * BeanPropertyNamingStrategy based on ObjectMapper naming strategy.
 * Uses {@link com.fasterxml.jackson.databind.PropertyNamingStrategy} to name.
 * In case it cannot get information from property's getter or field, it returns the same current name.
 */
@Component
public class ObjectMapperBeanPropertyNamingStrategy implements BeanPropertyNamingStrategy {

  private static final Logger LOG = LoggerFactory.getLogger(ObjectMapperBeanPropertyNamingStrategy.class);
  private ObjectMapper objectMapper;

  public ObjectMapperBeanPropertyNamingStrategy() {
  }

  @VisibleForTesting
  ObjectMapperBeanPropertyNamingStrategy(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public String nameForSerialization(final BeanPropertyDefinition beanProperty) {

    SerializationConfig serializationConfig = objectMapper.getSerializationConfig();

    Optional<PropertyNamingStrategy> namingStrategy
            = Optional.fromNullable(serializationConfig.getPropertyNamingStrategy());
    String newName = namingStrategy
            .transform(overTheWireName(beanProperty, serializationConfig))
            .or(beanProperty.getName());

    LOG.debug("Name '{}' renamed to '{}'", beanProperty.getName(), newName);

    return newName;
  }

  @Override
  public String nameForDeserialization(final BeanPropertyDefinition beanProperty) {

    DeserializationConfig deserializationConfig = objectMapper.getDeserializationConfig();

    Optional<PropertyNamingStrategy> namingStrategy
            = Optional.fromNullable(deserializationConfig.getPropertyNamingStrategy());
    String newName = namingStrategy
            .transform(overTheWireName(beanProperty, deserializationConfig))
            .or(beanProperty.getName());

    LOG.debug("Name '{}' renamed to '{}'", beanProperty.getName(), newName);

    return newName;
  }

  public void setObjectMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }
}
