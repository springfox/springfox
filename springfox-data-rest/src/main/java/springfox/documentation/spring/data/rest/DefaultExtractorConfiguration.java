/*
 *
 *  Copyright 2017-2018 the original author or authors.
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

import static com.google.common.collect.Lists.newArrayList;

class DefaultExtractorConfiguration implements RequestHandlerExtractorConfiguration {
  private final List<EntityOperationsExtractor> defaultEntityExtractors  = newArrayList(
      new EntitySaveExtractor(),
      new EntityDeleteExtractor(),
      new EntityFindOneExtractor(),
      new EntityFindAllExtractor(),
      new EntitySearchExtractor(),
      new EntityAssociationsExtractor()
  );


  private final List<EntityAssociationOperationsExtractor> defaultAssociationExtractors = newArrayList(
      new EntityAssociationSaveExtractor(),
      new EntityAssociationDeleteExtractor(),
      new EntityAssociationGetExtractor(),
      new EntityAssociationItemGetExtractor(),
      new EntityAssociationItemDeleteExtractor()
  );

  @Override
  public List<EntityOperationsExtractor> getEntityExtractors() {
    return defaultEntityExtractors;
  }

  @Override
  public List<EntityAssociationOperationsExtractor> getAssociationExtractors() {
    return defaultAssociationExtractors;
  }
}
