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

package springfox.documentation.schema;

import java.lang.annotation.Annotation;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.stereotype.Component;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Optional;

import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelProjectionProviderPlugin;

@Component
public class ModelProjectionExtractor {
    
  private final TypeResolver typeResolver;
  private final PluginRegistry<ModelProjectionProviderPlugin, DocumentationType> modelProjectionProviders;

    
  @Autowired
  public ModelProjectionExtractor(
      TypeResolver typeResolver,
      @Qualifier("modelProjectionProviderRegistry")
      PluginRegistry<ModelProjectionProviderPlugin, DocumentationType> modelProjectionProviders) {

    this.typeResolver = typeResolver;
    this.modelProjectionProviders = modelProjectionProviders;
  }
  
  public Optional<ResolvedType> extractProjection(ResolvedType type, List<Annotation> annotations, DocumentationType documentationType) {
    ModelProjectionProviderPlugin selected =
            modelProjectionProviders.getPluginFor(documentationType, new JacksonJsonViewProjectionProvider());
    Optional<Class<?>> projection = selected.projectionFor(type, annotations);
    if (projection.isPresent()) {
      return Optional.of(typeResolver.resolve(projection.get()));
    }
    return Optional.absent();
  }
}
