package com.mangofactory.swagger.models.dto.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.mangofactory.swagger.models.dto.ApiListing;

import java.io.IOException;

public class SwaggerApiListingJsonSerializer extends JsonSerializer<ApiListing> {

  private ObjectMapper objectMapper;

  public SwaggerApiListingJsonSerializer(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public void serialize(ApiListing value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
    jgen.writeRaw(objectMapper.writeValueAsString(value));
  }
}