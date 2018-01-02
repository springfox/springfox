/*
 *
 *  Copyright 2017 the original author or authors.
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

import java.util.Map;
import java.util.Set;

import com.google.common.base.Optional;

public interface UniqueTypeNameAdapter {

  /**
   * Provides information about models equality
   * @return a map with Models id
   */
  Map<Integer, Integer> getLinks();

  /**
   * Provides information about models with same types name
   * @param modelId - id of model
   * @return a set with Models id
   */
  Set<Integer> getSimilarTypes(final int modelId);

  /**
   * Returns type for the model
   * @param modelId - id of model
   * @return a set with Models id
   */
  Optional<String> getTypeName(int modelId);

  /**
   * Register model name to keep it unique
   * @param typeName - string representation of the models name
   * @param modelId - id of model
   */
  void registerType(String typeName, int modelId);

  /**
   * Sets equality of two models to make sure, that models will be treated as one
   * @param modelIdOf - id of the first model
   * @param modelIdTo - id of the second model
   */
  void setEqualityFor(int modelIdOf, int modelIdTo);

}