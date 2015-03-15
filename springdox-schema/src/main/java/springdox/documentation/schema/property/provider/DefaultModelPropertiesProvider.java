package springdox.documentation.schema.property.provider;

import com.fasterxml.classmate.ResolvedType;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import springdox.documentation.schema.ModelProperty;
import springdox.documentation.schema.configuration.ObjectMapperConfigured;
import springdox.documentation.schema.property.bean.BeanModelPropertyProvider;
import springdox.documentation.schema.property.constructor.ConstructorModelPropertyProvider;
import springdox.documentation.schema.property.field.FieldModelPropertyProvider;
import springdox.documentation.spi.schema.contexts.ModelContext;

import java.util.List;

import static com.google.common.collect.Iterables.*;

@Component(value = "default")
public class DefaultModelPropertiesProvider implements ModelPropertiesProvider,
        ApplicationListener<ObjectMapperConfigured> {

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
  public void onApplicationEvent(ObjectMapperConfigured event) {
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

