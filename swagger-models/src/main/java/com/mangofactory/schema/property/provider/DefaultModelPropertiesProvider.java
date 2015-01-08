package com.mangofactory.schema.property.provider;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mangofactory.schema.plugins.ModelContext;
import com.mangofactory.schema.property.bean.BeanModelPropertyProvider;
import com.mangofactory.schema.property.constructor.ConstructorModelPropertyProvider;
import com.mangofactory.schema.property.field.FieldModelPropertyProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.collect.Lists.*;

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
  public List<com.mangofactory.service.model.ModelProperty> propertiesFor(ResolvedType type, ModelContext
          givenContext) {
    List<com.mangofactory.service.model.ModelProperty> concat
            = newArrayList(fieldModelPropertyProvider.propertiesFor(type, givenContext));
    concat.addAll(beanModelPropertyProvider.propertiesFor(type, givenContext));
    concat.addAll(constructorModelPropertyProvider.propertiesFor(type, givenContext));
    return concat;
  }

  @Override
  public void setObjectMapper(ObjectMapper objectMapper) {
    fieldModelPropertyProvider.setObjectMapper(objectMapper);
    beanModelPropertyProvider.setObjectMapper(objectMapper);
    constructorModelPropertyProvider.setObjectMapper(objectMapper);
  }
}

