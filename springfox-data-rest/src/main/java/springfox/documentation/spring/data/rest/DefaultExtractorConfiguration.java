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
package springfox.documentation.spring.data.rest;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

class DefaultExtractorConfiguration implements RequestHandlerExtractorConfiguration {
  private final List<EntityOperationsExtractor> defaultEntityExtractors  = Stream.of(
      new EntitySaveExtractor(),
      new EntityDeleteExtractor(),
      new EntityFindOneExtractor(),
      new EntityFindAllExtractor(),
      new EntitySearchExtractor(),
      new EntityAssociationsExtractor()
  ).collect(toList());


  private final List<EntityAssociationOperationsExtractor> defaultAssociationExtractors = Stream.of(
      new EntityAssociationSaveExtractor(),
      new EntityAssociationDeleteExtractor(),
      new EntityAssociationGetExtractor(),
      new EntityAssociationItemGetExtractor(),
      new EntityAssociationItemDeleteExtractor()
  ).collect(toList());

  @Override
  public List<EntityOperationsExtractor> getEntityExtractors() {
    return defaultEntityExtractors;
  }

  @Override
  public List<EntityAssociationOperationsExtractor> getAssociationExtractors() {
    return defaultAssociationExtractors;
  }
}
