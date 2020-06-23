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

package springfox.documentation.schema;


import com.fasterxml.classmate.ResolvedType;

import springfox.documentation.annotations.Cacheable;
import springfox.documentation.spi.schema.contexts.ModelContext;

import java.util.Optional;

/**
 * @deprecated
 * @since 3.0 use {@link ModelSpecificationProvider} instead
 */
@Deprecated
public interface ModelProvider {
  @Cacheable(value = "models")
  Optional<Model> modelFor(ModelContext modelContext);

  java.util.Map<ResolvedType, Model> dependencies(ModelContext modelContext);
}
