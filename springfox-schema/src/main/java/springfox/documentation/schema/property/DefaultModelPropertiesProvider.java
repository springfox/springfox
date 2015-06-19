/*
 *
 *  Copyright 2015 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package springfox.documentation.schema.property;

import com.fasterxml.classmate.ResolvedType;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import springfox.documentation.schema.ModelProperty;
import springfox.documentation.schema.configuration.ObjectMapperConfigured;
import springfox.documentation.schema.property.bean.BeanModelPropertyProvider;
import springfox.documentation.schema.property.constructor.ConstructorModelPropertyProvider;
import springfox.documentation.schema.property.field.FieldModelPropertyProvider;
import springfox.documentation.spi.schema.contexts.ModelContext;

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

