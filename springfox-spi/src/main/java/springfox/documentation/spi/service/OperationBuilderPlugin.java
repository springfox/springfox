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
import springfox.documentation.spi.service.contexts.OperationContext;

public interface OperationBuilderPlugin extends Plugin<DocumentationType> {
  /**
   * Implement this method to override the Operation using the OperationBuilder available in the context
   *
   * @param context - context that can be used to override the parameter attributes
   * @see springfox.documentation.service.Operation
   * @see springfox.documentation.builders.OperationBuilder
   */
  void apply(OperationContext context);
}
