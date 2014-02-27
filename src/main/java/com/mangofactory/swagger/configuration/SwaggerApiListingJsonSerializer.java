package com.mangofactory.swagger.configuration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.wordnik.swagger.model.ApiListing;

import java.io.IOException;

public class SwaggerApiListingJsonSerializer extends JsonSerializer<ApiListing> {

  @Override
  public void serialize(ApiListing value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
      JsonProcessingException {
    String jsonString = com.wordnik.swagger.core.util.JsonSerializer.asJson(value);
    jgen.writeRaw(jsonString);
  }
}
