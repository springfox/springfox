/*
 *
 *  Copyright 2015-2019 the original author or authors.
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

package springfox.documentation.spring.web.plugins

import com.fasterxml.classmate.TypeResolver
import spock.lang.Specification
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.Defaults
import springfox.documentation.spi.service.contexts.DocumentationContextBuilder
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spring.web.mixins.ServicePluginsSupport
import springfox.documentation.spring.web.paths.DefaultPathProvider
import springfox.documentation.spring.web.readers.operation.ApiOperationReader

import static springfox.documentation.spi.service.contexts.Orderings.*

class DocumentationContextSpec extends Specification implements ServicePluginsSupport {
  DocumentationContextBuilder contextBuilder
  Docket plugin
  ApiOperationReader operationReader
  private defaultConfiguration

  def setup() {
    defaultConfiguration = new DefaultConfiguration(
        new Defaults(),
        new TypeResolver(),
        new DummyPathProvider())

    contextBuilder = this.defaultConfiguration.create(DocumentationType.SWAGGER_12)
        .requestHandlers([])
        .operationOrdering(nickNameComparator())
    plugin = new Docket(DocumentationType.SWAGGER_12)
    operationReader = Mock(ApiOperationReader)
  }

  def documentationContext() {
    plugin.configure(contextBuilder)
  }

  def context() {
    OperationContext context = Mock()
    context.documentationContext >> documentationContext()
    context.consumes() >> []
    return context
  }

  class DummyPathProvider extends DefaultPathProvider {
  }
}
