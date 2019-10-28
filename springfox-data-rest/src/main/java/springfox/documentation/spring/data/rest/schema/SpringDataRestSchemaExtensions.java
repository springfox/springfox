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
package springfox.documentation.spring.data.rest.schema;

import com.fasterxml.classmate.TypeResolver;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.server.LinkRelationProvider;
import springfox.documentation.schema.TypeNameExtractor;
import springfox.documentation.spi.schema.EnumTypeDeterminer;

@Configuration
public class SpringDataRestSchemaExtensions {

  @Bean
  public ResourcesModelProvider resourcesModelProvider(
      TypeResolver resolver,
      TypeNameExtractor typeNameExtractor,
      EnumTypeDeterminer enumTypeDeterminer) {
    return new ResourcesModelProvider(resolver, typeNameExtractor, enumTypeDeterminer);
  }

  @Bean
  public EmbeddedCollectionModelProvider embeddedCollectionProvider(
      TypeResolver resolver,
      @Qualifier("_relProvider")
          LinkRelationProvider relProvider,
      TypeNameExtractor typeNameExtractor,
      EnumTypeDeterminer enumTypeDeterminer) {
    return new EmbeddedCollectionModelProvider(resolver, relProvider, typeNameExtractor, enumTypeDeterminer);
  }
}
