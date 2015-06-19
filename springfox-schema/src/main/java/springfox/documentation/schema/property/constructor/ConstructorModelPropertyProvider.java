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

package springfox.documentation.schema.property.constructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import springfox.documentation.schema.TypeNameExtractor;
import springfox.documentation.schema.plugins.SchemaPluginsManager;
import springfox.documentation.schema.property.BeanPropertyNamingStrategy;
import springfox.documentation.schema.property.field.FieldModelPropertyProvider;
import springfox.documentation.schema.property.field.FieldProvider;
import springfox.documentation.schema.property.ModelPropertiesProvider;

@Component
public class ConstructorModelPropertyProvider extends FieldModelPropertyProvider implements ModelPropertiesProvider {

  @Autowired
  public ConstructorModelPropertyProvider(
          FieldProvider fieldProvider,
          BeanPropertyNamingStrategy namingStrategy,
          SchemaPluginsManager schemaPluginsManager,
          TypeNameExtractor extractor) {

    super(fieldProvider, namingStrategy, schemaPluginsManager, extractor);
  }
}
