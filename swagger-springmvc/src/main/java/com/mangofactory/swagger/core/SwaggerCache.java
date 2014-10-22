package com.mangofactory.swagger.core;

import com.wordnik.swagger.models.Swagger;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.google.common.collect.Maps.*;

@Component
public class SwaggerCache {
  private Map<String, Swagger> swaggerApiMap = newLinkedHashMap();

  public void addSwaggerApi(String swaggerGroup, Swagger swagger) {
    swaggerApiMap.put(swaggerGroup, swagger);
  }

  public Swagger getResourceListing(String key) {
    return swaggerApiMap.get(key);
  }

  public Map<String, Swagger> getSwaggerApiMap() {
    return swaggerApiMap;
  }
}
