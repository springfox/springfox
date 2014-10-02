package com.mangofactory.swagger.models.property;

import com.wordnik.swagger.models.properties.Property;

public interface SwaggerPropertyFactory {
  Property create(ModelProperty property);
}
