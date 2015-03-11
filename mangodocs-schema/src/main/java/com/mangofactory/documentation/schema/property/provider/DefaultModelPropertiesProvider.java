package com.mangofactory.documentation.schema.property.provider;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.mangofactory.documentation.schema.ModelProperty;
import com.mangofactory.documentation.schema.property.bean.BeanModelPropertyProvider;
import com.mangofactory.documentation.schema.property.constructor.ConstructorModelPropertyProvider;
import com.mangofactory.documentation.schema.property.field.FieldModelPropertyProvider;
import com.mangofactory.documentation.spi.schema.contexts.ModelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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
  public List<ModelProperty> propertiesFor(ResolvedType type, ModelContext givenContext) {
    return FluentIterable
            .from(concat(fieldModelPropertyProvider.propertiesFor(type, givenContext),
                    beanModelPropertyProvider.propertiesFor(type, givenContext),
                    constructorModelPropertyProvider.propertiesFor(type, givenContext)))
            .filter(visibleProperties())
            .toList();

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

