package com.mangofactory.swagger.configuration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.wordnik.swagger.model.Authorization;
import com.wordnik.swagger.model.AuthorizationScope;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SwaggerAuthorizationJsonSerializer extends JsonSerializer<Authorization> {

   @Override
   public void serialize(Authorization auth, JsonGenerator jgen, SerializerProvider provider) throws IOException,
           JsonProcessingException {

      Map<String, AuthorizationScope[]> result  = new HashMap<String, AuthorizationScope[]>();
      result.put(auth.type(), auth.scopes());
      jgen.writeObject(result);
   }
}
