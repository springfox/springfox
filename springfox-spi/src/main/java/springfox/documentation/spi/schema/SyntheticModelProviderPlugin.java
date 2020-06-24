/*
 *
 *  Copyright 2017-2019 the original author or authors.
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
package springfox.documentation.spi.schema;

import com.fasterxml.classmate.ResolvedType;
import org.springframework.plugin.core.Plugin;
import springfox.documentation.schema.ModelSpecification;
import springfox.documentation.schema.PropertySpecification;
import springfox.documentation.spi.schema.contexts.ModelContext;

import java.util.List;
import java.util.Set;

/**
 * Plugin to generate custom models
 *
 * @since 2.8.1
 */
@SuppressWarnings("deprecation")
public interface SyntheticModelProviderPlugin extends Plugin<ModelContext> {
  /**
   * Creates a synthetic model
   *
   * @param context - context to create the model from
   * @return model - when the plugin indicates it supports it, it must return a model
   */
  springfox.documentation.schema.Model create(ModelContext context);

  /**
   * Creates a synthetic model properties
   *
   * @param context - context to create the model properties from
   * @return model - when the plugin indicates it supports it, it must provide properties by name
   */
  List<springfox.documentation.schema.ModelProperty> properties(ModelContext context);


  /**
   * Creates a synthetic model
   *
   * @param context - context to create the model from
   * @return model - when the plugin indicates it supports it, it must return a model
   */
  ModelSpecification createModelSpecification(ModelContext context);

  /**
   * Creates a synthetic model properties
   *
   * @param context - context to create the model properties from
   * @return model - when the plugin indicates it supports it, it must provide properties by name
   */
  List<PropertySpecification> propertySpecifications(ModelContext context);

  /**
   * Creates a dependencies for the synthetic model
   *
   * @param context - context to create the model dependencies from
   * @return model - when the plugin indicates it supports it, it may return dependent model types.
   */
  Set<ResolvedType> dependencies(ModelContext context);
}
