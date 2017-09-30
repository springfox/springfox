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

package springfox.documentation.spi.service;

import java.lang.annotation.Annotation;
import java.util.List;

import org.springframework.plugin.core.Plugin;

import com.fasterxml.classmate.ResolvedType;
import com.google.common.base.Optional;

import springfox.documentation.spi.DocumentationType;

public interface ProjectionProviderPlugin extends Plugin<DocumentationType> {
  
  /**
   * Given a required annotation, if needed, to determine the active projection
   * @return optional of annotation class
   */
  Optional<Class<? extends Annotation>> getRequiredAnnotation();
  
  /**
   * Given a type provides a active projection for it
   * @param type - resolved type to provide projection for
   * @param requiredAnnotation - type annotation taken from controller, if needed 
   * @return resolved projection names
   */
  List<ResolvedType> projectionsFor(ResolvedType type, Optional<? extends Annotation> requiredAnnotation);
  
  /**
   * Given a type provides is it in a active projection
   * @param activeProjection - resolved projection type
   * @param type - type to apply projection
   * @param annotation - projections of type 
   * @return resolved projection names
   */
  boolean applyProjection(ResolvedType activeProjection, ResolvedType typeToApply, List<ResolvedType> typeProjections);
}
