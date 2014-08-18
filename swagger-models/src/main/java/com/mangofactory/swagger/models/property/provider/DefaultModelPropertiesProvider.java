package com.mangofactory.swagger.models.property.provider;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterables;
import com.mangofactory.swagger.models.property.ModelProperty;
import com.mangofactory.swagger.models.property.bean.BeanModelPropertyProvider;
import com.mangofactory.swagger.models.property.constructor.ConstructorModelPropertyProvider;
import com.mangofactory.swagger.models.property.field.FieldModelPropertyProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value = "default")
public class DefaultModelPropertiesProvider implements ModelPropertiesProvider {

  private final FieldModelPropertyProvider fieldModelPropertyProvider;
  private final BeanModelPropertyProvider beanModelPropertyProvider;
  private final ConstructorModelPropertyProvider constructorModelPropertyProvider;

  @Autowired
  public DefaultModelPropertiesProvider(BeanModelPropertyProvider beanModelPropertyProvider,
                                        FieldModelPropertyProvider fieldModelPropertyProvider,
                                        ConstructorModelPropertyProvider constructorModelPropertyProvider) {
    this.beanModelPropertyProvider = beanModelPropertyProvider;
    this.fieldModelPropertyProvider = fieldModelPropertyProvider;
    this.constructorModelPropertyProvider = constructorModelPropertyProvider;
  }

  @Override
  public Iterable<? extends ModelProperty> propertiesForSerialization(ResolvedType type) {
    return Iterables.concat(fieldModelPropertyProvider.propertiesForSerialization(type),
            beanModelPropertyProvider.propertiesForSerialization(type),
            constructorModelPropertyProvider.propertiesForSerialization(type));
  }

  @Override
  public Iterable<? extends ModelProperty> propertiesForDeserialization(ResolvedType type) {
    return Iterables.concat(fieldModelPropertyProvider.propertiesForDeserialization(type),
            beanModelPropertyProvider.propertiesForDeserialization(type),
            constructorModelPropertyProvider.propertiesForDeserialization(type));
  }

  @Override
  public void setObjectMapper(ObjectMapper objectMapper) {
    fieldModelPropertyProvider.setObjectMapper(objectMapper);
    beanModelPropertyProvider.setObjectMapper(objectMapper);
    constructorModelPropertyProvider.setObjectMapper(objectMapper);
  }
}

