package com.mangofactory.swagger.models.dto.jackson;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.mangofactory.swagger.models.dto.ApiListing;
import com.mangofactory.swagger.models.dto.ResourceListing;

public class SwaggerJacksonProvider {

  private final ObjectMapper objectMapper;

  public SwaggerJacksonProvider() {
    this.objectMapper = new ObjectMapper();
    configure(this.objectMapper);
  }

  private void configure(ObjectMapper mapper) {
    mapper.setVisibilityChecker(
            mapper.getSerializationConfig().getDefaultVisibilityChecker()
                    .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                    .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                    .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                    .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
    configureSerializationFeatures(mapper);
  }

  private void configureSerializationFeatures(ObjectMapper mapper) {
    mapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
  }

  public Module swaggerJacksonModule() {
    SimpleModule module = new SimpleModule("SwaggerJacksonModule");
    module.addSerializer(ApiListing.class, new SwaggerApiListingJsonSerializer(objectMapper));
    module.addSerializer(ResourceListing.class, new SwaggerResourceListingJsonSerializer(objectMapper));
    return module;
  }

}
