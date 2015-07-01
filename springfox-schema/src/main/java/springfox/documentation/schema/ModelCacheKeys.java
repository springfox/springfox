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
import com.fasterxml.classmate.TypeResolver;
import springfox.documentation.schema.property.ModelPropertiesKeyGenerator;
import springfox.documentation.schema.property.OptimizedModelPropertiesProvider;
import springfox.documentation.spi.schema.contexts.ModelContext;

public class ModelCacheKeys {
  private ModelCacheKeys() {
    throw new UnsupportedOperationException();
  }

  public static final String MODEL_CONTEXT_SPEL
      = "T(springfox.documentation.schema.ModelCacheKeys).modelContextKey(#modelContext)";
  public static String modelContextKey(ModelContext givenContext) {
    return new ModelContextKeyGenerator(new TypeResolver())
        .generate(DefaultModelProvider.class, null, givenContext).toString();
  }

  public static final String MODEL_PROPERTIES_SPEL
      = "T(springfox.documentation.schema.ModelCacheKeys).modelPropertiesKey(#type, #givenContext)";
  public static String modelPropertiesKey(ResolvedType type, ModelContext givenContext) {
    return new ModelPropertiesKeyGenerator(new TypeResolver())
        .generate(OptimizedModelPropertiesProvider.class, null, type, givenContext).toString();
  }
}
