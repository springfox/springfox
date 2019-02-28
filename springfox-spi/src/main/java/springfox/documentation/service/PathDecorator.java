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
package springfox.documentation.service;


import org.springframework.plugin.core.Plugin;
import springfox.documentation.annotations.Incubating;
import springfox.documentation.spi.service.contexts.DocumentationContext;
import springfox.documentation.spi.service.contexts.PathContext;

import java.util.function.Function;

/**
 * Path decorator is useful to create transformations from a given path based on
 * the RequestMappingContext. This is an experimental feature
 */
@Incubating("2.1.0")
public interface PathDecorator extends Plugin<DocumentationContext> {
  Function<String, String> decorator(PathContext context);
}
