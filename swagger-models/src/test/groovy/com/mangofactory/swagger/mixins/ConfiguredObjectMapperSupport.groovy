package com.mangofactory.swagger.mixins

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.databind.ObjectMapper

class ConfiguredObjectMapperSupport {
  ObjectMapper objectMapperThatUsesFields() {
    def objectMapper = new ObjectMapper()
    objectMapper.serializationConfig.defaultVisibilityChecker
      .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
      .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
      .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
    objectMapper.deserializationConfig.defaultVisibilityChecker
            .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
            .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
            .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
    return objectMapper
  }
  ObjectMapper objectMapperThatUsesGetters() {
    def objectMapper = new ObjectMapper()
    objectMapper.serializationConfig.defaultVisibilityChecker
            .withFieldVisibility(JsonAutoDetect.Visibility.NONE)
            .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
            .withGetterVisibility(JsonAutoDetect.Visibility.ANY)
    objectMapper.deserializationConfig.defaultVisibilityChecker
            .withFieldVisibility(JsonAutoDetect.Visibility.NONE)
            .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
            .withGetterVisibility(JsonAutoDetect.Visibility.ANY)
    return objectMapper
  }
  ObjectMapper objectMapperThatUsesSetters() {
    def objectMapper = new ObjectMapper()
    objectMapper.serializationConfig.defaultVisibilityChecker
            .withFieldVisibility(JsonAutoDetect.Visibility.NONE)
            .withSetterVisibility(JsonAutoDetect.Visibility.ANY)
            .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
    objectMapper.deserializationConfig.defaultVisibilityChecker
            .withFieldVisibility(JsonAutoDetect.Visibility.NONE)
            .withSetterVisibility(JsonAutoDetect.Visibility.ANY)
            .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
    return objectMapper
  }
}
