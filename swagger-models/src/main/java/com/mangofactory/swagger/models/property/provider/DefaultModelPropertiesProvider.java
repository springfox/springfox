package com.mangofactory.swagger.models.property.provider;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.mangofactory.swagger.models.property.ModelProperty;
import com.mangofactory.swagger.models.property.bean.BeanModelPropertyProvider;
import com.mangofactory.swagger.models.property.constructor.ConstructorModelPropertyProvider;
import com.mangofactory.swagger.models.property.field.FieldModelPropertyProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.google.common.collect.Iterables.*;

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
    return FluentIterable
            .from(concat(fieldModelPropertyProvider.propertiesForSerialization(type),
                    beanModelPropertyProvider.propertiesForSerialization(type),
                    constructorModelPropertyProvider.propertiesForSerialization(type)))
            .filter(visibleProperties());
  }

  @Override
  public Iterable<? extends ModelProperty> propertiesForDeserialization(ResolvedType type) {
    return FluentIterable
            .from(concat(fieldModelPropertyProvider.propertiesForDeserialization(type),
                    beanModelPropertyProvider.propertiesForDeserialization(type),
                    constructorModelPropertyProvider.propertiesForDeserialization(type)))
            .filter(visibleProperties());
  }

  @Override
  public void setObjectMapper(ObjectMapper objectMapper) {
    fieldModelPropertyProvider.setObjectMapper(objectMapper);
    beanModelPropertyProvider.setObjectMapper(objectMapper);
    constructorModelPropertyProvider.setObjectMapper(objectMapper);
  }

  private Predicate<ModelProperty> visibleProperties() {
    return new Predicate<ModelProperty>() {
      @Override
      public boolean apply(ModelProperty input) {
        return !input.isHidden();
      }
    };
  }
}

