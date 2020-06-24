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
import org.springframework.context.ApplicationListener;
import springfox.documentation.annotations.Cacheable;
import springfox.documentation.schema.PropertySpecification;
import springfox.documentation.schema.configuration.ObjectMapperConfigured;
import springfox.documentation.spi.schema.contexts.ModelContext;

import java.util.List;

public interface ModelPropertiesProvider extends ApplicationListener<ObjectMapperConfigured> {
  @Cacheable(value = "modelProperties")
  @Deprecated
  /**
   * Use {@link ModelPropertiesProvider#propertySpecificationsFor(ResolvedType, ModelContext)}
   * @deprecated @since 3.0.0
   */
  List<springfox.documentation.schema.ModelProperty> propertiesFor(ResolvedType type, ModelContext givenContext);

  @Cacheable(value = "propertySpecifications")
  List<PropertySpecification> propertySpecificationsFor(
      ResolvedType propertiesHost,
      ModelContext context);
}
