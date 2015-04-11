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

package springfox.documentation.spi.service;

import org.springframework.plugin.core.Plugin;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.DocumentationContext;
import springfox.documentation.spi.service.contexts.DocumentationContextBuilder;

public interface DocumentationPlugin extends Plugin<DocumentationType> {
  /**
   * @return indicator to determine if the plugin is enabled
   */
  boolean isEnabled();

  DocumentationType getDocumentationType();

  /**
   * Creates a documentation context based on a given DocumentationContextBuilder
   *
   * @param builder - @see springfox.documentation.spi.service.contexts.DocumentationContextBuilder
   * @return context to use for building the documentation
   */
  DocumentationContext configure(DocumentationContextBuilder builder);

  /**
   * Gets the group name for the plugin. This is expected to be unique for each instance of the plugin
   * @return group the plugin belongs to
   */
  String getGroupName();
}

