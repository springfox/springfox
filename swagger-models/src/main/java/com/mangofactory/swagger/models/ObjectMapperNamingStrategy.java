package com.mangofactory.swagger.models;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.introspect.POJOPropertyBuilder;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * NamingStrategy based on ObjectMapper naming strategy.
 * Uses {@link com.fasterxml.jackson.databind.PropertyNamingStrategy} to name.
 * In case it cannot get information from property's getter or field, it returns the same current name.
 */
public class ObjectMapperNamingStrategy implements NamingStrategy {

  public static final boolean FOR_SERIALIZATION = true;
  private static final Logger LOG = LoggerFactory.getLogger(ObjectMapperNamingStrategy.class);
  private final SerializationConfig serializationConfig;
  private ObjectMapper objectMapper;
  private AnnotationIntrospector annotationIntrospector;

  public ObjectMapperNamingStrategy(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
    this.serializationConfig = objectMapper.getSerializationConfig();
    this.annotationIntrospector = this.serializationConfig.isAnnotationProcessingEnabled() ?
            this.serializationConfig.getAnnotationIntrospector() : null;
  }

  @Override
  public String name(final String currentName) {

    Optional<PropertyNamingStrategy> namingStrategy = Optional.fromNullable(this.serializationConfig
            .getPropertyNamingStrategy());
    String newName = namingStrategy.transform(new Function<PropertyNamingStrategy, String>() {

      @Override
      public String apply(PropertyNamingStrategy naming) {
        POJOPropertyBuilder prop = new POJOPropertyBuilder(currentName, annotationIntrospector, FOR_SERIALIZATION);
        return naming.nameForField(serializationConfig, prop.getField(),
                currentName);
      }

    }).or(currentName);

    LOG.debug("Name '{}' renamed to '{}'", currentName, newName);

    return newName;
  }
}
