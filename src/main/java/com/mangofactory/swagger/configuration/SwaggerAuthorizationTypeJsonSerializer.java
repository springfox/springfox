package com.mangofactory.swagger.configuration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.wordnik.swagger.model.AuthorizationType;

import java.io.IOException;

/**
 * Swagger serializes a list of AuthorizationTypes almost like a map as opposed to a list
 */
public class SwaggerAuthorizationTypeJsonSerializer extends JsonSerializer<AuthorizationType> {

   @Override
   public void serialize(AuthorizationType value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
           JsonProcessingException {
      String jsonString = com.wordnik.swagger.core.util.JsonSerializer.asJson(value);
      jgen.writeRaw(jsonString);
   }
}
